package com.example.demo.service.tree;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.tree.ZoneMapper;
import com.example.demo.model.dto.tree.zone.ZoneCreateDTO;
import com.example.demo.model.dto.tree.zone.ZoneDTO;
import com.example.demo.model.dto.tree.zone.ZoneUpdateDTO;
import com.example.demo.model.entity.tree.Zone;
import com.example.demo.repository.tree.ZoneRepository;
import com.example.demo.service.excel.ExcelHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class ZoneService {
    ZoneRepository zoneRepository;
    ZoneMapper zoneMapper;
    ExcelHelper excelHelper;

    @Transactional
    public ZoneDTO createZone(ZoneCreateDTO request) {
        if (zoneRepository.existsByZoneName(request.getZoneName().trim())) {
            throw new AppException(ErrorCode.ZONE_ALREADY_EXISTS);
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
            throw new AppException(ErrorCode.INVALID_WKT_FORMAT);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public ZoneDTO updateZone(UUID zoneId, ZoneUpdateDTO request) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_EXISTS));

        if (request.getZoneName() != null &&
                !request.getZoneName().trim().equals(zone.getZoneName()) &&
                zoneRepository.existsByZoneNameAndZoneIdNot(request.getZoneName().trim(), zoneId)) {
            throw new AppException(ErrorCode.ZONE_ALREADY_EXISTS);
        }

        try {
            zoneMapper.updateZone(zone, request);
            return zoneMapper.toZoneDTO(zoneRepository.save(zone));
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_WKT_FORMAT);
        }
    }

    @Transactional
    public void deleteZone(UUID zoneId) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_EXISTS));
        zoneRepository.delete(zone);
    }

    @Transactional
    public void softDeleteZone(UUID zoneId) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_EXISTS));
        zone.setIsActive(false);
        zoneRepository.save(zone);
    }

    public ZoneDTO getZoneById(UUID zoneId) {
        Zone zone = zoneRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_EXISTS));
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
            throw new AppException(ErrorCode.EMPTY_REQUEST_LIST);
        }

        Set<String> zoneNames = requests.stream()
                .map(req -> req.getZoneName().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (zoneNames.size() != requests.size()) {
            throw new AppException(ErrorCode.DUPLICATE_ZONE_NAMES_IN_REQUEST);
        }

        List<String> existingNames = requests.stream()
                .filter(req -> zoneRepository.existsByZoneName(req.getZoneName().trim()))
                .map(ZoneCreateDTO::getZoneName)
                .collect(Collectors.toList());

        if (!existingNames.isEmpty()) {
            throw new AppException(ErrorCode.ZONE_ALREADY_EXISTS);
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
            throw new AppException(ErrorCode.INVALID_WKT_FORMAT);
        }
    }

    @Transactional
    public List<ZoneDTO> importZonesFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("application/vnd")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        try {
            List<ZoneCreateDTO> requests = excelHelper.excelToZones(file);
            if (requests.isEmpty()) {
                throw new AppException(ErrorCode.EMPTY_DATA_FILE);
            }
            return createZoneList(requests);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }
}