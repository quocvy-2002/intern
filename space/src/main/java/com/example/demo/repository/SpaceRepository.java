    package com.example.demo.repository;

    import com.example.demo.entity.Space;
    import org.springframework.data.jpa.repository.JpaRepository;


    import java.util.List;
    import java.util.Optional;

    public interface SpaceRepository extends JpaRepository<Space, String> {
        Optional<Space> findBySpaceId(Integer spaceId);
        List<Space> findByParentIdIsNull();
        List<Space> findAllByParentId(Integer spaceId);
        List<Space> findByParentId(Integer parentId);

    }
