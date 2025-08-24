    package com.example.demo.service.tree;

    import com.example.demo.exception.AppException;
    import com.example.demo.exception.ErrorCode;
    import com.example.demo.mapper.tree.SpeciesMapper;
    import com.example.demo.model.dto.tree.species.SpeciesCreateDTO;
    import com.example.demo.model.dto.tree.species.SpeciesDTO;
    import com.example.demo.model.dto.tree.species.SpeciesUpdateDTO;
    import com.example.demo.model.entity.tree.Species;
    import com.example.demo.repository.tree.SpeciesRepository;
    import com.example.demo.service.excel.ExcelHelper;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.ByteArrayInputStream;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.UUID;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public class SpeciesService {
        SpeciesRepository speciesRepository;
        SpeciesMapper speciesMapper;
        ExcelHelper excelHelper;

        public SpeciesDTO createSpecies(SpeciesCreateDTO request){
            speciesRepository.findByScientificName(request.getScientificName())
                    .ifPresent(s -> { throw new AppException(ErrorCode.SPECIES_ALREADY_EXISTS); });

            Species species = speciesMapper.toSpecies(request);
            LocalDateTime now = LocalDateTime.now();
            species.setCreatedAt(now);
            return speciesMapper.toSpeciesDTO(speciesRepository.save(species));
        }

        public SpeciesDTO updateSpecies(UUID speciesId , SpeciesUpdateDTO request) {
            Species species = speciesRepository.findBySpeciesId(speciesId)
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIES_NOT_EXISTS));
            speciesMapper.updateSpecies(species, request);
            return speciesMapper.toSpeciesDTO(speciesRepository.save(species));
        }

        public void deleteSpecies(UUID speciesId) {
            Species species = speciesRepository.findBySpeciesId(speciesId)
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIES_NOT_EXISTS));
            speciesRepository.delete(species);
        }

        public SpeciesDTO getSpeciesById(UUID speciesId) {
            Species species = speciesRepository.findBySpeciesId(speciesId)
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIES_NOT_EXISTS));
            return speciesMapper.toSpeciesDTO(species);
        }

        public List<SpeciesDTO> getAllSpecies() {
            return speciesRepository.findAll()
                    .stream()
                    .map(speciesMapper::toSpeciesDTO)
                    .collect(Collectors.toList());
        }

        public List<SpeciesDTO> createSpeciesList(List<SpeciesCreateDTO> requests) {
            requests.forEach(req -> {
                speciesRepository.findByScientificName(req.getScientificName())
                        .ifPresent(s -> {
                            throw new AppException(ErrorCode.SPECIES_ALREADY_EXISTS);
                        });
            });

            List<Species> speciesList = requests.stream()
                    .map(speciesMapper::toSpecies)
                    .collect(Collectors.toList());

            return speciesRepository.saveAll(speciesList)
                    .stream()
                    .map(speciesMapper::toSpeciesDTO)
                    .collect(Collectors.toList());
        }

        public List<SpeciesDTO> importSpeciesFromExcel(MultipartFile file) {
            List<SpeciesCreateDTO> requests = excelHelper.excelToSpecies(file);
            return createSpeciesList(requests);
        }

        public ByteArrayInputStream exportSpeciesToExcel() {
            List<SpeciesDTO> speciesList = getAllSpecies();
            return excelHelper.speciesToExcel(speciesList);
        }

    }
