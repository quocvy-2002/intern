package com.example.demo.service;

import com.example.demo.dto.request.CreateEquipmentStatusRequest;
import com.example.demo.dto.request.UpdateEquipmentStatusRequest;
import com.example.demo.dto.response.EquipmentStatusResponse;
import com.example.demo.entity.EquipmentStatus;
import com.example.demo.entity.EquipmentType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentStatusMapper;
import com.example.demo.repository.EquipmentStatusRepository;
import com.example.demo.repository.EquipmentTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentStatusService {
    EquipmentStatusRepository equipmentStatusRepository;
    EquipmentStatusMapper equipmentStatusMapper;
    EquipmentTypeRepository equipmentTypeRepository;

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
}
