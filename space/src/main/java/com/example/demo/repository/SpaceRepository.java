    package com.example.demo.repository;

    import com.example.demo.entity.Space;
    import com.example.demo.entity.SpaceType;
    import org.springframework.data.jpa.repository.JpaRepository;


    import java.util.List;
    import java.util.Optional;

    public interface SpaceRepository extends JpaRepository<Space, String> {
        List<Space> findByParentIsNull();
        List<Space> findAllByParent(Space parent);
        Optional<Space> findBySpaceId(Integer spaceIds);

        void deleteAllBySpaceIdIn(List<Integer> allToDelete);
    }
