package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private StatisticsService statisticsService;

    @PreAuthorize("hasAnyAuthority('Superadmin', 'TourPackageVendor')")
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        try {
            // Default to current year if not provided
            if (year == null) {
                year = Year.now().getValue();
            }

            // Validate month
            if (month != null && (month < 1 || month > 12)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "message", "Month must be between 1 and 12",
                    "timestamp", new Date()
                ));
            }

            // Get revenue data
            Map<String, Long> revenueByType = statisticsService.getRevenueByActivityType(year, month);

            // Calculate total
            long totalRevenue = revenueByType.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            // Build response
            Map<String, Object> data = new HashMap<>();
            data.put("period", month != null ? String.format("%d-%02d", year, month) : String.valueOf(year));
            data.put("totalRevenue", totalRevenue);
            data.put("breakdown", revenueByType);

            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Successfully retrieved revenue statistics",
                "timestamp", new Date(),
                "data", data
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "Error: " + e.getMessage(),
                    "timestamp", new Date()
                ));
        }
    }

    @PreAuthorize("hasAnyAuthority('Superadmin', 'TourPackageVendor')")
    @GetMapping("/revenue/yearly/{year}")
    public ResponseEntity<?> getYearlyRevenue(@PathVariable Integer year) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("year", year);
            
            Map<Integer, Long> monthlyRevenue = new HashMap<>();
            
            for (int month = 1; month <= 12; month++) {
                Map<String, Long> revenueByType = statisticsService.getRevenueByActivityType(year, month);
                long monthTotal = revenueByType.values().stream()
                        .mapToLong(Long::longValue)
                        .sum();
                monthlyRevenue.put(month, monthTotal);
            }
            
            response.put("monthlyRevenue", monthlyRevenue);
            
            long yearTotal = monthlyRevenue.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();
            response.put("totalRevenue", yearTotal);

            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Successfully retrieved yearly revenue",
                "timestamp", new Date(),
                "data", response
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "Error: " + e.getMessage(),
                    "timestamp", new Date()
                ));
        }
    }

    @PreAuthorize("hasAnyAuthority('Superadmin', 'TourPackageVendor')")
    @GetMapping("/revenue/monthly/{year}/{month}")
    public ResponseEntity<?> getMonthlyRevenue(
            @PathVariable Integer year,
            @PathVariable Integer month) {

        try {
            if (month < 1 || month > 12) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "message", "Month must be between 1 and 12",
                    "timestamp", new Date()
                ));
            }

            Map<String, Long> revenueByType = statisticsService.getRevenueByActivityType(year, month);

            long totalRevenue = revenueByType.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            Map<String, Object> response = new HashMap<>();
            response.put("period", String.format("%d-%02d", year, month));
            response.put("totalRevenue", totalRevenue);
            response.put("breakdown", revenueByType);

            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Successfully retrieved monthly revenue",
                "timestamp", new Date(),
                "data", response
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "Error: " + e.getMessage(),
                    "timestamp", new Date()
                ));
        }
    }
}