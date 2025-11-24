package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.*;
import apap.ti._5.tour_package_2306165963_be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderedQuantityServiceImpl implements OrderedQuantityService {
    @Autowired private OrderedQuantityRepository oqRepo;
    @Autowired private PlanRepository planRepo;
    @Autowired private ActivityRepository actRepo;

    @Override
    public OrderedQuantity createOrderedQuantity(String planId, OrderedQuantity oq) {
        Plan plan = planRepo.findById(planId).orElseThrow();
        Activity act = actRepo.findById(oq.getActivityId()).orElseThrow();

        if(!"Pending".equals(plan.getStatus())) throw new IllegalStateException("Plan must be Pending");

        oq.setId(UUID.randomUUID().toString());
        oq.setPlanId(planId);
        oq.setPrice(act.getPrice());
        oq.setActivityName(act.getActivityName());
        oq.setActivityItem(act.getActivityItem());
        oq.setQuota(act.getCapacity());
        oq.setStartDate(act.getStartDate());
        oq.setEndDate(act.getEndDate());
        oq.setIsFulfilled(false);

        // Update Plan Price
        long addPrice = (long)(oq.getPrice() * oq.getOrderedQuota());
        plan.setPrice((plan.getPrice() == null ? 0 : plan.getPrice()) + addPrice);
        planRepo.save(plan);

        return oqRepo.save(oq);
    }

    @Override
    public boolean deleteOrderedQuantity(String id) {
        OrderedQuantity oq = oqRepo.findById(id).orElse(null);
        if(oq == null) return false;
        
        Plan plan = planRepo.findById(oq.getPlanId()).orElseThrow();
        if(!"Pending".equals(plan.getStatus())) throw new IllegalStateException("Plan must be Pending");
        
        long subPrice = (long)(oq.getPrice() * oq.getOrderedQuota());
        plan.setPrice(plan.getPrice() - subPrice);
        planRepo.save(plan);
        
        oqRepo.delete(oq);
        return true;
    }

    // Dummy
    @Override public List<OrderedQuantity> getAllOrderedQuantities() { return List.of(); }
    @Override public Optional<OrderedQuantity> getOrderedQuantityById(String id) { return oqRepo.findById(id); }
    @Override public OrderedQuantity updateOrderedQuantity(String id, Integer q) { return null; }
    @Override public List<OrderedQuantity> getOrderedQuantitiesByPlanId(String pid) { return List.of(); }
    @Override public Long calculateTotalPriceForPlan(String pid) { return 0L; }
}