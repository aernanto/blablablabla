package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {
    @Autowired private PlanRepository planRepository;
    @Autowired private PackageRepository packageRepository;
    @Autowired private OrderedQuantityRepository oqRepo;

    @Override
    public Plan createPlan(String packageId, Plan plan) {
        @SuppressWarnings("null")
        Package pkg = packageRepository.findById(packageId).orElseThrow();
        if("Processed".equals(pkg.getStatus())) throw new IllegalStateException("Package processed");

        plan.setId(UUID.randomUUID().toString());
        plan.setPackageId(packageId);
        plan.setStatus("Pending");
        if(plan.getOrderedQuantities() == null) plan.setOrderedQuantities(new ArrayList<>());
        
        return planRepository.save(plan);
    }

    @Override
    public boolean deletePlan(String id) {
        @SuppressWarnings("null")
        Plan plan = planRepository.findById(id).orElse(null);
        if(plan == null) return false;
        if(!"Pending".equals(plan.getStatus())) throw new IllegalStateException("Only Pending");
        
        oqRepo.deleteByPlanId(id);
        planRepository.delete(plan);
        return true;
    }

    // Dummy implementation
    @Override public List<Plan> getPlansByPackageId(String pid) { return planRepository.findByPackageId(pid); }
    @SuppressWarnings("null")
    @Override public Optional<Plan> getPlanById(String id) { return planRepository.findById(id); }
    @Override public Optional<Plan> getPlanWithOrderedQuantities(String id) { return planRepository.findByIdWithOrderedQuantities(id); }
    @SuppressWarnings("null")
    @Override public Plan updatePlan(Plan p) { return planRepository.save(p); }
    @Override public List<Plan> getAllPlans() { return List.of(); }
    @Override public void processPlan(String id) {}
    @Override public Long calculateTotalPlanPrice(String id) { return 0L; }
}