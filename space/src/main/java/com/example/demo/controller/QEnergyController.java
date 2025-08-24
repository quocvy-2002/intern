package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.response.QEnergyDTO;
import com.example.demo.service.qenergy.QEnergyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/qenergy")
public class QEnergyController {
    QEnergyService qEnergyReportService;

    /**
     * Lấy báo cáo năng lượng theo ngày trong khoảng thời gian
     */
    @GetMapping("/daily-report")
    public ApiResponse<QEnergyDTO> getDailyReport(
            @RequestParam Integer spaceId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate.trim());
        LocalDate end = LocalDate.parse(endDate.trim());

        QEnergyDTO report = qEnergyReportService.getDailyEnergyReport(spaceId, start, end);
        return ApiResponse.<QEnergyDTO>builder()
                .result(report)
                .build();
    }
}
