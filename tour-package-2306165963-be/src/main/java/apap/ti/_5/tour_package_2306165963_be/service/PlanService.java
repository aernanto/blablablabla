package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Plan;
import java.util.Optional;

public interface PlanService {
    Plan createPlan(String packageId, Plan planData);
    Optional<Plan> getPlanById(String packageId, String planId);
    Plan updatePlan(String packageId, String planId, Plan updatedPlan);
    boolean deletePlan(String packageId, String planId);
    Plan processPlan(String packageId, String planId);
}