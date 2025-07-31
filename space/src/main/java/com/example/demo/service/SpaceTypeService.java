package com.example.demo.service;


import com.example.demo.model.dto.spacetype.SpaceTypeCreateDTO;
import com.example.demo.model.dto.spacetype.SpaceTypeDTO;
import com.example.demo.model.dto.spacetype.SpaceTypeUpdateDTO;
import com.example.demo.model.entity.SpaceType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.SpaceTypeMapper;
import com.example.demo.repository.SpaceTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpaceTypeService {

    SpaceTypeRepository spaceTypeRepository;
    SpaceTypeMapper spaceTypeMapper;

    public SpaceTypeDTO createSpaceType(SpaceTypeCreateDTO request) {
        SpaceType spaceType = spaceTypeMapper.toSpaceType(request);
        return spaceTypeMapper.toSpaceTypeDTO(spaceTypeRepository.save(spaceType));
    }

    public SpaceTypeDTO updateSpaceType(Integer id, SpaceTypeUpdateDTO request) {
        SpaceType existing = spaceTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));


        spaceTypeMapper.updateSpaceType(existing, request);
        return spaceTypeMapper.toSpaceTypeDTO(spaceTypeRepository.save(existing));
    }

    public void deleteSpaceType(Integer id) {
        SpaceType spaceType = spaceTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        spaceTypeRepository.delete(spaceType);
    }

    public List<SpaceTypeDTO> getAllSpaceTypes() {
        return spaceTypeRepository.findAll()
                .stream()
                .map(spaceTypeMapper::toSpaceTypeDTO)
                .collect(Collectors.toList());
    }

    public SpaceTypeDTO getSpaceType(Integer id) {
        SpaceType spaceType = spaceTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        return spaceTypeMapper.toSpaceTypeDTO(spaceType);
    }
}
