package apap.ti._5.tour_package_2306165963_be.restservice;

import apap.ti._5.tour_package_2306165963_be.dto.statistics.ActivityTypeBreakdownDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueMonthlyDetailResponseDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueResponseDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueYearlyResponseDTO;
import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatisticsRestServiceImpl implements StatisticsRestService {

    @Autowired
    private PackageService packageService;

    @Override
    public RevenueResponseDTO getRevenue(Integer year, Integer month) {
        if (month == null) {
            // Return yearly breakdown (per month)
            List<RevenueYearlyResponseDTO> monthlyData = getYearlyRevenue(year);
            
            RevenueResponseDTO response = new RevenueResponseDTO();
            response.setPeriod(year.toString());
            response.setMonthlyBreakdown(monthlyData);
            
            // Calculate total from all months
            long total = monthlyData.stream()
                    .mapToLong(dto -> dto.getTotalRevenue() != null ? dto.getTotalRevenue() : 0L)
                    .sum();
            response.setTotalRevenue(total);
            
            return response;
        } else {
            // Return monthly detail with activityType breakdown
            RevenueMonthlyDetailResponseDTO monthlyDetail = getMonthlyRevenue(year, month);
            
            RevenueResponseDTO response = new RevenueResponseDTO();
            response.setPeriod(String.format("%d-%02d", year, month));
            response.setTotalRevenue(monthlyDetail.getTotalRevenue());
            response.setBreakdown(monthlyDetail.getBreakdown());
            
            return response;
        }
    }

    @Override
    public List<RevenueYearlyResponseDTO> getYearlyRevenue(Integer year) {
        List<Package> allPackages = packageService.getAllPackages();
        
        // Initialize all 12 months with 0
        Map<Integer, Long> monthlyRevenue = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.put(i, 0L);
        }
        
        // Calculate revenue for each package
        for (Package pkg : allPackages) {
            if (pkg == null || pkg.getStartDate() == null) continue;
            
            LocalDateTime startDate = pkg.getStartDate();
            if (startDate.getYear() != year) continue;
            
            int month = startDate.getMonthValue();
            
            // Get plans safely
            List<Plan> plans = pkg.getPlans();
            if (plans == null) continue;
            
            // Sum all fulfilled ordered quantities
            long packageRevenue = plans.stream()
                    .filter(plan -> plan != null && plan.getOrderedQuantities() != null)
                    .flatMap(plan -> plan.getOrderedQuantities().stream())
                    .filter(oq -> oq != null && oq.getIsFulfilled() != null && oq.getIsFulfilled())
                    .mapToLong(oq -> {
                        Long totalPrice = oq.getTotalPrice();
                        return totalPrice != null ? totalPrice : 0L;
                    })
                    .sum();
            
            monthlyRevenue.put(month, monthlyRevenue.get(month) + packageRevenue);
        }
        
        // Convert to DTO list
        List<RevenueYearlyResponseDTO> result = new ArrayList<>();
        String[] monthNames = {"", "January", "February", "March", "April", "May", "June",
                               "July", "August", "September", "October", "November", "December"};
        
        for (int i = 1; i <= 12; i++) {
            RevenueYearlyResponseDTO dto = new RevenueYearlyResponseDTO();
            dto.setPeriod(String.format("%d-%02d", year, i));
            dto.setMonth(monthNames[i]);
            dto.setTotalRevenue(monthlyRevenue.get(i));
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public RevenueMonthlyDetailResponseDTO getMonthlyRevenue(Integer year, Integer month) {
        List<Package> allPackages = packageService.getAllPackages();
        
        // Initialize activity types
        Map<String, Long> activityRevenue = new HashMap<>();
        activityRevenue.put("Flight", 0L);
        activityRevenue.put("Accommodation", 0L);
        activityRevenue.put("Vehicle Rental", 0L);
        
        // Calculate revenue for the specific month
        for (Package pkg : allPackages) {
            if (pkg == null || pkg.getStartDate() == null) continue;
            
            LocalDateTime startDate = pkg.getStartDate();
            if (startDate.getYear() != year || startDate.getMonthValue() != month) {
                continue;
            }
            
            // Get plans safely
            List<Plan> plans = pkg.getPlans();
            if (plans == null) continue;
            
            // Process each plan
            for (Plan plan : plans) {
                if (plan == null || plan.getActivityType() == null) continue;
                
                String activityType = plan.getActivityType();
                
                // Normalize activity type
                if ("Vehicle".equalsIgnoreCase(activityType)) {
                    activityType = "Vehicle Rental";
                }
                
                // Get ordered quantities safely
                List<OrderedQuantity> orderedQuantities = plan.getOrderedQuantities();
                if (orderedQuantities == null) continue;
                
                // Sum fulfilled ordered quantities
                long planRevenue = orderedQuantities.stream()
                        .filter(oq -> oq != null && oq.getIsFulfilled() != null && oq.getIsFulfilled())
                        .mapToLong(oq -> {
                            Long totalPrice = oq.getTotalPrice();
                            return totalPrice != null ? totalPrice : 0L;
                        })
                        .sum();
                
                activityRevenue.put(activityType, 
                    activityRevenue.getOrDefault(activityType, 0L) + planRevenue);
            }
        }
        
        // Build response
        RevenueMonthlyDetailResponseDTO response = new RevenueMonthlyDetailResponseDTO();
        response.setPeriod(String.format("%d-%02d", year, month));
        
        // Calculate total
        long total = activityRevenue.values().stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        response.setTotalRevenue(total);
        
        // Build breakdown list
        List<ActivityTypeBreakdownDTO> breakdown = new ArrayList<>();
        for (Map.Entry<String, Long> entry : activityRevenue.entrySet()) {
            ActivityTypeBreakdownDTO dto = new ActivityTypeBreakdownDTO();
            dto.setActivityType(entry.getKey());
            dto.setRevenue(entry.getValue());
            breakdown.add(dto);
        }
        response.setBreakdown(breakdown);
        
        return response;
    }
}