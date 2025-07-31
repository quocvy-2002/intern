package com.example.demo.service;

import com.example.demo.model.dto.equipmenttype.ETypeCreateDTO;
import com.example.demo.model.dto.equipmenttype.ETypeDTO;
import com.example.demo.model.dto.equipmenttype.ETypeUpdateDTO;
import com.example.demo.model.entity.EquipmentType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.EquipmentTypeMapper;
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
public class EquipmentTypeService {
    EquipmentTypeRepository equipmentTypeRepository;
    EquipmentTypeMapper equipmentTypeMapper;

    public ETypeDTO createEquipmentType(ETypeCreateDTO request) {
        if (equipmentTypeRepository.findByEquipmentTypeName(request.getEquipmentTypeName()).isPresent()) {
            throw new AppException(ErrorCode.EQUIPMENT_TYPE_EXISTS);
        }
        EquipmentType saved = equipmentTypeRepository.save(equipmentTypeMapper.toEquipmentType(request));
        return equipmentTypeMapper.toEquipmentTypeResponse(saved);
    }


    public ETypeDTO getEquipmentById(Integer equipmentTypeId){
        EquipmentType equipmentType = equipmentTypeRepository.findByEquipmentTypeId(equipmentTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_TYPE_EXISTS));
        return equipmentTypeMapper.toEquipmentTypeResponse(equipmentType);
    }

    public List<ETypeDTO> getAllEquipmentTypes(){
        List<EquipmentType> equipmentTypes = equipmentTypeRepository.findAll();
        return equipmentTypes.stream().map(equipmentTypeMapper::toEquipmentTypeResponse).collect(Collectors.toList());
    }

    public ETypeDTO updateEquipmentType(Integer equipmentTypeId, ETypeUpdateDTO request){
        EquipmentType equipmentType = equipmentTypeRepository.findByEquipmentTypeId(equipmentTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_TYPE_EXISTS));
       equipmentTypeMapper.updateEquipmentType(equipmentType,request);
       return equipmentTypeMapper.toEquipmentTypeResponse(equipmentTypeRepository.save(equipmentType));
    }
}
