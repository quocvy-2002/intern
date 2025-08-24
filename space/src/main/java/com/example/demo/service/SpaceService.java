package com.example.demo.service;

import com.example.demo.model.dto.response.SpaceTypeResponse;
import com.example.demo.model.dto.space.SpaceCreateDTO;
import com.example.demo.model.dto.space.SpaceDTO;
import com.example.demo.model.dto.space.SpaceUpdateDTO;
import com.example.demo.model.entity.Space;
import com.example.demo.model.entity.SpaceType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.SpaceMapper;
import com.example.demo.repository.SpaceRepository;
import com.example.demo.repository.SpaceTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpaceService {

    SpaceMapper spaceMapper;
    SpaceRepository spaceRepository;
    SpaceTypeRepository spaceTypeRepository;

    public SpaceDTO creatSpace(SpaceCreateDTO request) {
        Space space = spaceMapper.toSpace(request);

        if (request.getParentId() != null && request.getParentId() != 0) {
            Space parent = spaceRepository.findBySpaceId(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));
            space.setParent(parent);
        }

        SpaceType spaceType = spaceTypeRepository.findBySpaceTypeId(request.getSpaceTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        space.setSpaceType(spaceType);
        space.setQEnergySiteId(request.getQEnergySiteId());
        spaceRepository.save(space);
        return convertToDTOWithChildren(space);
    }

    public List<SpaceDTO> getAllSpaces() {
        List<Space> rootSpaces = spaceRepository.findByParentIsNull();
        return rootSpaces.stream()
                .map(this::convertToDTOWithChildren)
                .collect(Collectors.toList());
    }

    public SpaceDTO getSpcae(Integer spaceId) {
        return spaceRepository.findBySpaceId(spaceId)
                .map(this::convertToDTOWithChildren)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));
    }

    private SpaceDTO convertToDTOWithChildren(Space space) {
        List<Space> children = spaceRepository.findAllByParent(space);

        return SpaceDTO.builder()
                .spaceId(space.getSpaceId())
                .spaceName(space.getSpaceName())
                .qEnergySiteId(space.getQEnergySiteId())
                .spaceType(SpaceTypeResponse.builder()
                        .spaceTypeId(space.getSpaceType().getSpaceTypeId())
                        .spaceTypeName(space.getSpaceType().getSpaceTypeName())
                        .spaceTypeLevel(space.getSpaceType().getSpaceTypeLevel())
                        .build())
                .children(children.stream()
                        .map(this::convertToDTOWithChildren)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<Integer> findAllDescendantSpaceIds(Integer parentId) {
        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(parentId);

        while (!stack.isEmpty()) {
            Integer currentId = stack.pop();
            Space parent = spaceRepository.findBySpaceId(currentId).orElse(null);
            if (parent == null) continue;

            List<Space> children = spaceRepository.findAllByParent(parent);
            for (Space child : children) {
                result.add(child.getSpaceId());
                stack.push(child.getSpaceId());
            }
        }

        return result;
    }

    public void deleteSubtree(Integer spaceId) {
        Space space = spaceRepository.findBySpaceId(spaceId).orElse(null);
        if (space == null) return;

        List<Integer> allToDelete = findAllDescendantSpaceIds(spaceId);
        allToDelete.add(spaceId);

        spaceRepository.deleteAllBySpaceIdIn(allToDelete);
    }

    public SpaceDTO updateSpace(Integer spaceId, SpaceUpdateDTO request) {
        Space space = spaceRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));

        spaceMapper.updateSpace(space, request);

        if (request.getParentId() != null) {
            Space parent = spaceRepository.findBySpaceId(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));
            space.setParent(parent);
        }

        if (request.getSpaceTypeId() != null) {
            SpaceType spaceType = spaceTypeRepository.findById(request.getSpaceTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
            space.setSpaceType(spaceType);
        }

        return convertToDTOWithChildren(space);
    }
}
