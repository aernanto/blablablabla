package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderedQuantityServiceImpl implements OrderedQuantityService {

    @Autowired
    private PlanService planService;

    @Autowired
    private ActivityService activityService; // Diperlukan untuk mengambil data Activity

    @Override
    public OrderedQuantity createOrderedQuantity(String packageId, String planId, OrderedQuantity oqData) {
        Optional<Plan> planOpt = planService.getPlanById(packageId, planId);
        Optional<Activity> activityOpt = activityService.getActivityById(oqData.getActivityId());

        if (planOpt.isEmpty()) {
            throw new IllegalStateException("Plan not found.");
        }
        if (activityOpt.isEmpty()) {
            throw new IllegalStateException("Activity not found.");
        }

        Plan plan = planOpt.get();
        Activity activity = activityOpt.get();

        if ("Processed".equals(plan.getStatus())) {
            throw new IllegalStateException("Cannot add ordered quantity to processed plan.");
        }

        if (oqData.getOrderedQuota() <= 0) { 
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        if (oqData.getOrderedQuota() > activity.getCapacity()) {
            throw new IllegalArgumentException(String.format("Ordered quantity (%d) exceeds available activity capacity (%d).", oqData.getOrderedQuota(), activity.getCapacity()));
        }

        oqData.setId(UUID.randomUUID().toString());
        oqData.setPlanId(planId);
        oqData.setActivityName(activity.getActivityName());
        oqData.setActivityItem(activity.getActivityItem());
        oqData.setQuota(activity.getCapacity()); // Kuota maksimum activity saat ini
        oqData.setPrice(activity.getPrice()); // Harga satuan activity saat ini
        oqData.setStartDate(activity.getStartDate());
        oqData.setEndDate(activity.getEndDate());

        plan.getOrderedQuantities().add(oqData);

        return oqData;
    }

    @Override
    public Optional<OrderedQuantity> getOrderedQuantityById(String packageId, String planId, String oqId) {
        Optional<Plan> planOpt = planService.getPlanById(packageId, planId);

        if (planOpt.isEmpty()) {
            return Optional.empty();
        }

        return planOpt.get().getOrderedQuantities().stream()
                .filter(oq -> oq.getId().equals(oqId))
                .findFirst();
    }

    @Override
    public OrderedQuantity updateOrderedQuantity(String packageId, String planId, String oqId, OrderedQuantity updatedOq) {
        Optional<Plan> planOpt = planService.getPlanById(packageId, planId);
        Optional<OrderedQuantity> oqOpt = getOrderedQuantityById(packageId, planId, oqId);
        
        if (planOpt.isEmpty()) {
            throw new IllegalStateException("Plan not found.");
        }
        if (oqOpt.isEmpty()) {
            throw new IllegalStateException("Ordered Quantity not found.");
        }

        Plan plan = planOpt.get();

        if ("Processed".equals(plan.getStatus())) {
            throw new IllegalStateException("Cannot edit ordered quantity in processed plan.");
        }

        if (updatedOq.getOrderedQuota() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        if (updatedOq.getOrderedQuota() > oqOpt.get().getQuota()) {
             throw new IllegalArgumentException(String.format("Ordered quantity (%d) exceeds recorded activity capacity (%d).", updatedOq.getOrderedQuota(), oqOpt.get().getQuota()));
        }

        OrderedQuantity existing = oqOpt.get();
        
        existing.setOrderedQuota(updatedOq.getOrderedQuota()); 

        return existing;
    }

    @Override
    public boolean deleteOrderedQuantity(String packageId, String planId, String oqId) {
        Optional<Plan> planOpt = planService.getPlanById(packageId, planId);

        if (planOpt.isEmpty()) {
            return false;
        }

        Plan plan = planOpt.get();
        if ("Processed".equals(plan.getStatus())) {
             throw new IllegalStateException("Cannot delete ordered quantity from processed plan.");
        }
        
        return plan.getOrderedQuantities().removeIf(oq -> oq.getId().equals(oqId));
    }
}