package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306165963_be.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderedQuantityServiceImplEnhancedTest {

    @Mock
    OrderedQuantityRepository orderedQuantityRepository;

    @Mock
    PlanRepository planRepository;

    @Mock
    ActivityRepository activityRepository;

    @InjectMocks
    OrderedQuantityServiceImpl service;

    Plan plan;
    Activity activity;

    @BeforeEach
    void setup() {
        plan = TestDataFactory.plan("plan-1", "pkg-1");
        plan.setActivityType("Accommodation");
        activity = TestDataFactory.activity("act-1");
        activity.setActivityType("Accommodation");
    }

    // ========== NULL HANDLING TESTS ==========
    
    @Test
    void createOrderedQuantity_nullPlanId_throws() {
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        assertThatThrownBy(() -> service.createOrderedQuantity(null, oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plan ID cannot be null");
    }
    
    @Test
    void createOrderedQuantity_emptyPlanId_throws() {
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        assertThatThrownBy(() -> service.createOrderedQuantity("  ", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plan ID cannot be null");
    }
    
    @Test
    void createOrderedQuantity_nullOrderedQuantity_throws() {
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrderedQuantity cannot be null");
    }
    
    @Test
    void createOrderedQuantity_nullActivityId_throws() {
        OrderedQuantity oq = new OrderedQuantity();
        oq.setActivityId(null);
        oq.setOrderedQuota(2);
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Activity ID cannot be null");
    }
    
    @Test
    void createOrderedQuantity_nullOrderedQuota_throws() {
        OrderedQuantity oq = new OrderedQuantity();
        oq.setActivityId("act-1");
        oq.setOrderedQuota(null);
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ordered quota cannot be null");
    }
    
    @Test
    void createOrderedQuantity_activityIsDeleted_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setIsDeleted(true);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Activity is deleted");
    }
    
    @Test
    void createOrderedQuantity_activityCapacityZero_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setCapacity(0);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity must be greater than 0");
    }
    
    @Test
    void createOrderedQuantity_activityCapacityNegative_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setCapacity(-5); // capacity is primitive int, can't be null
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity must be greater than 0");
    }
    
    @Test
    void createOrderedQuantity_activityPriceZero_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setPrice(0L);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price must be greater than 0");
    }
    
    @Test
    void createOrderedQuantity_activityPriceNull_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setPrice(null);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price must be greater than 0");
    }
    
    @Test
    void createOrderedQuantity_activityStartDateNull_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setStartDate(null);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("start date and end date cannot be null");
    }
    
    @Test
    void createOrderedQuantity_activityEndDateNull_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setEndDate(null);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("start date and end date cannot be null");
    }
    
    @Test
    void createOrderedQuantity_activityStartDateAfterEndDate_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setStartDate(LocalDateTime.now().plusDays(10));
        activity.setEndDate(LocalDateTime.now().plusDays(5));
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("start date must be before end date");
    }
    
    @Test
    void createOrderedQuantity_activityStartDateEqualsEndDate_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        LocalDateTime sameTime = LocalDateTime.now().plusDays(5);
        activity.setStartDate(sameTime);
        activity.setEndDate(sameTime);
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("start date must be before end date");
    }

    // ========== ORDERED QUOTA VALIDATION TESTS ==========
    
    @Test
    void createOrderedQuantity_negativeOrderedQuota_throws() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        when(orderedQuantityRepository.findByPlanIdAndActivityId("plan-1", "act-1"))
                .thenReturn(List.of());
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        oq.setOrderedQuota(-5);
        
        assertThatThrownBy(() -> service.createOrderedQuantity("plan-1", oq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
    
    @SuppressWarnings("null")
    @Test
    void createOrderedQuantity_zeroOrderedQuota_success() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        when(orderedQuantityRepository.findByPlanIdAndActivityId("plan-1", "act-1"))
                .thenReturn(List.of());
        when(orderedQuantityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        OrderedQuantity oq = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        oq.setOrderedQuota(0);
        
        // Should succeed - quota can be 0
        OrderedQuantity saved = service.createOrderedQuantity("plan-1", oq);
        assertThat(saved.getOrderedQuota()).isEqualTo(0);
    }

    // ========== UPDATE VALIDATION TESTS ==========
    
    @Test
    void updateOrderedQuantity_nullId_throws() {
        assertThatThrownBy(() -> service.updateOrderedQuantity(null, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }
    
    @Test
    void updateOrderedQuantity_emptyId_throws() {
        assertThatThrownBy(() -> service.updateOrderedQuantity("  ", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }
    
    @Test
    void updateOrderedQuantity_nullNewQuota_throws() {
        assertThatThrownBy(() -> service.updateOrderedQuantity("oq-1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New quota cannot be null");
    }
    
    @Test
    void updateOrderedQuantity_negativeQuota_throws() {
        OrderedQuantity existing = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        when(orderedQuantityRepository.findById("oq-1")).thenReturn(Optional.of(existing));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.updateOrderedQuantity("oq-1", -3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
    
    @Test
    void updateOrderedQuantity_zeroQuota_throws() {
        OrderedQuantity existing = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        when(orderedQuantityRepository.findById("oq-1")).thenReturn(Optional.of(existing));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        // Update requires quota > 0 (business rule)
        assertThatThrownBy(() -> service.updateOrderedQuantity("oq-1", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be greater than 0");
    }
    
    @Test
    void updateOrderedQuantity_invalidQuotaData_throws() {
        OrderedQuantity existing = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        existing.setQuota(null); // Invalid state
        when(orderedQuantityRepository.findById("oq-1")).thenReturn(Optional.of(existing));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.updateOrderedQuantity("oq-1", 5))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalid quota data");
    }

    // ========== DELETE VALIDATION TESTS ==========
    
    @Test
    void deleteOrderedQuantity_nullId_returnsFalse() {
        assertThat(service.deleteOrderedQuantity(null)).isFalse();
    }
    
    @Test
    void deleteOrderedQuantity_emptyId_returnsFalse() {
        assertThat(service.deleteOrderedQuantity("  ")).isFalse();
    }
    
    @Test
    void deleteOrderedQuantity_planIsPending_success() {
        OrderedQuantity existing = TestDataFactory.oq("oq-1", "plan-1", "act-1");
        plan.setStatus("Pending");
        when(orderedQuantityRepository.findById("oq-1")).thenReturn(Optional.of(existing));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        when(orderedQuantityRepository.countByPlanId("plan-1")).thenReturn(1L);
        
        boolean result = service.deleteOrderedQuantity("oq-1");
        
        assertThat(result).isTrue();
        verify(orderedQuantityRepository).deleteById("oq-1");
    }

    // ========== GET BY ID NULL HANDLING ==========
    
    @Test
    void getOrderedQuantityById_nullId_returnsEmpty() {
        assertThat(service.getOrderedQuantityById(null)).isEmpty();
    }
    
    @Test
    void getOrderedQuantityById_emptyId_returnsEmpty() {
        assertThat(service.getOrderedQuantityById("  ")).isEmpty();
    }

    // ========== GET BY PLAN ID NULL HANDLING ==========
    
    @Test
    void getOrderedQuantitiesByPlanId_nullPlanId_returnsEmptyList() {
        assertThat(service.getOrderedQuantitiesByPlanId(null)).isEmpty();
    }
    
    @Test
    void getOrderedQuantitiesByPlanId_emptyPlanId_returnsEmptyList() {
        assertThat(service.getOrderedQuantitiesByPlanId("  ")).isEmpty();
    }

    // ========== CALCULATE TOTAL PRICE NULL HANDLING ==========
    
    @Test
    void calculateTotalPriceForPlan_nullPlanId_returnsZero() {
        assertThat(service.calculateTotalPriceForPlan(null)).isEqualTo(0L);
    }
    
    @Test
    void calculateTotalPriceForPlan_emptyPlanId_returnsZero() {
        assertThat(service.calculateTotalPriceForPlan("  ")).isEqualTo(0L);
    }

    // ========== SUCCESS SCENARIOS WITH ALL VALIDATIONS PASSING ==========
    
    @SuppressWarnings("null")
    @Test
    void createOrderedQuantity_allValidationsPass_success() {
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        activity.setIsDeleted(false); // Explicitly not deleted
        when(activityRepository.findById("act-1")).thenReturn(Optional.of(activity));
        when(orderedQuantityRepository.findByPlanIdAndActivityId("plan-1", "act-1"))
                .thenReturn(List.of());
        when(orderedQuantityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        OrderedQuantity oq = new OrderedQuantity();
        oq.setActivityId("act-1");
        oq.setOrderedQuota(2);
        
        OrderedQuantity saved = service.createOrderedQuantity("plan-1", oq);
        
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlanId()).isEqualTo("plan-1");
        assertThat(saved.getActivityName()).isEqualTo(activity.getActivityName());
        assertThat(saved.getQuota()).isEqualTo(activity.getCapacity());
        assertThat(saved.getPrice()).isEqualTo(activity.getPrice());
        assertThat(plan.getStatus()).isEqualTo("Pending");
        verify(planRepository).save(plan);
    }
}