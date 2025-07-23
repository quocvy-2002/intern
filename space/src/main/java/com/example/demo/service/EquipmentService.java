package com.example.demo.service;

import com.example.demo.dto.request.CreateEquipmentRequest;
import com.example.demo.dto.request.UpdateEquipmentRequest;
import com.example.demo.dto.response.EquipmentResponse;
import com.example.demo.entity.Equipment;
import com.example.demo.entity.EquipmentStatusLog;
import com.example.demo.entity.EquipmentUsageHistory;
import com.example.demo.entity.Space;
import com.example.demo.enums.Status;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentMapper;
import com.example.demo.repository.EquipmentRepository;
import com.example.demo.repository.EquipmentStatusLogRepository;
import com.example.demo.repository.EquipmentUsageHistoryRepository;
import com.example.demo.repository.SpaceRepository;
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

    public EquipmentResponse createEquipment(CreateEquipmentRequest createEquipmentRequest) {
        Equipment equipment = equipmentMapper.toEquipment(createEquipmentRequest);
        equipmentRepository.save(equipment);
        return equipmentMapper.toEquipmentResponse(equipment);
    }
    public List<EquipmentResponse> getAllEquipments() {
        List<Equipment> equipments = equipmentRepository.findAll();
        return equipments.stream().map(equipmentMapper::toEquipmentResponse).collect(Collectors.toList());
    }
    public EquipmentResponse getEquipmentById(Integer equipmentId) {
        Equipment equipment = equipmentRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        return equipmentMapper.toEquipmentResponse(equipment);
    }
    public EquipmentResponse updateEquipment(Integer equipmentId, UpdateEquipmentRequest updateEquipmentRequest) {
        Equipment equipment = equipmentRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        equipmentMapper.updateEquipment(equipment, updateEquipmentRequest);
        return equipmentMapper.toEquipmentResponse(equipment);
    }
    public void deleteEquipment(Integer equipmentId) {
        equipmentRepository.deleteByEquipmentId(equipmentId);
    }
    public List<EquipmentResponse> getEquipmentsBySpaceId(Integer spaceId) {
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


        Optional<EquipmentStatusLog> lastLogOpt = equipmentStatusLogRepository
                .findTopByEquipmentOrderByTimestampDesc(equipment);

        if (lastLogOpt.isPresent()) {
            Status currentStatus = lastLogOpt.get().getStatus();
            if (currentStatus == status) {
                throw new AppException(ErrorCode.EQUIPMENT_ALREADY_IN_THIS_STATUS);
            }
        }

        LocalDateTime now = LocalDateTime.now();


        EquipmentStatusLog log = new EquipmentStatusLog();
        log.setEquipment(equipment);
        log.setTimestamp(now);
        log.setStatus(status);
        log.setPowerConsumptionKW(currentPowerKW);
        equipmentStatusLogRepository.save(log);

        if (status == Status.ON) {
            boolean hasOpenHistory = equipmentUsageHistoryRepository
                    .existsByEquipmentAndEndTimeIsNull(equipment);

            if (!hasOpenHistory) {
                EquipmentUsageHistory history = new EquipmentUsageHistory();
                history.setEquipment(equipment);
                history.setStartTime(now);
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
