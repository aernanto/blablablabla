package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderedQuantityServiceImpl implements OrderedQuantityService {

    @Autowired
    private OrderedQuantityRepository orderedQuantityRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public List<OrderedQuantity> getAllOrderedQuantities() {
        return orderedQuantityRepository.findAll();
    }

    @Override
    public Optional<OrderedQuantity> getOrderedQuantityById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return orderedQuantityRepository.findById(id);
    }

    @Override
    public OrderedQuantity createOrderedQuantity(String planId, OrderedQuantity orderedQuantity) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan ID cannot be null or empty");
        }
        if (orderedQuantity == null) {
            throw new IllegalArgumentException("OrderedQuantity cannot be null");
        }
        if (orderedQuantity.getActivityId() == null || orderedQuantity.getActivityId().trim().isEmpty()) {
            throw new IllegalArgumentException("Activity ID cannot be null or empty");
        }
        if (orderedQuantity.getOrderedQuota() == null) {
            throw new IllegalArgumentException("Ordered quota cannot be null");
        }

        // === VERIFY PLAN EXISTS ===
        Optional<Plan> planOptional = planRepository.findById(planId);
        if (planOptional.isEmpty()) {
            throw new IllegalArgumentException("Plan not found with ID: " + planId);
        }

        Plan plan = planOptional.get();

        // === CHECK PLAN STATUS ===
        if ("Processed".equals(plan.getStatus())) {
            throw new IllegalStateException("Cannot add ordered quantity to processed plan");
        }

        // === VERIFY ACTIVITY EXISTS ===
        String activityId = orderedQuantity.getActivityId();
        @SuppressWarnings("null")
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with ID: " + activityId);
        }

        Activity activity = activityOptional.get();

        // === ACTIVITY VALIDATIONS ===
        if (activity.getIsDeleted() != null && activity.getIsDeleted()) {
            throw new IllegalArgumentException("Activity is deleted and cannot be used");
        }

        if (activity.getCapacity() <= 0) {
            throw new IllegalArgumentException("Activity capacity must be greater than 0");
        }

        if (activity.getPrice() == null || activity.getPrice() <= 0) {
            throw new IllegalArgumentException("Activity price must be greater than 0");
        }

        if (activity.getStartDate() == null || activity.getEndDate() == null) {
            throw new IllegalArgumentException("Activity start date and end date cannot be null");
        }
        if (!activity.getStartDate().isBefore(activity.getEndDate())) {
            throw new IllegalArgumentException("Activity start date must be before end date");
        }

        if (activity.getActivityType() == null || plan.getActivityType() == null) {
            throw new IllegalArgumentException("Activity type and plan type cannot be null");
        }

        if (!activity.getActivityType().equals(plan.getActivityType())) {
            throw new IllegalArgumentException(
                "Activity type (" + activity.getActivityType() +
                ") does not match plan type (" + plan.getActivityType() + ")"
            );
        }

        // === CHECK IF ACTIVITY ALREADY IN PLAN ===
        List<OrderedQuantity> existingOQ =
                orderedQuantityRepository.findByPlanIdAndActivityId(planId, activityId);

        if (!existingOQ.isEmpty()) {
            throw new IllegalStateException("Activity is already added to this plan");
        }

        // === QUOTA VALIDATION ===
        if (orderedQuantity.getOrderedQuota() < 0) {
            throw new IllegalArgumentException("Ordered quota cannot be negative");
        }
        if (orderedQuantity.getOrderedQuota() > activity.getCapacity()) {
            throw new IllegalArgumentException(
                "Ordered quota (" + orderedQuantity.getOrderedQuota() +
                ") exceeds activity capacity (" + activity.getCapacity() + ")"
            );
        }

        // === SETUP ORDERED QUANTITY ===
        orderedQuantity.setId(UUID.randomUUID().toString());
        orderedQuantity.setPlanId(planId);

        // Copy activity details
        orderedQuantity.setQuota(activity.getCapacity());
        orderedQuantity.setPrice(activity.getPrice());
        orderedQuantity.setActivityName(activity.getActivityName());
        orderedQuantity.setActivityItem(activity.getActivityItem());
        orderedQuantity.setStartDate(activity.getStartDate());
        orderedQuantity.setEndDate(activity.getEndDate());

        // === NEW: SET isFulfilled = false ===
        orderedQuantity.setIsFulfilled(false);

        // === SAVE ===
        OrderedQuantity saved = orderedQuantityRepository.save(orderedQuantity);

        // === UPDATE PLAN STATUS ===
        if ("Unfinished".equals(plan.getStatus())) {
            plan.setStatus("Pending");
            planRepository.save(plan);
        }

        return saved;
    }

    @Override
    public OrderedQuantity updateOrderedQuantity(String id, Integer newQuota) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("OrderedQuantity ID cannot be null or empty");
        }
        if (newQuota == null) {
            throw new IllegalArgumentException("New quota cannot be null");
        }

        Optional<OrderedQuantity> existingOQ = orderedQuantityRepository.findById(id);
        if (existingOQ.isEmpty()) {
            throw new IllegalArgumentException("Ordered quantity not found with ID: " + id);
        }

        OrderedQuantity oq = existingOQ.get();

        // === VALIDATE PLAN STATUS ===
        @SuppressWarnings("null")
        Optional<Plan> planOptional = planRepository.findById(oq.getPlanId());
        if (planOptional.isPresent() && "Processed".equals(planOptional.get().getStatus())) {
            throw new IllegalStateException("Cannot update ordered quantity of processed plan");
        }

        if (newQuota <= 0) {
            throw new IllegalArgumentException("Ordered quota must be greater than 0");
        }

        if (newQuota > oq.getQuota()) {
            throw new IllegalArgumentException(
                "Ordered quota (" + newQuota +
                ") exceeds activity capacity (" + oq.getQuota() + ")"
            );
        }

        oq.setOrderedQuota(newQuota);

        return orderedQuantityRepository.save(oq);
    }

    @Override
    public boolean deleteOrderedQuantity(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        Optional<OrderedQuantity> oqOptional = orderedQuantityRepository.findById(id);
        if (oqOptional.isEmpty()) {
            return false;
        }

        OrderedQuantity oq = oqOptional.get();

        if (oq.getPlanId() != null) {
            @SuppressWarnings("null")
            Optional<Plan> planOptional = planRepository.findById(oq.getPlanId());

            if (planOptional.isPresent()) {
                Plan plan = planOptional.get();

                if ("Processed".equals(plan.getStatus())) {
                    throw new IllegalStateException("Cannot delete ordered quantity from processed plan");
                }

                orderedQuantityRepository.deleteById(id);

                long count = orderedQuantityRepository.countByPlanId(oq.getPlanId());

                if (count == 0) {
                    plan.setStatus("Unfinished");
                    planRepository.save(plan);
                }
            } else {
                orderedQuantityRepository.deleteById(id);
            }
        } else {
            orderedQuantityRepository.deleteById(id);
        }

        return true;
    }

    @Override
    public List<OrderedQuantity> getOrderedQuantitiesByPlanId(String planId) {
        if (planId == null || planId.trim().isEmpty()) {
            return List.of();
        }
        return orderedQuantityRepository.findByPlanId(planId);
    }

    @Override
    public Long calculateTotalPriceForPlan(String planId) {
        if (planId == null || planId.trim().isEmpty()) {
            return 0L;
        }
        Long totalPrice = orderedQuantityRepository.sumTotalPriceByPlanId(planId);
        return totalPrice != null ? totalPrice : 0L;
    }

    // ============================================================
    // NEW METHODS — FULFILLMENT HANDLING
    // ============================================================

    public OrderedQuantity fulfillOrderedQuantity(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("OrderedQuantity ID cannot be null or empty");
        }

        Optional<OrderedQuantity> oqOptional = orderedQuantityRepository.findById(id);
        if (oqOptional.isEmpty()) {
            throw new IllegalArgumentException("Ordered quantity not found with ID: " + id);
        }

        OrderedQuantity oq = oqOptional.get();

        if (Boolean.TRUE.equals(oq.getIsFulfilled())) {
            throw new IllegalStateException("OrderedQuantity is already fulfilled");
        }

        oq.setIsFulfilled(true);

        return orderedQuantityRepository.save(oq);
    }

    public OrderedQuantity unfulfillOrderedQuantity(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("OrderedQuantity ID cannot be null or empty");
        }

        Optional<OrderedQuantity> oqOptional = orderedQuantityRepository.findById(id);
        if (oqOptional.isEmpty()) {
            throw new IllegalArgumentException("Ordered quantity not found with ID: " + id);
        }

        OrderedQuantity oq = oqOptional.get();

        oq.setIsFulfilled(false);

        return orderedQuantityRepository.save(oq);
    }
}