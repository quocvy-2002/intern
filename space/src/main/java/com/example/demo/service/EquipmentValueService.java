package com.example.demo.service;

import com.example.demo.dto.request.CreateEquipmentValueRequest;
import com.example.demo.dto.request.UpdateEquipmentValueRequest;
import com.example.demo.dto.response.EquipmentValueResponse;
import com.example.demo.entity.EquipmentValue;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentValueMapper;
import com.example.demo.repository.EquipmentValueRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentValueService {
    EquipmentValueRepository equipmentValueRepository;
    EquipmentValueMapper equipmentValueMapper;

    public EquipmentValueResponse getEquipmentValueByEquipmentId(Integer equipmentValueId) {
        EquipmentValue equipmentValue = equipmentValueRepository.findByEquipmentValueId(equipmentValueId)
                .orElseThrow(()-> new AppException(ErrorCode.EQUIPMENT_VALUE_ALREADY_EXISTS));
        return equipmentValueMapper.toEquipmentValueResponse(equipmentValue);
    }

    public EquipmentValueResponse createEquipmentValue(CreateEquipmentValueRequest request) {
       EquipmentValue equipmentValue = equipmentValueMapper.toEquipmentValue(request);
       equipmentValueRepository.save(equipmentValue);
       return equipmentValueMapper.toEquipmentValueResponse(equipmentValue);
    }

    public EquipmentValueResponse updateEquipmentValue(Integer equipmentValueId ,UpdateEquipmentValueRequest request) {
        if (!equipmentValueRepository.existsByEquipmentValueId(equipmentValueId)) {
            throw new AppException(ErrorCode.EQUIPMENT_VALUE_NOT_FOUND);
        }

        EquipmentValue equipmentValue = equipmentValueRepository.findByEquipmentValueId(equipmentValueId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_VALUE_NOT_FOUND));
        equipmentValueRepository.save(equipmentValue);
        return equipmentValueMapper.toEquipmentValueResponse(equipmentValue);
    }

    public EquipmentValueResponse deleteEquipmentValue(Integer equipmentValueId) {
        if (!equipmentValueRepository.existsByEquipmentValueId(equipmentValueId)) {
            throw new AppException(ErrorCode.EQUIPMENT_VALUE_NOT_FOUND);
        }

        EquipmentValue equipmentValue = equipmentValueRepository.findByEquipmentValueId(equipmentValueId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_VALUE_NOT_FOUND));
        equipmentValueRepository.delete(equipmentValue);
        return equipmentValueMapper.toEquipmentValueResponse(equipmentValue);
    }

    public List<EquipmentValueResponse> getAll() {
        List<EquipmentValue> equipmentValues = equipmentValueRepository.findAll();
        return equipmentValues.stream()
                .map(equipmentValueMapper::toEquipmentValueResponse)
                .collect(Collectors.toList());
    }
}
