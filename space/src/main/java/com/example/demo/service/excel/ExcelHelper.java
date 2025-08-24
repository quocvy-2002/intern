package com.example.demo.service.excel;

import com.example.demo.model.dto.tree.species.SpeciesCreateDTO;
import com.example.demo.model.dto.tree.species.SpeciesDTO;
import com.example.demo.model.dto.tree.tree.TreeCreateDTO;
import com.example.demo.model.dto.tree.tree.TreeDTO;
import com.example.demo.model.dto.tree.tree.TreeMeasurementDTO;
import com.example.demo.model.dto.tree.zone.ZoneCreateDTO;
import com.example.demo.model.entity.tree.Measurement;
import com.example.demo.model.enums.HealthStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExcelHelper {

    // ============================
    // SPECIES OPERATIONS
    // ============================

    public List<SpeciesCreateDTO> excelToSpecies(MultipartFile file) {
        List<SpeciesCreateDTO> speciesList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua header
            if (rows.hasNext()) rows.next();

            int rowNumber = 1; // Để tracking lỗi
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;

                try {
                    if (isEmptyRow(row)) continue;

                    SpeciesCreateDTO dto = new SpeciesCreateDTO();
                    dto.setScientificName(getCellValueAsString(row.getCell(0)));
                    dto.setLocalName(getCellValueAsString(row.getCell(1)));
                    dto.setWoodDensity(getCellValueAsBigDecimal(row.getCell(2)));
                    dto.setCoeffB0(getCellValueAsBigDecimal(row.getCell(3)));
                    dto.setCoeffB1(getCellValueAsBigDecimal(row.getCell(4)));
                    dto.setCoeffB2(getCellValueAsBigDecimal(row.getCell(5)));

                    // Validate required fields
                    if (dto.getScientificName() == null || dto.getScientificName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Scientific name is required at row " + rowNumber);
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

            // Create header
            Row header = sheet.createRow(0);
            String[] columns = {"Scientific Name", "Local Name", "Wood Density", "CoeffB0", "CoeffB1", "CoeffB2"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Create data rows
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
            }

            // Auto-size columns
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

    // ============================
    // ZONE OPERATIONS
    // ============================

    public List<ZoneCreateDTO> excelToZones(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ZoneCreateDTO> zones = new ArrayList<>();

            // Bỏ qua header
            if (rows.hasNext()) rows.next();

            int rowNumber = 1;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNumber++;

                try {
                    if (isEmptyRow(currentRow)) continue;

                    ZoneCreateDTO zone = new ZoneCreateDTO();
                    zone.setZoneName(getCellValueAsString(currentRow.getCell(0)));
                    zone.setZoneType(getCellValueAsString(currentRow.getCell(1)));
                    zone.setBoundaryWkt(getCellValueAsString(currentRow.getCell(2)));

                    // Validate required fields
                    if (zone.getZoneName() == null || zone.getZoneName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Zone name is required at row " + rowNumber);
                    }
                    if (zone.getBoundaryWkt() == null || zone.getBoundaryWkt().trim().isEmpty()) {
                        throw new IllegalArgumentException("Boundary WKT is required at row " + rowNumber);
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

    // ============================
    // TREE OPERATIONS
    // ============================

    public List<TreeCreateDTO> excelToTrees(MultipartFile file) {
        List<TreeCreateDTO> treeList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua header
            if (rows.hasNext()) rows.next();

            int rowNumber = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;

                try {
                    if (isEmptyRow(row)) continue;

                    TreeCreateDTO dto = TreeCreateDTO.builder()
                            .speciesId(UUID.fromString(getCellValueAsString(row.getCell(0))))
                            .zoneId(UUID.fromString(getCellValueAsString(row.getCell(1))))
                            .latitude(getCellValueAsDouble(row.getCell(2)))
                            .longitude(getCellValueAsDouble(row.getCell(3)))
                            .plantedDate(row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK
                                    ? row.getCell(4).getLocalDateTimeCellValue().toLocalDate()
                                    : null)
                            .build();

                    // Validate required fields
                    if (dto.getSpeciesId() == null) {
                        throw new IllegalArgumentException("Species ID is required at row " + rowNumber);
                    }
                    if (dto.getZoneId() == null) {
                        throw new IllegalArgumentException("Zone ID is required at row " + rowNumber);
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

    public Workbook exportTreesToExcel(List<TreeDTO> trees) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Trees");

        // Header
        Row header = sheet.createRow(0);
        String[] columns = {"Tree ID", "Code", "Species ID", "Zone ID", "Latitude", "Longitude", "Planted Date", "Created At"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (TreeDTO tree : trees) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(tree.getTreeId() != null ? tree.getTreeId().toString() : "");
            row.createCell(1).setCellValue(tree.getCode() != null ? tree.getCode() : "");
            row.createCell(2).setCellValue(tree.getSpeciesId() != null ? tree.getSpeciesId().toString() : "");
            row.createCell(3).setCellValue(tree.getZoneId() != null ? tree.getZoneId().toString() : "");
            row.createCell(4).setCellValue(tree.getLatitude());
            row.createCell(5).setCellValue(tree.getLongitude());
            row.createCell(6).setCellValue(tree.getPlantedDate() != null ? tree.getPlantedDate().toString() : "");
            row.createCell(7).setCellValue(tree.getCreatedAt() != null ? tree.getCreatedAt().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    // ============================
    // MEASUREMENT OPERATIONS
    // ============================

    public List<TreeMeasurementDTO> excelToTreeMeasurements(MultipartFile file) {
        List<TreeMeasurementDTO> result = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua header
            if (rows.hasNext()) rows.next();

            int rowNumber = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;

                try {
                    if (isEmptyRow(row)) continue;

                    TreeCreateDTO tree = TreeCreateDTO.builder()
                            .speciesId(UUID.fromString(getCellValueAsString(row.getCell(0))))
                            .zoneId(UUID.fromString(getCellValueAsString(row.getCell(1))))
                            .latitude(getCellValueAsDouble(row.getCell(2)))
                            .longitude(getCellValueAsDouble(row.getCell(3)))
                            .plantedDate(row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK
                                    ? row.getCell(4).getLocalDateTimeCellValue().toLocalDate()
                                    : null)
                            .build();

                    BigDecimal dbhCm = getCellValueAsBigDecimal(row.getCell(5));
                    BigDecimal heightM = getCellValueAsBigDecimal(row.getCell(6));
                    BigDecimal canopyDiameterM = getCellValueAsBigDecimal(row.getCell(7));
                    HealthStatus healthStatus = HealthStatus.valueOf(getCellValueAsString(row.getCell(8)));
                    LocalDateTime measuredAt = row.getCell(9) != null && row.getCell(9).getCellType() != CellType.BLANK
                            ? row.getCell(9).getLocalDateTimeCellValue()
                            : LocalDateTime.now();

                    TreeMeasurementDTO tm = TreeMeasurementDTO.builder()
                            .tree(tree)
                            .dbhCm(dbhCm)
                            .heightM(heightM)
                            .canopyDiameterM(canopyDiameterM)
                            .healthStatus(healthStatus)
                            .measuredAt(measuredAt)
                            .build();

                    result.add(tm);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing row " + rowNumber + ": " + e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        return result;
    }

    public Workbook exportTreesWithMeasurementsToExcel(List<TreeDTO> trees, List<Measurement> measurements) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TreesWithMeasurements");

        // Header
        Row header = sheet.createRow(0);
        String[] columns = {
                "Tree ID", "Code", "Species ID", "Zone ID", "Latitude", "Longitude", "Planted Date", "Tree Created At",
                "Measurement ID", "DBH (cm)", "Height (m)", "Canopy Diameter (m)", "Health Status", "Measured At", "Measurement Created At"
        };
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // Map treeId -> TreeDTO để dễ lookup
        Map<UUID, TreeDTO> treeMap = trees.stream()
                .filter(t -> t.getTreeId() != null)
                .collect(Collectors.toMap(TreeDTO::getTreeId, t -> t));

        // Data
        int rowIdx = 1;
        for (Measurement m : measurements) {
            if (m.getTree() == null || m.getTree().getTreeId() == null) continue;

            TreeDTO tree = treeMap.get(m.getTree().getTreeId());
            if (tree == null) continue;

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(tree.getTreeId().toString());
            row.createCell(1).setCellValue(tree.getCode() != null ? tree.getCode() : "");
            row.createCell(2).setCellValue(tree.getSpeciesId() != null ? tree.getSpeciesId().toString() : "");
            row.createCell(3).setCellValue(tree.getZoneId() != null ? tree.getZoneId().toString() : "");
            row.createCell(4).setCellValue(tree.getLatitude());
            row.createCell(5).setCellValue(tree.getLongitude());
            row.createCell(6).setCellValue(tree.getPlantedDate() != null ? tree.getPlantedDate().toString() : "");
            row.createCell(7).setCellValue(tree.getCreatedAt() != null ? tree.getCreatedAt().toString() : "");

            row.createCell(8).setCellValue(m.getMeasurementId() != null ? m.getMeasurementId().toString() : "");
            row.createCell(9).setCellValue(m.getDbhCm() != null ? m.getDbhCm().doubleValue() : 0);
            row.createCell(10).setCellValue(m.getHeightM() != null ? m.getHeightM().doubleValue() : 0);
            row.createCell(11).setCellValue(m.getCanopyDiameterM() != null ? m.getCanopyDiameterM().doubleValue() : 0);
            row.createCell(12).setCellValue(m.getHealthStatus() != null ? m.getHealthStatus().name() : "");
            row.createCell(13).setCellValue(m.getMeasuredAt() != null ? m.getMeasuredAt().toString() : "");
            row.createCell(14).setCellValue(m.getCreatedAt() != null ? m.getCreatedAt().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    public Workbook exportMeasurementsToExcel(List<Measurement> measurements) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Measurements");

        // Header
        Row header = sheet.createRow(0);
        String[] columns = {"Tree Code", "DBH (cm)", "Height (m)", "Canopy Diameter (m)", "Health Status", "Measured At", "Created At"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Measurement m : measurements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getTree() != null && m.getTree().getCode() != null ? m.getTree().getCode() : "");
            row.createCell(1).setCellValue(m.getDbhCm() != null ? m.getDbhCm().doubleValue() : 0);
            row.createCell(2).setCellValue(m.getHeightM() != null ? m.getHeightM().doubleValue() : 0);
            row.createCell(3).setCellValue(m.getCanopyDiameterM() != null ? m.getCanopyDiameterM().doubleValue() : 0);
            row.createCell(4).setCellValue(m.getHealthStatus() != null ? m.getHealthStatus().name() : "");
            row.createCell(5).setCellValue(m.getMeasuredAt() != null ? m.getMeasuredAt().toString() : "");
            row.createCell(6).setCellValue(m.getCreatedAt() != null ? m.getCreatedAt().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    public Workbook exportMeasurementsByTreeToExcel(List<Measurement> measurements) {
        return exportMeasurementsToExcel(measurements);
    }

    // ============================
    // UTILITY METHODS
    // ============================

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
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
                // Nếu là số nguyên, không hiển thị .0
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot convert cell value to number: " + cell.getStringCellValue());
                }
            default:
                throw new IllegalArgumentException("Unsupported cell type for numeric conversion");
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        Double value = getCellValueAsDouble(cell);
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}