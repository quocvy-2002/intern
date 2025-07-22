package com.example.demo.service;


import com.example.demo.dto.request.CreatSpaceTypeRequest;
import com.example.demo.dto.request.UpdateSpaceTypeRequest;
import com.example.demo.dto.response.SpaceTypeResponse;
import com.example.demo.entity.SpaceType;
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

    public SpaceTypeResponse createSpaceType(CreatSpaceTypeRequest request) {
        SpaceType spaceType = spaceTypeMapper.toSpaceType(request);
        return spaceTypeMapper.toSpaceTypeResponse(spaceTypeRepository.save(spaceType));
    }

    public SpaceTypeResponse updateSpaceType(Integer id, UpdateSpaceTypeRequest request) {
        SpaceType existing = spaceTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));

        spaceTypeMapper.updateSpaceType(existing, request);
        return spaceTypeMapper.toSpaceTypeResponse(spaceTypeRepository.save(existing));
    }

    public void deleteSpaceType(Integer id) {
        SpaceType spaceType = spaceTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        spaceTypeRepository.delete(spaceType);
    }

    public List<SpaceTypeResponse> getAllSpaceTypes() {
        return spaceTypeRepository.findAll()
                .stream()
                .map(spaceTypeMapper::toSpaceTypeResponse)
                .collect(Collectors.toList());
    }

    public SpaceTypeResponse getSpaceType(Integer id) {
        SpaceType spaceType = spaceTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        return spaceTypeMapper.toSpaceTypeResponse(spaceType);
    }
}
