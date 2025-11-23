package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueMonthlyDetailResponseDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueResponseDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueYearlyResponseDTO;
import apap.ti._5.tour_package_2306165963_be.restservice.StatisticsRestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private StatisticsRestService statisticsRestService;

    /**
     * GET /api/statistics/revenue?year={year}&month={month}
     * - If month not provided: returns revenue per month in that year
     * - If month provided: returns detailed revenue for that month with breakdown per activityType
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueResponseDTO> getRevenue(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) {
            return ResponseEntity.badRequest().build();
        }

        RevenueResponseDTO response = statisticsRestService.getRevenue(year, month);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/revenue/yearly/{year}
     * Returns monthly revenue statistics for the entire year (Jan-Dec)
     */
    @GetMapping("/revenue/yearly/{year}")
    public ResponseEntity<List<RevenueYearlyResponseDTO>> getYearlyRevenue(@PathVariable Integer year) {
        if (year == null) {
            return ResponseEntity.badRequest().build();
        }

        List<RevenueYearlyResponseDTO> response = statisticsRestService.getYearlyRevenue(year);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/revenue/monthly/{year}/{month}
     * Returns detailed revenue statistics for a specific month
     * with breakdown per activityType
     */
    @GetMapping("/revenue/monthly/{year}/{month}")
    public ResponseEntity<RevenueMonthlyDetailResponseDTO> getMonthlyRevenue(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        
        if (year == null || month == null || month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }

        RevenueMonthlyDetailResponseDTO response = statisticsRestService.getMonthlyRevenue(year, month);
        return ResponseEntity.ok(response);
    }
}