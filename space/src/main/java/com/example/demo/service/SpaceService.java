package com.example.demo.service;

import com.example.demo.dto.request.CreatSpaceRequest;
import com.example.demo.dto.request.UpdateSpaceRequest;
import com.example.demo.dto.response.SpaceResponse;
import com.example.demo.entity.Space;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.SpaceMapper;
import com.example.demo.repository.SpaceRepository;
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

    // Tạo Space mới
    public SpaceResponse creatSpace(CreatSpaceRequest request) {
        Space space = spaceMapper.toSpace(request);
        Integer parentId = request.getParentId();

        if (parentId != null && parentId != 0) {
            space.setParentId(parentId);
        } else {
            space.setParentId(null);
        }

        spaceRepository.save(space);
        return spaceMapper.toSpaceResponse(space);
    }





        public List<SpaceResponse> getAllSpaces() {
            List<Space> rootSpaces = spaceRepository.findByParentIdIsNull();
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
            List<Space> children = spaceRepository.findAllByParentId(space.getSpaceId());

            return SpaceResponse.builder()
                    .spaceName(space.getSpaceName())
                    .spaceTypeName(space.getSpaceTypeName())
                    .spaceTypeLevel(space.getSpaceTypeLevel())
                    .children(children.stream()
                            .map(this::convertToResponseWithChildren)
                            .collect(Collectors.toList()))
                    .build();
        }






    private SpaceResponse buildSpaceTree(Space space, Map<Integer, List<Space>> parentIdMap) {
        List<SpaceResponse> children = new ArrayList<>();
        List<Space> childSpaces = parentIdMap.get(space.getSpaceId());

        if (childSpaces != null) {
            for (Space child : childSpaces) {
                children.add(buildSpaceTree(child, parentIdMap));
            }
        }

        return SpaceResponse.builder()
                .spaceName(space.getSpaceName())
                .spaceTypeName(space.getSpaceTypeName())
                .spaceTypeLevel(space.getSpaceTypeLevel())
                .children(children)
                .build();
    }

    // Tìm tất cả các con cháu của 1 Space (trả về danh sách ID)
    public List<String> findAllDescendantSpaceIds(Integer parentId) {
        List<String> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(parentId);

        while (!stack.isEmpty()) {
            Integer currentParentId = stack.pop();
            List<Space> children = spaceRepository.findByParentId(currentParentId);
            for (Space child : children) {
                result.add(String.valueOf(child.getSpaceId()));
                if (child.getSpaceId() != null) {
                    stack.push(child.getSpaceId());
                }
            }
        }

        return result;
    }


    public void deleteSubtree(String spaceId) {
        Space space = spaceRepository.findById(spaceId).orElse(null);
        if (space == null) return;

        List<String> allToDelete = new ArrayList<>();
        try {
            Integer id = Integer.parseInt(spaceId);
            allToDelete = findAllDescendantSpaceIds(id);
        } catch (NumberFormatException ignored) {}

        allToDelete.add(spaceId); // thêm chính nó
        spaceRepository.deleteAllById(allToDelete);
    }

    // Cập nhật Space
    public SpaceResponse updateSpace(Integer spaceId, UpdateSpaceRequest request) {
        Space space = spaceRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_NOT_FOUND));
        spaceMapper.updateSpace(space, request);
        return spaceMapper.toSpaceResponse(space);
    }
}
