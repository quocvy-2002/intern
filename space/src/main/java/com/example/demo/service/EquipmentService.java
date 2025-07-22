package com.example.demo.service;

import com.example.demo.dto.request.CreateEquipmentRequest;
import com.example.demo.dto.request.UpdateEquipmentRequest;
import com.example.demo.dto.response.EquipmentResponse;
import com.example.demo.entity.Equipment;
import com.example.demo.entity.Space;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentMapper;
import com.example.demo.repository.EquipmentRepository;
import com.example.demo.repository.SpaceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentService {
    EquipmentRepository equipmentRepository;
    EquipmentMapper equipmentMapper;
    SpaceRepository spaceRepository;

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
}
