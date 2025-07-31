package com.example.demo.service;

import com.example.demo.model.dto.equipment.EquipmentCreateDTO;
import com.example.demo.model.dto.equipment.EquipmentDTO;
import com.example.demo.model.dto.equipment.EquipmentUpdateDTO;
import com.example.demo.model.entity.*;
import com.example.demo.model.enums.Status;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentMapper;
import com.example.demo.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentService {
    EquipmentRepository equipmentRepository;
    EquipmentMapper equipmentMapper;
    SpaceRepository spaceRepository;
    EquipmentUsageHistoryRepository equipmentUsageHistoryRepository;
    EquipmentStatusLogRepository equipmentStatusLogRepository;
    EquipmentStatusRepository equipmentStatusRepository;
    EquipmentTypeRepository equipmentTypeRepository;
    public EquipmentDTO createEquipment(EquipmentCreateDTO createEquipmentRequest) {
        Equipment equipment = equipmentMapper.toEquipment(createEquipmentRequest);

        EquipmentType equipmentType = equipmentTypeRepository.findById(createEquipmentRequest.getEquipmentTypeId())
                .orElseThrow(() -> new RuntimeException("EquipmentType not found"));

        equipment.setEquipmentType(equipmentType);

        equipmentRepository.save(equipment);
        return equipmentMapper.toEquipmentResponse(equipment);
    }

    public List<EquipmentDTO> getAllEquipments() {
        List<Equipment> equipments = equipmentRepository.findAll();
        return equipments.stream().map(equipmentMapper::toEquipmentResponse).collect(Collectors.toList());
    }
    public EquipmentDTO getEquipmentById(Integer equipmentId) {
        Equipment equipment = equipmentRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        return equipmentMapper.toEquipmentResponse(equipment);
    }
    public EquipmentDTO updateEquipment(Integer equipmentId, EquipmentUpdateDTO updateEquipmentRequest) {
        Equipment equipment = equipmentRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        equipmentMapper.updateEquipment(equipment, updateEquipmentRequest);
        return equipmentMapper.toEquipmentResponse(equipment);
    }
    public void deleteEquipment(Integer equipmentId) {
        equipmentRepository.deleteByEquipmentId(equipmentId);
    }
    public List<EquipmentDTO> getEquipmentsBySpaceId(Integer spaceId) {
        Space space = spaceRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));
        List<Equipment> equipments = equipmentRepository.findBySpace_SpaceId(spaceId);
        return equipments.stream()
                .map(equipmentMapper::toEquipmentResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateStatus(Integer equipmentId, Status status, BigDecimal currentPowerKW) {
        if (currentPowerKW == null || currentPowerKW.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.INVALID_POWER_CONSUMPTION);
        }

        if (status == null) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        Equipment equipment = equipmentRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        // Fetch EquipmentType from Equipment (assuming Equipment has a reference to EquipmentType)
        EquipmentType equipmentType = equipment.getEquipmentType();
        if (equipmentType == null) {
            throw new AppException(ErrorCode.EQUIPMENT_TYPE_NOT_EXISTS);
        }

        // Map Status enum to EquipmentStatus entity
        EquipmentStatus equipmentStatus = equipmentStatusRepository
                .findByStatusNameAndEquipmentType(status.name(), equipmentType)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STATUS));

        Optional<EquipmentStatusLog> lastLogOpt = equipmentStatusLogRepository
                .findTopByEquipmentOrderByTimestampDesc(equipment);

        if (lastLogOpt.isPresent()) {
            EquipmentStatus currentStatus = lastLogOpt.get().getEquipmentStatus();
            if (currentStatus.getStatusName().equalsIgnoreCase(status.name())) {
                throw new AppException(ErrorCode.EQUIPMENT_ALREADY_IN_THIS_STATUS);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        EquipmentStatusLog log = EquipmentStatusLog.builder()
                .equipment(equipment)
                .timestamp(now)
                .equipmentStatus(equipmentStatus)
                .powerConsumptionKW(currentPowerKW)
                .build();
        equipmentStatusLogRepository.save(log);

        if (status == Status.ON) {
            boolean hasOpenHistory = equipmentUsageHistoryRepository
                    .existsByEquipmentAndEndTimeIsNull(equipment);

            if (!hasOpenHistory) {
                EquipmentUsageHistory history = EquipmentUsageHistory.builder()
                        .equipment(equipment)
                        .startTime(now)
                        .build();
                equipmentUsageHistoryRepository.save(history);
            }
        } else if (status == Status.OFF) {
            EquipmentUsageHistory latestHistory = equipmentUsageHistoryRepository
                    .findTopByEquipmentAndEndTimeIsNullOrderByStartTimeDesc(equipment)
                    .orElse(null);
            if (latestHistory != null) {
                latestHistory.setEndTime(now);
                equipmentUsageHistoryRepository.save(latestHistory);
            }
        }
    }


}
