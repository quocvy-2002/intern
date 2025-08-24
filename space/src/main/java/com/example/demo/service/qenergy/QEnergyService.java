package com.example.demo.service.qenergy;

import com.example.demo.model.dto.response.DailyEnergyDTO;
import com.example.demo.model.dto.response.QEnergyDTO;
import com.example.demo.model.entity.QEnergy;
import com.example.demo.model.entity.Space;
import com.example.demo.repository.QEnergyRepository;
import com.example.demo.repository.SpaceRepository;
import com.example.demo.utils.QEnergyUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QEnergyService {

    QEnergyUtils qEnergyUtils;
    QEnergyRepository qEnergyRepository;
    SpaceRepository spaceRepository;

    private final ConcurrentHashMap<Integer, AtomicReference<BigDecimal>> energyCache = new ConcurrentHashMap<>();

    private static final BigDecimal KW_TO_KWH_PER_MIN = new BigDecimal("0.0166666667");

    // Chạy mỗi phút: chỉ cache năng lượng 1 phút
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Ho_Chi_Minh")
    public void cachePerMinuteEnergy() {
        List<Space> spaces = spaceRepository.findAll();

        spaces.forEach(space -> {
            Integer siteId = space.getQEnergySiteId();
            if (siteId == null) {
                System.err.println("[QEnergy] Skip (no siteId) space=" + space.getSpaceName());
                return;
            }

            try {
                // 1) Lấy công suất tức thời kW
                BigDecimal livePowerKW = qEnergyUtils.getCurrentEnergyConsumptionBySiteId(siteId);
                // 2) Tính kWh tiêu thụ trong 1 phút
                BigDecimal energyKWh = livePowerKW.multiply(KW_TO_KWH_PER_MIN)
                        .setScale(7, RoundingMode.HALF_UP);

                // 3) Cập nhật cache và lấy tổng hiện tại của Space
                AtomicReference<BigDecimal> ref = energyCache
                        .computeIfAbsent(space.getSpaceId(), k -> new AtomicReference<>(BigDecimal.ZERO));
                BigDecimal updatedTotalForSpace = ref.updateAndGet(prev -> prev.add(energyKWh));

                // 4) In ra log: energy phút này + tổng cache hiện tại của Space
                System.out.println("[QEnergy] Cached space=" + space.getSpaceName()
                        + " energy(min,kWh)=" + energyKWh
                        + " | total in cache=" + updatedTotalForSpace + " kWh");

            } catch (Exception e) {
                System.err.println("[QEnergy] Fetch fail space=" + space.getSpaceName() + " err=" + e.getMessage());
            }
        });
    }




    // Chạy mỗi 30 phút: flush cache vào DB
    @Scheduled(cron = "5 0,30 * * * *", zone = "Asia/Ho_Chi_Minh")

    public void flushEveryThirtyMinutes() {
        LocalDateTime now = LocalDateTime.now();

        spaceRepository.findAll().forEach(space -> {
            Integer siteId = space.getQEnergySiteId();
            if (siteId == null) return;

            try {
                AtomicReference<BigDecimal> ref = energyCache.computeIfAbsent(space.getSpaceId(),
                        k -> new AtomicReference<>(BigDecimal.ZERO));
                BigDecimal totalKWh = ref.getAndSet(BigDecimal.ZERO).setScale(7, RoundingMode.HALF_UP);

                BigDecimal livePowerKW = qEnergyUtils.getCurrentEnergyConsumptionBySiteId(siteId);
                BigDecimal currentKWhAtMark = livePowerKW.multiply(KW_TO_KWH_PER_MIN).setScale(7, RoundingMode.HALF_UP);

                QEnergy qEnergy = new QEnergy();
                qEnergy.setSpace(space);
                qEnergy.setCurrentEnergyConsumption(currentKWhAtMark);
                qEnergy.setTotalEnergyConsumption(totalKWh);
                qEnergy.setDate(now);

                qEnergyRepository.save(qEnergy);

                System.out.println("[QEnergy] FLUSHED space=" + space.getSpaceName()
                        + " total(30m,kWh)=" + totalKWh
                        + " currentAtMark(kWh)=" + currentKWhAtMark);
            } catch (Exception e) {
                System.err.println("[QEnergy] Flush fail space=" + space.getSpaceName() + " err=" + e.getMessage());
            }
        });
    }

    // Lấy báo cáo tổng năng lượng trong khoảng thời gian
    public QEnergyDTO getDailyEnergyReport(Integer spaceId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // include endDate

        List<QEnergy> qEnergies = qEnergyRepository.findBySpaceAndDateBetween(spaceId, startDateTime, endDateTime);

        BigDecimal totalEnergy = qEnergies.stream()
                .map(QEnergy::getTotalEnergyConsumption)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String dateLabel = startDate.equals(endDate)
                ? startDate.toString()
                : startDate + " to " + endDate;

        DailyEnergyDTO dailyEnergyDTO = DailyEnergyDTO.builder()
                .date(dateLabel)
                .totalEnergy(totalEnergy)
                .build();

        String spaceName = qEnergies.isEmpty() ? "Unknown" : qEnergies.get(0).getSpace().getSpaceName();

        return QEnergyDTO.builder()
                .spaceId(spaceId)
                .spaceName(spaceName)
                .dailyEnergyDTOS(List.of(dailyEnergyDTO))
                .build();
    }
}
