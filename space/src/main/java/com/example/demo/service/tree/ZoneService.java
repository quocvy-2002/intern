package com.example.demo.service.tree;

import com.example.SmartBuildingBackend.exceptionManagement.ExceptionFactory;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.SystemError;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.ZoneError;
import com.example.SmartBuildingBackend.exceptionManagement.exceptions.AppException;
import com.example.SmartBuildingBackend.mapper.tree.ZoneMapper;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneCreateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneDTO;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneUpdateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.Zone;
import com.example.SmartBuildingBackend.repository.tree.ZoneRepository;
import com.example.SmartBuildingBackend.service.excel.ExcelHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class ZoneService {
    ZoneRepository zoneRepository;
    ZoneMapper zoneMapper;
    ExcelHelper excelHelper;
    ExceptionFactory exceptionFactory;

    @Transactional
    public ZoneDTO createZone(ZoneCreateDTO request) {
        if (zoneRepository.existsByZoneName(request.getZoneName().trim())) {
            throw exceptionFactory.createAlreadyExistsException("Zone", "ZoneName", request.getZoneName(),
                    ZoneError.ZONE_ALREADY_EXISTS);
        }
        try {
            Zone zone = zoneMapper.toZone(request);
            LocalDateTime now = LocalDateTime.now();
            zone.setCreatedAt(now);
            zone.setUpdatedAt(now);
            Zone savedZone = zoneRepository.save(zone);
            ZoneDTO result = zoneMapper.toZoneDTO(savedZone);
            return result;

        } catch (IllegalArgumentException e) {
            throw exceptionFactory.createCustomException("Zone", List.of("Operation"), List.of("createZone"),
                    ZoneError.INVALID_WKT_FORMAT);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public ZoneDTO updateZone(Long zoneId, ZoneUpdateDTO request) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Zone", zoneId, ZoneError.ZONE_NOT_FOUND));

        if (request.getZoneName() != null &&
                !request.getZoneName().trim().equals(zone.getZoneName()) &&
                zoneRepository.existsByZoneNameAndZoneIdNot(request.getZoneName().trim(), zoneId)) {
            throw exceptionFactory.createCustomException("Zone", List.of("ZoneName", "ZoneId"),
                    List.of(request.getZoneName(), zoneId),
                    ZoneError.ZONE_ALREADY_EXISTS);
        }

        try {
            zoneMapper.updateZone(zone, request);
            return zoneMapper.toZoneDTO(zoneRepository.save(zone));
        } catch (IllegalArgumentException e) {
            throw exceptionFactory.createCustomException("Zone", List.of("Operation"), List.of("updateZone"),
                    ZoneError.INVALID_WKT_FORMAT);
        }
    }

    @Transactional
    public void deleteZone(Long zoneId) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Zone", zoneId, ZoneError.ZONE_NOT_FOUND));
        zoneRepository.delete(zone);
    }

    @Transactional
    public void softDeleteZone(Long zoneId) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Zone", zoneId, ZoneError.ZONE_NOT_FOUND));
        zone.setIsActive(false);
        zoneRepository.save(zone);
    }

    public ZoneDTO getZoneById(Long zoneId) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Zone", zoneId, ZoneError.ZONE_NOT_FOUND));
        return zoneMapper.toZoneDTO(zone);
    }

    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAllByIsActiveTrue()
                .stream()
                .map(zoneMapper::toZoneDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ZoneDTO> createZoneList(List<ZoneCreateDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw exceptionFactory.createCustomException("Zone", List.of("Operation"), List.of("updateZone"),
                    SystemError.EMPTY_FILE);
        }

        Set<String> zoneNames = requests.stream()
                .map(req -> req.getZoneName().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (zoneNames.size() != requests.size()) {
            throw exceptionFactory.createCustomException("Zone", List.of("ZoneName", "CreateZoneList"),
                    List.of(zoneNames.size(), requests.size()),
                    ZoneError.DUPLICATE_ZONE_NAMES_IN_REQUEST);
        }

        List<String> existingNames = requests.stream()
                .filter(req -> zoneRepository.existsByZoneName(req.getZoneName().trim()))
                .map(ZoneCreateDTO::getZoneName)
                .collect(Collectors.toList());

        if (!existingNames.isEmpty()) {
            throw exceptionFactory.createAlreadyExistsException("Zone", "ZoneName", requests.get(0).getZoneName(),
                    ZoneError.ZONE_ALREADY_EXISTS);
        }

        try {
            List<Zone> zones = requests.stream()
                    .map(zoneMapper::toZone)
                    .collect(Collectors.toList());
            return zoneRepository.saveAll(zones)
                    .stream()
                    .map(zoneMapper::toZoneDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException(ZoneError.INVALID_WKT_FORMAT);
        }
    }

    @Transactional
    public List<ZoneDTO> importZonesFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw exceptionFactory.createCustomException("Zone", List.of("Operation", "FileName"),
                    List.of("importZonesFromExcel", file.getOriginalFilename()),
                    SystemError.EMPTY_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("application/vnd")) {
            throw exceptionFactory.createCustomException("Zone", List.of("Operation", "FileName"),
                    List.of("importZonesFromExcel", file.getOriginalFilename()), SystemError.INVALID_FILE_TYPE);
        }

        try {
            List<ZoneCreateDTO> requests = excelHelper.excelToZones(file);
            if (requests.isEmpty()) {
                throw exceptionFactory.createCustomException("Zone", List.of("Operation", "FileName"),
                        List.of("importZonesFromExcel", file.getOriginalFilename()), SystemError.EMPTY_DATA_FILE);
            }
            return createZoneList(requests);
        } catch (Exception e) {
            throw new AppException(SystemError.FILE_PROCESSING_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream exportZoneToExcel() {
        try {
            List<ZoneDTO> zoneDTOS = getAllZones();
            ByteArrayInputStream result = excelHelper.zonesToExcel(zoneDTOS);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}