package com.example.demo.service.excel;

import com.example.demo.model.dto.tree.species.SpeciesCreateDTO;
import com.example.demo.model.dto.tree.species.SpeciesDTO;
import com.example.demo.model.dto.tree.tree.TreeCreateDTO;
import com.example.demo.model.dto.tree.tree.TreeDTO;
import com.example.demo.model.dto.tree.tree.TreeMeasurementDTO;
import com.example.demo.model.dto.tree.zone.ZoneCreateDTO;
import com.example.demo.model.dto.tree.zone.ZoneDTO;
import com.example.demo.model.entity.tree.Measurement;
import com.example.demo.model.entity.tree.Species;
import com.example.demo.model.enums.HealthStatus;
import com.example.demo.model.enums.TreeLevel;
import com.example.demo.repository.tree.SpeciesRepository;
import com.example.demo.repository.tree.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExcelHelper {

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("d-M-yyyy")
    };

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    public List<SpeciesCreateDTO> excelToSpecies(MultipartFile file) {
        List<SpeciesCreateDTO> speciesList = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next();
            int rowNumber = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;
                try {
                    if (isEmptyRow(row)) continue;
                    SpeciesCreateDTO dto = SpeciesCreateDTO.builder()
                            .localName(getCellValueAsString(row.getCell(0)))
                            .scientificName(getCellValueAsString(row.getCell(1)))
                            .lai(getCellValueAsInt(row.getCell(2)))
                            .woodDensity(getCellValueAsBigDecimal(row.getCell(3)))
                            .coeffB1(getCellValueAsBigDecimal(row.getCell(4)))
                            .build();
                    if (dto.getScientificName() == null || dto.getScientificName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Scientific name is required at row " + rowNumber);
                    }
                    if (dto.getLocalName() == null || dto.getLocalName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Local name is required at row " + rowNumber);
                    }
                    speciesList.add(dto);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing row " + rowNumber + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }
        return speciesList;
    }

    public ByteArrayInputStream speciesToExcel(List<SpeciesDTO> speciesList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Species");
            Row header = sheet.createRow(0);
            String[] columns = {
                    "Scientific Name",
                    "Local Name",
                    "Wood Density",
                    "CoeffB0",
                    "CoeffB1",
                    "CoeffB2",
                    "Lai"
            };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }
            int rowIdx = 1;
            for (SpeciesDTO dto : speciesList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getScientificName());
                row.createCell(1).setCellValue(dto.getLocalName());
                if (dto.getWoodDensity() != null) {
                    row.createCell(2).setCellValue(dto.getWoodDensity().doubleValue());
                }
                if (dto.getCoeffB0() != null) {
                    row.createCell(3).setCellValue(dto.getCoeffB0().doubleValue());
                }
                if (dto.getCoeffB1() != null) {
                    row.createCell(4).setCellValue(dto.getCoeffB1().doubleValue());
                }
                if (dto.getCoeffB2() != null) {
                    row.createCell(5).setCellValue(dto.getCoeffB2().doubleValue());
                }
                row.createCell(6).setCellValue(dto.getLai());
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error writing Excel file: " + e.getMessage(), e);
        }
    }

    public List<ZoneCreateDTO> excelToZones(MultipartFile file) {
        List<ZoneCreateDTO> zones = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // Skip header row
            int rowNumber = 1;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNumber++;
                try {
                    if (isEmptyRow(currentRow)) continue;
                    ZoneCreateDTO zone = ZoneCreateDTO.builder()
                            .zoneName(getCellValueAsString(currentRow.getCell(0)))
                            .zoneType(getCellValueAsString(currentRow.getCell(1)))
                            .boundaryWkt(getCellValueAsString(currentRow.getCell(2)))
                            .area(getCellValueAsBigDecimal(currentRow.getCell(3)))
                            .nonGreenArea(getCellValueAsBigDecimal(currentRow.getCell(4)))
                            .build();
                    if (zone.getZoneName() == null || zone.getZoneName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Zone name is required at row " + rowNumber);
                    }
                    if (zone.getZoneType() == null || zone.getZoneType().trim().isEmpty()) {
                        throw new IllegalArgumentException("Zone type is required at row " + rowNumber);
                    }
                    if (zone.getBoundaryWkt() == null || zone.getBoundaryWkt().trim().isEmpty()) {
                        throw new IllegalArgumentException("Boundary WKT is required at row " + rowNumber);
                    }
                    if (zone.getArea() == null) {
                        throw new IllegalArgumentException("Area is required at row " + rowNumber);
                    }
                    if (zone.getNonGreenArea() == null) {
                        throw new IllegalArgumentException("NonGreenArea is required at row " + rowNumber);
                    }
                    zones.add(zone);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing row " + rowNumber + ": " + e.getMessage(), e);
                }
            }
            return zones;
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }
    }


    public ByteArrayInputStream zonesToExcel(List<ZoneDTO> zoneList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Zones");

            // Header row
            Row header = sheet.createRow(0);
            String[] columns = {
                    "Zone ID",
                    "Zone Name",
                    "Zone Type",
                    "Boundary WKT",
                    "Area",
                    "Non Green Area",
                    "Is Active",
                    "Created At",
                    "Updated At"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data rows
            int rowIdx = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (ZoneDTO dto : zoneList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getZoneId() != null ? dto.getZoneId().toString() : "");
                row.createCell(1).setCellValue(dto.getZoneName() != null ? dto.getZoneName() : "");
                row.createCell(2).setCellValue(dto.getZoneType() != null ? dto.getZoneType() : "");
                row.createCell(3).setCellValue(dto.getBoundaryWkt() != null ? dto.getBoundaryWkt() : "");

                if (dto.getArea() != null) {
                    row.createCell(4).setCellValue(dto.getArea().doubleValue());
                }
                if (dto.getNonGreenArea() != null) {
                    row.createCell(5).setCellValue(dto.getNonGreenArea().doubleValue());
                }
                row.createCell(6).setCellValue(dto.getIsActive() != null ? dto.getIsActive() : false);

                row.createCell(7).setCellValue(
                        dto.getCreatedAt() != null ? dto.getCreatedAt().format(dateFormatter) : "");
                row.createCell(8).setCellValue(
                        dto.getUpdatedAt() != null ? dto.getUpdatedAt().format(dateFormatter) : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error writing Excel file: " + e.getMessage(), e);
        }
    }


    public List<TreeCreateDTO> excelToTrees(MultipartFile file) {
        List<TreeCreateDTO> treeList = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next();
            int rowNumber = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;
                try {
                    if (isEmptyRow(row)) continue;
                    TreeCreateDTO dto = TreeCreateDTO.builder()
                            .localName(getCellValueAsString(row.getCell(0)))
                            .zoneName(getCellValueAsString(row.getCell(1)))
                            .longitude(getCellValueAsDouble(row.getCell(2)))  // Đúng: cột 2 = Longitude
                            .latitude(getCellValueAsDouble(row.getCell(3)))   // Đúng: cột 3 = Latitude
                            .imgUrl(getCellValueAsString(row.getCell(4)))
                            .plantedDate(parseDateSafely(row.getCell(5)))
                            .build();
                    if (dto.getLocalName() == null || dto.getLocalName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Species Local Name is required at row " + rowNumber);
                    }
                    if (dto.getZoneName() == null || dto.getZoneName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Zone Name is required at row " + rowNumber);
                    }

                    treeList.add(dto);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing row " + rowNumber + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }
        return treeList;
    }

    public List<TreeMeasurementDTO> excelToTreeMeasurements(MultipartFile file) {
        List<TreeMeasurementDTO> result = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) {
                rows.next(); // Skip header row
                System.out.println("Skipped header row");
            }

            int rowNumber = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;
                try {
                    if (isEmptyRow(row)) {
                        System.out.println("Skipping row " + rowNumber + ": Empty row");
                        continue;
                    }
                    LocalDateTime measuredAt = parseDateTimeSafely(row.getCell(0));
                    if (measuredAt == null) {
                        System.out.println("Skipping row " + rowNumber + ": Invalid or missing Collected_Date");
                        continue;
                    }
                    String zoneName = getCellValueAsString(row.getCell(1));
                    if (zoneName == null || zoneName.trim().isEmpty()) {
                        System.out.println("Skipping row " + rowNumber + ": Missing or empty Zone name");
                        continue;
                    }
                    String localName = getCellValueAsString(row.getCell(2));
                    if (localName == null || localName.trim().isEmpty()) {
                        System.out.println("Skipping row " + rowNumber + ": Missing or empty Local Name");
                        continue;
                    }
                    Double latitude = null, longitude = null;
                    String gpsValue = getCellValueAsString(row.getCell(4));
                    if (gpsValue == null || gpsValue.trim().isEmpty()) {
                        System.out.println("Skipping row " + rowNumber + ": Missing GPS coordinates");
                        continue;
                    }
                    try {
                        String[] coordinates = gpsValue.trim().split(",");
                        if (coordinates.length != 2) {
                            System.out.println("Skipping row " + rowNumber + ": Invalid GPS coordinates format: Expected 'latitude, longitude', got '" + gpsValue + "'");
                            continue;
                        }
                        latitude = Double.parseDouble(coordinates[0].trim());
                        longitude = Double.parseDouble(coordinates[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping row " + rowNumber + ": Invalid GPS coordinates format: '" + gpsValue + "'. Error: " + e.getMessage());
                        continue;
                    }
                    try {
                        validateCoordinates(longitude, latitude, rowNumber);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Skipping row " + rowNumber + ": Invalid coordinates: " + e.getMessage());
                        continue;
                    }
                    LocalDate plantedDate = null;
                    String plantedYearStr = getCellValueAsString(row.getCell(8));
                    if (plantedYearStr != null && !plantedYearStr.trim().isEmpty()) {
                        try {
                            int treeAge = Integer.parseInt(plantedYearStr.trim());
                            int plantedYear = LocalDate.now().getYear() - treeAge;
                            plantedDate = LocalDate.of(plantedYear,
                                    LocalDate.now().getMonthValue(),
                                    LocalDate.now().getDayOfMonth());
                        } catch (NumberFormatException e) {
                            System.out.println("Skipping row " + rowNumber + ": Invalid planted_Year format: '" + plantedYearStr + "'. Error: " + e.getMessage());
                            continue;
                        }
                    }
                    TreeCreateDTO tree = TreeCreateDTO.builder()
                            .localName(localName)
                            .zoneName(zoneName)
                            .latitude(latitude)
                            .longitude(longitude)
                            .imgUrl(getCellValueAsString(row.getCell(10))) // photo_url
                            .plantedDate(plantedDate)
                            .build();

                    BigDecimal heightM = getCellValueAsBigDecimal(row.getCell(5)); // height_m
                    BigDecimal girthCm = getCellValueAsBigDecimal(row.getCell(6)); // girth_cm
                    BigDecimal canopyDiameterM = getCellValueAsBigDecimal(row.getCell(7)); // canopy_diameter_m
                    HealthStatus healthStatus = parseHealthStatus(getCellValueAsString(row.getCell(9))); // health_status

                    TreeMeasurementDTO tm = TreeMeasurementDTO.builder()
                            .tree(tree)
                            .heightM(heightM)
                            .girthCm(girthCm)
                            .canopyDiameterM(canopyDiameterM)
                            .healthStatus(healthStatus)
                            .measuredAt(measuredAt)
                            .build();

                    result.add(tm);
                    System.out.println("Successfully processed row " + rowNumber + ": TreeMeasurementDTO created");

                } catch (Exception e) {
                    System.out.println("Skipping row " + rowNumber + ": Unexpected error: " + e.getMessage());
                }
            }
            System.out.println("Processed Excel file: " + rowNumber + " rows processed, " + result.size() + " TreeMeasurementDTOs created");
        } catch (Exception e) {
            System.out.println("Failed to parse Excel file: " + e.getMessage());
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
        return result;
    }


    private void validateCoordinates(Double longitude, Double latitude, int rowNumber) {
        if (longitude == null) {
            throw new IllegalArgumentException("Longitude is required at row " + rowNumber);
        }
        if (latitude == null) {
            throw new IllegalArgumentException("Latitude is required at row " + rowNumber);
        }

        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid longitude at row %d: %.6f. Longitude must be between -180 and 180 degrees",
                    rowNumber, longitude));
        }

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid latitude at row %d: %.6f. Latitude must be between -90 and 90 degrees",
                    rowNumber, latitude));
        }

        if (Math.abs(longitude) > 1000 || Math.abs(latitude) > 1000) {
            throw new IllegalArgumentException(String.format(
                    "Suspicious coordinate values at row %d: lng=%.6f, lat=%.6f. "
                            + "These values seem too large. Please check if coordinates are in degrees (not meters/kilometers).",
                    rowNumber, longitude, latitude));
        }
    }


    public Workbook exportTreesWithMeasurementsToExcel(List<TreeDTO> trees, List<Measurement> measurements, List<Species> species) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TreesWithMeasurements");
        Row header = sheet.createRow(0);
        String[] columns = {
                "Tree ID", "Code", "Species Name", "Zone Name", "Latitude", "Longitude",
                "Tree Level", "Planted Date", "Tree Created At",
                "LAI", "Girth (cm)", "Height (m)", "Leaf Area(m)",
                "Health Status", "Measured At", "Measurement Created At", "Photo URL"
        };
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        Map<UUID, TreeDTO> treeMap = trees.stream()
                .filter(t -> t.getTreeId() != null)
                .collect(Collectors.toMap(TreeDTO::getTreeId, t -> t));
        Map<UUID, Species> speciesMap = species.stream()
                .filter(s -> s.getSpeciesId() != null)
                .collect(Collectors.toMap(Species::getSpeciesId, s -> s));
        int rowIdx = 1;
        for (Measurement m : measurements) {
            if (m.getTree() == null || m.getTree().getTreeId() == null) continue;
            TreeDTO tree = treeMap.get(m.getTree().getTreeId());
            if (tree == null) continue;
            Double lai = 0.0;
            Species specie = m.getTree().getSpecies();
            UUID speciesId = specie.getSpeciesId();
            if (speciesId != null) {
                Species speciesObj = speciesMap.get(speciesId);
                if (speciesObj != null) {
                    lai = (double) speciesObj.getLai();
                }
            }
            Double leafArea = calculateLai(m.getCanopyDiameterM());
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(tree.getTreeId().toString());
            row.createCell(1).setCellValue(tree.getCode() != null ? tree.getCode() : "");
            row.createCell(2).setCellValue(tree.getLocalName() != null ? tree.getLocalName() : "");
            row.createCell(3).setCellValue(tree.getZoneName() != null ? tree.getZoneName() : "");
            row.createCell(4).setCellValue(tree.getLatitude());
            row.createCell(5).setCellValue(tree.getLongitude());
            row.createCell(6).setCellValue(tree.getPlantedDate() != null ? tree.getPlantedDate().toString() : "");
            row.createCell(7).setCellValue(tree.getCreatedAt() != null ? tree.getCreatedAt().toString() : "");
            row.createCell(8).setCellValue(lai);
            row.createCell(9).setCellValue(m.getGirthCm() != null ? m.getGirthCm().doubleValue() : 0);
            row.createCell(10).setCellValue(m.getHeightM() != null ? m.getHeightM().doubleValue() : 0);
            row.createCell(11).setCellValue(m.getCanopyDiameterM() != null ? leafArea : 0);
            row.createCell(12).setCellValue(m.getHealthStatus() != null ? m.getHealthStatus().name() : "");
            row.createCell(13).setCellValue(m.getMeasuredAt() != null ? m.getMeasuredAt().toString() : "");
            row.createCell(14).setCellValue(m.getCreatedAt() != null ? m.getCreatedAt().toString() : "");
            row.createCell(15).setCellValue(tree.getImgUrl() != null ? tree.getImgUrl() : "");
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }

    public Workbook exportMeasurementsToExcel(List<Measurement> measurements) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Measurements");
        Row header = sheet.createRow(0);
        String[] columns = {"Tree Code", "DBH (cm)", "Height (m)", "Canopy Diameter (m)", "Health Status", "Measured At", "Created At"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        int rowNum = 1;
        for (Measurement m : measurements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getTree() != null && m.getTree().getCode() != null ? m.getTree().getCode() : "");
            row.createCell(1).setCellValue(m.getGirthCm() != null ? m.getGirthCm().doubleValue() : 0);
            row.createCell(2).setCellValue(m.getHeightM() != null ? m.getHeightM().doubleValue() : 0);
            row.createCell(3).setCellValue(m.getCanopyDiameterM() != null ? m.getCanopyDiameterM().doubleValue() : 0);
            row.createCell(4).setCellValue(m.getHealthStatus() != null ? m.getHealthStatus().name() : "");
            row.createCell(5).setCellValue(m.getMeasuredAt() != null ? m.getMeasuredAt().toString() : "");
            row.createCell(6).setCellValue(m.getCreatedAt() != null ? m.getCreatedAt().toString() : "");
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }

    private LocalDate parseDateSafely(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toLocalDate();
                    } else {
                        double serial = cell.getNumericCellValue();
                        java.util.Date javaDate = DateUtil.getJavaDate(serial, false);
                        return javaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    }
                case STRING:
                    String dateStr = cell.getStringCellValue().trim();
                    if (dateStr.isEmpty()) return null;
                    return parseStringToDate(dateStr);
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                return cell.getLocalDateTimeCellValue().toLocalDate();
                            } else {
                                double serial = cell.getNumericCellValue();
                                java.util.Date javaDate = DateUtil.getJavaDate(serial, false);
                                return javaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            }
                        case STRING:
                            return parseStringToDate(cell.getStringCellValue().trim());
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            try {
                String cellValue = getCellValueAsString(cell);
                return parseStringToDate(cellValue);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private LocalDateTime parseDateTimeSafely(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            System.out.println("parseDateTimeSafely: Cell is null or blank, return null");
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        LocalDateTime ldt = cell.getLocalDateTimeCellValue();
                        System.out.println("parseDateTimeSafely: Parsed NUMERIC date-formatted to " + ldt);
                        return ldt;
                    } else {
                        // Sử dụng getJavaDate để tránh bug, convert sang LocalDateTime
                        double serial = cell.getNumericCellValue();
                        java.util.Date javaDate = DateUtil.getJavaDate(serial, false);  // false: 1900 epoch
                        LocalDateTime ldt = javaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                        System.out.println("parseDateTimeSafely: Parsed NUMERIC serial " + serial + " to " + ldt);
                        return ldt;
                    }
                case STRING:
                    String dateStr = cell.getStringCellValue().trim();
                    System.out.println("parseDateTimeSafely: Parsing STRING '" + dateStr + "'");
                    if (dateStr.isEmpty()) return null;
                    LocalDateTime ldt = parseStringToDateTime(dateStr);
                    System.out.println("parseDateTimeSafely: Parsed STRING to " + ldt);
                    return ldt;
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                return cell.getLocalDateTimeCellValue();
                            } else {
                                double serial = cell.getNumericCellValue();
                                java.util.Date javaDate = DateUtil.getJavaDate(serial, false);
                                return javaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                            }
                        case STRING:
                            String formulaStr = cell.getStringCellValue().trim();
                            if (formulaStr.isEmpty()) return null;
                            return parseStringToDateTime(formulaStr);
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            System.out.println("parseDateTimeSafely: Exception in main try: " + e.getMessage() + ", trying fallback");
            try {
                String cellValue = getCellValueAsString(cell);
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    LocalDateTime ldt = parseStringToDateTime(cellValue);
                    System.out.println("parseDateTimeSafely: Fallback parsed to " + ldt);
                    return ldt;
                }
            } catch (Exception ex) {
                System.out.println("parseDateTimeSafely: Fallback failed: " + ex.getMessage());
            }
            return null;  // Strict: return null để trigger exception
        }
    }

    private LocalDate parseStringToDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        dateStr = dateStr.trim();
        System.out.println("parseStringToDate: Trying to parse '" + dateStr + "'");
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                System.out.println("parseStringToDate: Success with " + formatter.toString() + " -> " + date);
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("parseStringToDate: Failed with " + formatter.toString() + ": " + e.getMessage());
                // Continue to next formatter
            }
        }
        System.out.println("parseStringToDate: All patterns failed for '" + dateStr + "'");
        return null;  // Không throw, return null để handle ở trên
    }

    private LocalDateTime parseStringToDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        dateTimeStr = dateTimeStr.trim();
        System.out.println("parseStringToDateTime: Trying to parse '" + dateTimeStr + "'");
        // Thử datetime patterns trước
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, formatter);
                System.out.println("parseStringToDateTime: Success with datetime " + formatter.toString() + " -> " + ldt);
                return ldt;
            } catch (DateTimeParseException ignored) {
                // Continue
            }
        }
        // Fallback to date + atStartOfDay
        try {
            LocalDate date = parseStringToDate(dateTimeStr);
            if (date != null) {
                LocalDateTime ldt = date.atStartOfDay();
                System.out.println("parseStringToDateTime: Fallback to date " + date + " -> " + ldt);
                return ldt;
            }
        } catch (Exception e) {
            // Ignore
        }
        System.out.println("parseStringToDateTime: All failed for '" + dateTimeStr + "'");
        return null;  // Strict null
    }

    private TreeLevel parseTreeLevel(String value) {
        if (value == null || value.trim().isEmpty()) {
            return TreeLevel.MEDIUM;
        }
        try {
            return TreeLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String upperValue = value.trim().toUpperCase();
            if (upperValue.contains("HIGH") || upperValue.contains("CAO")) {
                return TreeLevel.HIGH;
            } else if (upperValue.contains("LOW") || upperValue.contains("THẤP")) {
                return TreeLevel.LOW;
            } else {
                return TreeLevel.MEDIUM;
            }
        }
    }

    private HealthStatus parseHealthStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return HealthStatus.FAIR;
        }
        try {
            return HealthStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String upperValue = value.trim().toUpperCase();
            if (upperValue.contains("HEALTHY") || upperValue.contains("GOOD") || upperValue.contains("TỐT")) {
                return HealthStatus.HEALTHY;
            } else if (upperValue.contains("POOR") || upperValue.contains("BAD") || upperValue.contains("XẤU")) {
                return HealthStatus.POOR;
            } else {
                return HealthStatus.FAIR;
            }
        }
    }

    private int getCellValueAsInt(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return 0;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (strValue.isEmpty()) return 0;
                    return Integer.parseInt(strValue);
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            return (int) cell.getNumericCellValue();
                        case STRING:
                            String formulaStr = cell.getStringCellValue().trim();
                            if (formulaStr.isEmpty()) return 0;
                            return Integer.parseInt(formulaStr);
                        default:
                            return 0;
                    }
                default:
                    return 0;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = getCellValueAsString(cell);
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue().trim();
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                return cell.getLocalDateTimeCellValue().toString();
                            } else {
                                double numValue = cell.getNumericCellValue();
                                if (numValue == Math.floor(numValue)) {
                                    return String.valueOf((long) numValue);
                                } else {
                                    return String.valueOf(numValue);
                                }
                            }
                        case BOOLEAN:
                            return String.valueOf(cell.getBooleanCellValue());
                        default:
                            return cell.getCellFormula();
                    }
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return null;
        }
    }

    private String normalizeNumberString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }
        str = str.trim();
        if (str.contains(",") && str.contains(".")) {
            int lastDot = str.lastIndexOf('.');
            int lastComma = str.lastIndexOf(',');
            if (lastDot > lastComma) {
                str = str.replace(",", "");
            } else {
                str = str.substring(0, lastComma).replace(".", "") + "." + str.substring(lastComma + 1);
            }
        } else if (str.contains(",") && !str.contains(".")) {
            int commaCount = str.length() - str.replace(",", "").length();
            if (commaCount == 1) {
                int commaPos = str.indexOf(',');
                String afterComma = str.substring(commaPos + 1);
                if (afterComma.length() <= 6) {
                    str = str.replace(",", ".");
                } else {
                    str = str.replace(",", "");
                }
            } else {
                str = str.replace(",", "");
            }
        }
        return str;
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (strValue.isEmpty()) return null;
                    strValue = normalizeNumberString(strValue);
                    return Double.parseDouble(strValue);
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            return cell.getNumericCellValue();
                        case STRING:
                            String formulaStr = cell.getStringCellValue().trim();
                            if (formulaStr.isEmpty()) return null;
                            formulaStr = normalizeNumberString(formulaStr);
                            return Double.parseDouble(formulaStr);
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            String cellValue = "";
            try {
                cellValue = cell.toString();
            } catch (Exception ex) {
                cellValue = "unable to read cell";
            }
            throw new IllegalArgumentException(
                    String.format("Cannot parse number from cell value: '%s'. Error: %s",
                            cellValue, e.getMessage()), e);
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        Double doubleValue = getCellValueAsDouble(cell);
        return doubleValue != null ? BigDecimal.valueOf(doubleValue) : null;
    }

    private Double calculateLai(BigDecimal canopyDiameterM) {
        if (canopyDiameterM == null) return null;
        double radius = canopyDiameterM.doubleValue() / 2;
        return Math.PI * radius * radius;
    }
}