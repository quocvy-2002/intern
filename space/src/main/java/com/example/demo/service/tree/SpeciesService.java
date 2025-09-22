package com.example.demo.service.tree;

import com.example.SmartBuildingBackend.exceptionManagement.ExceptionFactory;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.SpeciesError;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.SystemError;
import com.example.SmartBuildingBackend.exceptionManagement.exceptions.AppException;
import com.example.SmartBuildingBackend.mapper.tree.SpeciesMapper;
import com.example.SmartBuildingBackend.model.dto.tree.species.SpeciesCreateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.species.SpeciesDTO;
import com.example.SmartBuildingBackend.model.dto.tree.species.SpeciesUpdateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.Species;
import com.example.SmartBuildingBackend.repository.tree.SpeciesRepository;
import com.example.SmartBuildingBackend.service.excel.ExcelHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpeciesService {
    SpeciesRepository speciesRepository;
    SpeciesMapper speciesMapper;
    ExcelHelper excelHelper;
    ExceptionFactory exceptionFactory;

    @Transactional
    public SpeciesDTO createSpecies(SpeciesCreateDTO request) {
        speciesRepository.findByScientificName(request.getScientificName())
                .ifPresent(s -> {
                    throw exceptionFactory.createAlreadyExistsException("Species", "ScientificName",
                            request.getScientificName(), SpeciesError.SPECIES_ALREADY_EXISTS);
                });

        Species species = speciesMapper.toSpecies(request);
        setAuditFields(species);
        return speciesMapper.toSpeciesDTO(speciesRepository.save(species));
    }

    @Transactional
    public SpeciesDTO updateSpecies(Long speciesId, SpeciesUpdateDTO request) {
        Species species = speciesRepository.findBySpeciesId(speciesId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Species", "speciesId",speciesId,SpeciesError.SPECIES_NOT_FOUND));

        speciesMapper.updateSpecies(species, request);
        return speciesMapper.toSpeciesDTO(speciesRepository.save(species));
    }

    @Transactional
    public void deleteSpecies(Long speciesId) {
        Species species = speciesRepository.findBySpeciesId(speciesId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Species", "speciesId",speciesId,SpeciesError.SPECIES_NOT_FOUND));
        speciesRepository.delete(species);
    }

    @Transactional(readOnly = true)
    public SpeciesDTO getSpeciesById(Long speciesId) {
        Species species = speciesRepository.findBySpeciesId(speciesId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Species", "speciesId",speciesId,SpeciesError.SPECIES_NOT_FOUND));
        return speciesMapper.toSpeciesDTO(species);
    }

    @Transactional(readOnly = true)
    public List<SpeciesDTO> getAllSpecies() {
        return speciesRepository.findAll()
                .stream()
                .map(speciesMapper::toSpeciesDTO)
                .toList();
    }

    @Transactional
    public List<SpeciesDTO> createSpeciesList(List<SpeciesCreateDTO> requests) {
        validateUniqueScientificNames(requests);
        LocalDateTime now = LocalDateTime.now();
        List<Species> speciesList = requests.stream()
                .map(req -> {
                    Species species = speciesMapper.toSpecies(req);
                    species.setCreatedAt(now);
                    return species;
                })
                .toList();

        return speciesRepository.saveAll(speciesList)
                .stream()
                .map(speciesMapper::toSpeciesDTO)
                .toList();
    }

    @Transactional
    public List<SpeciesDTO> importSpeciesFromExcel(MultipartFile file) {
        log.info("Importing species from Excel file: {}", file.getOriginalFilename());

        try {
            List<SpeciesCreateDTO> requests = excelHelper.excelToSpecies(file);

            if (requests.isEmpty()) {
                log.warn("No data found in Excel file");
                throw exceptionFactory.createCustomException("Species", List.of("FileName"),
                        List.of(file.getOriginalFilename()), SystemError.EMPTY_IMPORT_DATA);
            }

            List<SpeciesDTO> result = createSpeciesList(requests);
            log.info("Successfully imported {} species from Excel", result.size());

            return result;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error importing species from Excel file: {}", file.getOriginalFilename(), e);
            throw exceptionFactory.createCustomException("Species", List.of("FileName"),
                    List.of(file.getOriginalFilename()), SystemError.IMPORT_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream exportSpeciesToExcel() {
        log.info("Exporting species to Excel");

        try {
            List<SpeciesDTO> speciesList = getAllSpecies();
            ByteArrayInputStream result = excelHelper.speciesToExcel(speciesList);

            log.info("Successfully exported {} species to Excel", speciesList.size());
            return result;
        } catch (Exception e) {
            log.error("Error exporting species to Excel", e);
            throw exceptionFactory.createCustomException("Species", List.of("Operation"),
                    List.of("exportSpeciesToExcel"), SystemError.EXPORT_FAILED);
        }
    }

    private void validateUniqueScientificNames(List<SpeciesCreateDTO> requests) {
        Set<String> scientificNames = requests.stream()
                .map(SpeciesCreateDTO::getScientificName)
                .collect(Collectors.toSet());

        List<String> existingNames = speciesRepository.findScientificNamesByNames(scientificNames);

        if (!existingNames.isEmpty()) {
            throw exceptionFactory.createAlreadyExistsException("Species", "ScientificName",
                    scientificNames, SpeciesError.SPECIES_ALREADY_EXISTS);
        }
    }

    private void setAuditFields(Species species) {
        LocalDateTime now = LocalDateTime.now();
        species.setCreatedAt(now);
    }
}