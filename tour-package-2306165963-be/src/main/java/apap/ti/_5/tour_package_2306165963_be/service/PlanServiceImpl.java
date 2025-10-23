package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import apap.ti._5.tour_package_2306165963_be.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PackageService packageService;

    @Override
    public Plan createPlan(String packageId, Plan planData) {
        Optional<Package> pkgOpt = packageService.getPackageById(packageId);

        if (pkgOpt.isEmpty()) {
            throw new IllegalStateException("Package not found.");
        }

        Package pkg = pkgOpt.get();

        if (!"Pending".equals(pkg.getStatus())) {
            throw new IllegalStateException("Cannot add plan to processed package.");
        }

        if (planData.getStartDate() != null && planData.getEndDate() != null && 
            planData.getEndDate().isBefore(planData.getStartDate())) {
            throw new IllegalArgumentException("Plan end date must be after start date.");
        }

        planData.setId(UUID.randomUUID().toString());
        planData.setPackageId(packageId);
        planData.setStatus("Unfinished");
        planData.setOrderedQuantities(new ArrayList<>());

        pkg.getPlans().add(planData);
        return planData;
    }

    @Override
    public Optional<Plan> getPlanById(String packageId, String planId) {
        Optional<Package> pkgOpt = packageService.getPackageById(packageId);

        if (pkgOpt.isEmpty()) {
            return Optional.empty();
        }

        return pkgOpt.get().getPlans().stream()
                .filter(p -> p.getId().equals(planId))
                .findFirst();
    }

    @Override
    public Plan updatePlan(String packageId, String planId, Plan updatedPlan) {
        Optional<Package> pkgOpt = packageService.getPackageById(packageId);

        if (pkgOpt.isEmpty()) {
            throw new IllegalStateException("Package not found.");
        }

        Package pkg = pkgOpt.get();

        if (!"Pending".equals(pkg.getStatus())) {
            throw new IllegalStateException("Cannot edit plan in processed package.");
        }

        Optional<Plan> planOpt = getPlanById(packageId, planId);

        if (planOpt.isEmpty()) {
            throw new IllegalStateException("Plan not found.");
        }

        Plan existingPlan = planOpt.get();

        if ("Processed".equals(existingPlan.getStatus())) {
            throw new IllegalStateException("Cannot edit processed plan.");
        }

        if (updatedPlan.getStartDate() != null && updatedPlan.getEndDate() != null && 
            updatedPlan.getEndDate().isBefore(updatedPlan.getStartDate())) {
            throw new IllegalArgumentException("Plan end date must be after start date.");
        }

        updatedPlan.setId(planId);
        updatedPlan.setPackageId(packageId);
        updatedPlan.setOrderedQuantities(existingPlan.getOrderedQuantities());
        updatedPlan.setStatus(existingPlan.getStatus());

        int index = pkg.getPlans().indexOf(existingPlan);
        if (index != -1) {
            pkg.getPlans().set(index, updatedPlan);
        }

        return updatedPlan;
    }

    @Override
    public boolean deletePlan(String packageId, String planId) {
        Optional<Package> pkgOpt = packageService.getPackageById(packageId);

        if (pkgOpt.isEmpty()) {
            return false;
        }

        Package pkg = pkgOpt.get();
        return pkg.getPlans().removeIf(p -> p.getId().equals(planId));
    }

    @Override
    public Plan processPlan(String packageId, String planId) {
        Optional<Plan> planOpt = getPlanById(packageId, planId);

        if (planOpt.isEmpty()) {
            throw new IllegalStateException("Plan not found.");
        }

        Plan plan = planOpt.get();

        if (plan.getOrderedQuantities().isEmpty()) {
            throw new IllegalStateException("Cannot process plan: No ordered quantities added yet.");
        }

        plan.setStatus("Processed");
        return plan;
    }
}