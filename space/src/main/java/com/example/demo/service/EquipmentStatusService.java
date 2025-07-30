package com.example.demo.service;

import com.example.demo.dto.request.CreateEquipmentStatusRequest;
import com.example.demo.dto.request.StatusRequest;
import com.example.demo.dto.request.UpdateEquipmentStatusRequest;
import com.example.demo.dto.response.EquipmentLogResponse;
import com.example.demo.dto.response.EquipmentStatusResponse;
import com.example.demo.entity.*;
import com.example.demo.enums.Status;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentStatusMapper;
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
public class EquipmentStatusService {
    EquipmentStatusRepository equipmentStatusRepository;
    EquipmentStatusMapper equipmentStatusMapper;
    EquipmentTypeRepository equipmentTypeRepository;
    EquipmentStatusLogRepository equipmentStatusLogRepository;
    EquipmentRepository equipmentRepository;
    EquipmentUsageHistoryRepository equipmentUsageHistoryRepository;

    public EquipmentStatusResponse getEquipmentStatus(Integer statusId) {
        EquipmentStatus equipmentStatus = equipmentStatusRepository.findByStatusId(statusId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_STATUS_NOT_FOUND));
        equipmentStatusMapper.toEquipmentStatusResponse(equipmentStatus);
        return equipmentStatusMapper.toEquipmentStatusResponse(equipmentStatusRepository.save(equipmentStatus));
    }

    public List<EquipmentStatusResponse> getEquipmentStatusByEquiment(Integer equipmentTypeId) {
        List<EquipmentStatus> equipmentStatuses = equipmentStatusRepository.findByEquipmentType_EquipmentTypeId(equipmentTypeId);

        if (equipmentStatuses.isEmpty()) {
            throw new AppException(ErrorCode.EQUIPMENT_STATUS_NOT_FOUND);
        }

        return equipmentStatuses.stream()
                .map(equipmentStatusMapper::toEquipmentStatusResponse)
                .collect(Collectors.toList());
    }

    public List<EquipmentStatusResponse> getAllEquipmentStatuses() {
        List<EquipmentStatus> equipmentStatuses = equipmentStatusRepository.findAll();
        return equipmentStatuses.stream().map(equipmentStatusMapper::toEquipmentStatusResponse).collect(Collectors.toList());
    }

    public EquipmentStatusResponse createEquipmentStatus (CreateEquipmentStatusRequest request) {
        List<EquipmentStatus> equipmentStatuses = equipmentStatusRepository.findByEquipmentType_EquipmentTypeId(request.getEquipmentTypeId());
        for (EquipmentStatus equipmentStatus : equipmentStatuses) {
            if(equipmentStatus.getStatusName().equals(request.getStatusName())) {
                throw new AppException(ErrorCode.EQUIPMENT_STATUS_EXISTED);
            }
        }
        EquipmentStatus equipmentStatus = equipmentStatusMapper.toEquipmentStatus(request);
        return equipmentStatusMapper.toEquipmentStatusResponse(equipmentStatusRepository.save(equipmentStatus));
    }

    public EquipmentStatusResponse updateEquipmentStatus(Integer statusId, UpdateEquipmentStatusRequest request) {
        EquipmentStatus equipmentStatus = equipmentStatusRepository.findByStatusId(statusId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_STATUS_NOT_FOUND));
        equipmentStatusMapper.updateEquipmentStatus(equipmentStatus, request);
        return equipmentStatusMapper.toEquipmentStatusResponse(equipmentStatusRepository.save(equipmentStatus));
    }

    public EquipmentStatusResponse deleteEquipmentStatus(Integer statusId) {
        EquipmentStatus equipmentStatus = equipmentStatusRepository.findByStatusId(statusId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_STATUS_NOT_FOUND));
        equipmentStatusRepository.delete(equipmentStatus);
        return equipmentStatusMapper.toEquipmentStatusResponse(equipmentStatusRepository.save(equipmentStatus));
    }

        @Transactional
        public void updateStatusHistory(StatusRequest request) {
            if (request.getStatusName() == null || request.getStatusName().trim().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_STATUS);
            }

            EquipmentType equipmentType = equipmentTypeRepository.findByEquipmentTypeId(request.getEquipmentTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_TYPE_NOT_EXISTS));

            Equipment equipment = equipmentRepository.findByEquipmentId(request.getEquipmentId())
                    .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

            EquipmentStatus newStatus = equipmentStatusRepository
                    .findByStatusNameAndEquipmentType(request.getStatusName(), equipmentType)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_STATUS));

            Optional<EquipmentStatusLog> lastLogOpt = equipmentStatusLogRepository
                    .findTopByEquipmentOrderByTimestampDesc(equipment);

            if (lastLogOpt.isPresent()) {
                EquipmentStatus currentStatus = lastLogOpt.get().getEquipmentStatus();
                if (currentStatus.getStatusName().equalsIgnoreCase(request.getStatusName())) {
                    throw new AppException(ErrorCode.EQUIPMENT_ALREADY_IN_THIS_STATUS);
                }
            }

            LocalDateTime now = LocalDateTime.now();

            EquipmentStatusLog log = EquipmentStatusLog.builder()
                    .equipment(equipment)
                    .timestamp(now)
                    .equipmentStatus(newStatus)
                    .powerConsumptionKW(null)
                    .build();
            equipmentStatusLogRepository.save(log);
            if ("ON".equalsIgnoreCase(newStatus.getStatusName())) {
                boolean hasOpenHistory = equipmentUsageHistoryRepository
                        .existsByEquipmentAndEndTimeIsNull(equipment);
                if (!hasOpenHistory) {
                    EquipmentUsageHistory history = EquipmentUsageHistory.builder()
                            .equipment(equipment)
                            .startTime(now)
                            .endTime(null)
                            .totalPowerConsumption(null)
                            .build();
                    equipmentUsageHistoryRepository.save(history);
                }
            } else if ("OFF".equalsIgnoreCase(newStatus.getStatusName())) {
                EquipmentUsageHistory latestHistory = equipmentUsageHistoryRepository
                        .findTopByEquipmentAndEndTimeIsNullOrderByStartTimeDesc(equipment)
                        .orElse(null);
                if (latestHistory != null) {
                    latestHistory.setEndTime(now);
                    equipmentUsageHistoryRepository.save(latestHistory);
                }
            } else {
                boolean hasOpenHistory = equipmentUsageHistoryRepository
                        .existsByEquipmentAndEndTimeIsNull(equipment);
                if (!hasOpenHistory) {
                    throw new AppException(ErrorCode.NO_ACTIVE_USAGE_HISTORY);
                }
            }
        }

    public List<EquipmentLogResponse> getAllLogs() {
        return equipmentStatusLogRepository.findAllLogs();
    }

}
