package com.example.demo.service;

import com.example.demo.dto.request.CreatSpaceRequest;
import com.example.demo.dto.request.UpdateSpaceRequest;
import com.example.demo.dto.response.SpaceResponse;
import com.example.demo.dto.response.SpaceTypeResponse;
import com.example.demo.entity.Space;
import com.example.demo.entity.SpaceType;
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

    public SpaceResponse creatSpace(CreatSpaceRequest request) {
        Space space = spaceMapper.toSpace(request);
        Integer parentId = request.getParentId();
        if (parentId != null && parentId != 0) {
            Space parent = spaceRepository.findBySpaceId(parentId)
                    .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));
            space.setParent(parent);
        }
        Integer spaceTypeId = request.getSpaceTypeId();
        SpaceType spaceType = spaceTypeRepository.findById(spaceTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        space.setSpaceType(spaceType);

        spaceRepository.save(space);
        return spaceMapper.toSpaceResponse(space);
    }

    public List<SpaceResponse> getAllSpaces() {
        List<Space> rootSpaces = spaceRepository.findByParentIsNull();
        return rootSpaces.stream()
                .map(this::convertToResponseWithChildren)
                .collect(Collectors.toList());
    }

    public SpaceResponse getSpcae(Integer spaceId) {
        return spaceRepository.findBySpaceId(spaceId)
                .map(this::convertToResponseWithChildren)
                .orElseThrow(() -> new RuntimeException("Space not found with id: " + spaceId));
    }

    private SpaceResponse convertToResponseWithChildren(Space space) {
        List<Space> children = spaceRepository.findAllByParent(space);
        return SpaceResponse.builder()
                .spaceName(space.getSpaceName())
                .spaceType(SpaceTypeResponse.builder()
                        .spaceTypeName(space.getSpaceType().getSpaceTypeName())
                        .spaceTypeLevel(space.getSpaceType().getSpaceTypeLevel())
                        .build())
                .children(children.stream()
                        .map(this::convertToResponseWithChildren)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<Integer> findAllDescendantSpaceIds(Integer parentId) {
        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(parentId);

        while (!stack.isEmpty()) {
            Integer currentParentId = stack.pop();
            Space currentSpace = spaceRepository.findById(String.valueOf(currentParentId)).orElse(null);
            if (currentSpace == null) continue;

            List<Space> children = spaceRepository.findAllByParent(currentSpace);
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

    public SpaceResponse updateSpace(Integer spaceId, UpdateSpaceRequest request) {
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

        return SpaceResponse.builder()
                .spaceName(space.getSpaceName())
                .spaceType(SpaceTypeResponse.builder()
                        .spaceTypeName(space.getSpaceType().getSpaceTypeName())
                        .spaceTypeLevel(space.getSpaceType().getSpaceTypeLevel())
                        .build())
                .children(null)
                .build();
    }

}
