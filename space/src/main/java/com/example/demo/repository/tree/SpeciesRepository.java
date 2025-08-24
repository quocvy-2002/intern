package com.example.demo.repository.tree;

import com.example.demo.model.entity.tree.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpeciesRepository extends JpaRepository<Species, UUID> {
    @Query("SELECT s FROM Species s WHERE s.scientificName = :scientificName")
    Optional<Species> findByScientificName(@Param("scientificName")String scientificName);

    @Query("SELECT s FROM Species s WHERE s.speciesId = :speciesId")
    Optional<Species> findBySpeciesId(@Param("speciesId")UUID speciesId);
}
