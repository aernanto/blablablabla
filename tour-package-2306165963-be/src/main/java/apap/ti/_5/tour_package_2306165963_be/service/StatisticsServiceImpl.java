package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private PackageService packageService;

    @Override
    public Map<String, Long> getRevenueByActivityType(Integer year, Integer month) {
        Map<String, Long> revenueMap = new HashMap<>();
        
        revenueMap.put("Flight", 0L);
        revenueMap.put("Accommodation", 0L);
        revenueMap.put("Vehicle Rental", 0L);

        List<Package> allPackages = packageService.getAllPackages();

        for (Package pkg : allPackages) {
            // Skip if not Fulfilled or Processed
            if (!"Fulfilled".equals(pkg.getStatus()) && !"Processed".equals(pkg.getStatus())) {
                continue;
            }

            // Check year
            if (pkg.getStartDate() != null && pkg.getStartDate().getYear() != year) {
                continue;
            }

            // Check month (if provided)
            if (month != null && pkg.getStartDate() != null && 
                pkg.getStartDate().getMonthValue() != month) {
                continue;
            }

            // Iterate through plans
            for (Plan plan : pkg.getPlans()) {
                if (!"Fulfilled".equals(plan.getStatus())) {
                    continue;
                }
                
                String activityType = plan.getActivityType();
                
                // Normalize Vehicle type
                if ("Vehicle".equalsIgnoreCase(activityType)) {
                    activityType = "Vehicle Rental";
                }

                // Calculate revenue from ordered quantities
                long planRevenue = plan.getOrderedQuantities().stream()
                        .mapToLong(oq -> oq.getPrice() * oq.getOrderedQuota())
                        .sum();

                revenueMap.put(activityType, revenueMap.getOrDefault(activityType, 0L) + planRevenue);
            }
        }

        return revenueMap;
    }
}