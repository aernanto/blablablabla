package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    @Autowired private PlanRepository planRepository;
    @Autowired private PackageRepository packageRepository;

    @Override
    public List<Plan> getPlansByPackageId(String packageId) {
        return planRepository.findByPackageId(packageId);
    }

    @Override
    public Optional<Plan> getPlanById(String id) { return planRepository.findById(id); }

    @Override
    public Optional<Plan> getPlanWithOrderedQuantities(String id) {
        return planRepository.findByIdWithOrderedQuantities(id);
    }

    @Override
    public Plan createPlan(String packageId, Plan plan) {
        Package pkg = packageRepository.findById(packageId).orElseThrow();
        if("Processed".equals(pkg.getStatus())) throw new IllegalStateException("Package processed");

        plan.setId(UUID.randomUUID().toString());
        plan.setPackageId(packageId);
        plan.setStatus("Pending");
        if(plan.getOrderedQuantities() == null) plan.setOrderedQuantities(new ArrayList<>());

        return planRepository.save(plan);
    }

    @Override
    public Plan updatePlan(Plan plan) {
        Plan existing = planRepository.findById(plan.getId()).orElseThrow();
        if(!"Pending".equals(existing.getStatus())) throw new IllegalStateException("Only Pending plans can be updated");

        existing.setPrice(plan.getPrice());
        existing.setStartDate(plan.getStartDate());
        existing.setEndDate(plan.getEndDate());
        existing.setStartLocation(plan.getStartLocation());
        existing.setEndLocation(plan.getEndLocation());
        // Activity Type & PackageID gaboleh berubah

        return planRepository.save(existing);
    }

    @Override
    public boolean deletePlan(String id) {
        Plan plan = planRepository.findById(id).orElse(null);
        if(plan == null) return false;
        if(!"Pending".equals(plan.getStatus())) throw new IllegalStateException("Only Pending plans can be deleted");
        
        planRepository.delete(plan);
        return true;
    }

    @Override public List<Plan> getAllPlans() { return planRepository.findAll(); }
    @Override public void processPlan(String id) { /* Logic fulfilled here */ }
    @Override public Long calculateTotalPlanPrice(String planId) { return 0L; }
}