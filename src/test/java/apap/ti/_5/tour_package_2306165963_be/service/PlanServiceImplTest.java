package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.OrderedQuantityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import apap.ti._5.tour_package_2306165963_be.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceImplTest {

    @Mock PlanRepository planRepository;
    @Mock PackageRepository packageRepository;
    @Mock OrderedQuantityRepository orderedQuantityRepository;
    
    @InjectMocks PlanServiceImpl service;

    Package pkg;
    Plan plan;

    @BeforeEach
    void setup() {
        pkg = TestDataFactory.pkg("pkg-1");
        pkg.setStatus("Pending"); // Ensure package is Pending
        plan = TestDataFactory.plan("plan-1", "pkg-1");
        plan.setStatus("Pending"); // Default to Pending
    }

    // ========== REQUIREMENT 1: Menampilkan seluruh daftar Plan berdasarkan Package ID ==========
    
    @Test
    void getPlansByPackageId_returnsAllPlans() {
        Plan plan2 = TestDataFactory.plan("plan-2", "pkg-1");
        when(planRepository.findByPackageId("pkg-1"))
            .thenReturn(List.of(plan, plan2));
        
        List<Plan> result = service.getPlansByPackageId("pkg-1");
        
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactly("plan-1", "plan-2");
    }
    
    @Test
    void getPlansByPackageId_nullPackageId_throws() {
        assertThatThrownBy(() -> service.getPlansByPackageId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Package ID cannot be null");
    }
    
    @Test
    void getPlansByPackageId_emptyPackageId_throws() {
        assertThatThrownBy(() -> service.getPlansByPackageId("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Package ID cannot be null");
    }

    // ========== REQUIREMENT 2: Menampilkan detail lengkap Plan dengan OrderedActivities ==========
    
    @Test
    void getPlanWithOrderedQuantities_returnsFullData() {
        plan.setOrderedQuantities(List.of(
            TestDataFactory.oq("oq-1", "plan-1", "act-1"),
            TestDataFactory.oq("oq-2", "plan-1", "act-2")
        ));
        when(planRepository.findByIdWithOrderedQuantities("plan-1"))
            .thenReturn(Optional.of(plan));
        
        Optional<Plan> result = service.getPlanWithOrderedQuantities("plan-1");
        
        assertThat(result).isPresent();
        assertThat(result.get().getOrderedQuantities()).hasSize(2);
    }
    
    @Test
    void getPlanById_nullId_throws() {
        assertThatThrownBy(() -> service.getPlanById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Plan ID cannot be null");
    }

    // ========== REQUIREMENT 3: Validasi semua atribut tidak boleh kosong ==========
    
    @Test
    void createPlan_missingActivityType_throws() {
        plan.setActivityType(null);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Activity type is required");
    }
    
    @Test
    void createPlan_missingPrice_throws() {
        plan.setPrice(null);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Price is required");
    }
    
    @Test
    void createPlan_missingStartDate_throws() {
        plan.setStartDate(null);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Start date is required");
    }
    
    @Test
    void createPlan_missingEndDate_throws() {
        plan.setEndDate(null);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("End date is required");
    }
    
    @Test
    void createPlan_missingStartLocation_throws() {
        plan.setStartLocation("");
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Start location is required");
    }
    
    @Test
    void createPlan_missingEndLocation_throws() {
        plan.setEndLocation("  ");
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("End location is required");
    }

    // ========== REQUIREMENT 4: PlanID dibuat otomatis menggunakan UUID ==========
    
    @Test
    void createPlan_generatesUUID() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        plan.setId(null); // Ensure no ID
        Plan saved = service.createPlan("pkg-1", plan);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    // ========== REQUIREMENT 5: Status default Plan baru adalah "Pending" ==========
    
    @Test
    void createPlan_setsStatusToPending() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        Plan saved = service.createPlan("pkg-1", plan);
        
        assertThat(saved.getStatus()).isEqualTo("Pending");
    }

    // ========== REQUIREMENT 6: Validasi startDate < endDate, price > 0 ==========
    
    @Test
    void createPlan_startDateAfterEndDate_throws() {
        plan.setStartDate(LocalDateTime.now().plusDays(10));
        plan.setEndDate(LocalDateTime.now().plusDays(5));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("End date must be after start date");
    }
    
    @Test
    void createPlan_startDateEqualsEndDate_throws() {
        LocalDateTime now = LocalDateTime.now().plusDays(5);
        plan.setStartDate(now);
        plan.setEndDate(now);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("End date must be after start date");
    }
    
    @Test
    void createPlan_negativePrice_throws() {
        plan.setPrice(-1000L);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Price cannot be negative");
    }
    
    @Test
    void createPlan_zeroPrice_allowed() {
        plan.setPrice(0L);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        Plan saved = service.createPlan("pkg-1", plan);
        
        assertThat(saved.getPrice()).isEqualTo(0L);
    }

    // ========== REQUIREMENT 7: Plan dates dalam range Package dates ==========
    
    @Test
    void createPlan_datesOutOfPackageRange_throws() {
        plan.setStartDate(pkg.getStartDate().minusDays(1));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("within package dates");
    }
    
    @Test
    void createPlan_endDateAfterPackageEnd_throws() {
        plan.setEndDate(pkg.getEndDate().plusDays(1));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        
        assertThatThrownBy(() -> service.createPlan("pkg-1", plan))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("within package dates");
    }

    // ========== REQUIREMENT 8: Update hanya untuk Plan dengan status "Pending" ==========
    
    @Test
    void updatePlan_pendingStatus_allowed() {
        plan.setStatus("Pending");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        plan.setPrice(9999999L);
        Plan updated = service.updatePlan(plan);
        
        assertThat(updated.getPrice()).isEqualTo(9999999L);
    }
    
    @Test
    void updatePlan_processedStatus_throws() {
        plan.setStatus("Processed");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.updatePlan(plan))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only Pending plans can be updated");
    }
    
    @Test
    void updatePlan_unfinishedStatus_throws() {
        plan.setStatus("Unfinished");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.updatePlan(plan))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only Pending plans can be updated");
    }

    // ========== REQUIREMENT 9: PackageId dan activityType tidak dapat diubah ==========
    
    @Test
    void updatePlan_packageIdRemainsSame() {
        plan.setStatus("Pending");
        plan.setPackageId("pkg-1");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        Plan updateRequest = TestDataFactory.plan("plan-1", "pkg-1");
        updateRequest.setPackageId("different-package"); // Try to change
        
        Plan updated = service.updatePlan(updateRequest);
        
        // PackageId should NOT change
        assertThat(updated.getPackageId()).isEqualTo("pkg-1");
    }
    
    @Test
    void updatePlan_activityTypeRemainsSame() {
        plan.setStatus("Pending");
        plan.setActivityType("Flight");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        Plan updateRequest = TestDataFactory.plan("plan-1", "pkg-1");
        updateRequest.setActivityType("Vehicle"); // Try to change
        
        Plan updated = service.updatePlan(updateRequest);
        
        // ActivityType should NOT change
        assertThat(updated.getActivityType()).isEqualTo("Flight");
    }

    // ========== REQUIREMENT 10: Delete hanya untuk status "Pending" ==========
    
    @Test
    void deletePlan_pendingStatus_allowed() {
        plan.setStatus("Pending");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        boolean deleted = service.deletePlan("plan-1");
        
        assertThat(deleted).isTrue();
        verify(orderedQuantityRepository).deleteByPlanId("plan-1");
        verify(planRepository).deleteById("plan-1");
    }
    
    @Test
    void deletePlan_processedStatus_throws() {
        plan.setStatus("Processed");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.deletePlan("plan-1"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only Pending plans can be deleted");
    }

    // ========== REQUIREMENT 11: Cascade delete OrderedQuantities ==========
    
    @Test
    void deletePlan_cascadeDeletesOrderedQuantities() {
        plan.setStatus("Pending");
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(plan));
        
        service.deletePlan("plan-1");
        
        verify(orderedQuantityRepository, times(1)).deleteByPlanId("plan-1");
    }

    // ========== REQUIREMENT 12: Process Plan (Pending -> Processed) ==========
    
    @Test
    void processPlan_onlyPendingAllowed() {
        plan.setStatus("Pending");
        plan.setOrderedQuantities(List.of(TestDataFactory.oq("oq-1", "plan-1", "act-1")));
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        when(orderedQuantityRepository.sumTotalPriceByPlanId("plan-1")).thenReturn(3000000L);
        when(planRepository.findByPackageId("pkg-1")).thenReturn(List.of(plan));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        service.processPlan("plan-1");
        
        assertThat(plan.getStatus()).isEqualTo("Processed");
    }
    
    @Test
    void processPlan_processedStatus_throws() {
        plan.setStatus("Processed");
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.processPlan("plan-1"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Can only process Pending plans");
    }
    
    @Test
    void processPlan_unfinishedStatus_throws() {
        plan.setStatus("Unfinished");
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.processPlan("plan-1"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Can only process Pending plans");
    }
    
    @Test
    void processPlan_noOrderedQuantities_throws() {
        plan.setStatus("Pending");
        plan.setOrderedQuantities(new ArrayList<>());
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        
        assertThatThrownBy(() -> service.processPlan("plan-1"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("without ordered quantities");
    }
    
    @Test
    void processPlan_calculatesPrice() {
        plan.setStatus("Pending");
        plan.setOrderedQuantities(List.of(TestDataFactory.oq("oq-1", "plan-1", "act-1")));
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        when(orderedQuantityRepository.sumTotalPriceByPlanId("plan-1")).thenReturn(5500000L);
        when(planRepository.findByPackageId("pkg-1")).thenReturn(List.of(plan));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        service.processPlan("plan-1");
        
        assertThat(plan.getPrice()).isEqualTo(5500000L);
    }

    // ========== NULL SAFETY TESTS ==========
    
    @Test
    void createPlan_nullPlan_throws() {
        assertThatThrownBy(() -> service.createPlan("pkg-1", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Plan cannot be null");
    }
    
    @Test
    void updatePlan_nullPlan_throws() {
        assertThatThrownBy(() -> service.updatePlan(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Plan cannot be null");
    }
    
    @Test
    void calculateTotalPlanPrice_nullId_returnsZero() {
        Long result = service.calculateTotalPlanPrice(null);
        assertThat(result).isEqualTo(0L);
    }
    
    @Test
    void calculateTotalPlanPrice_nullSum_returnsZero() {
        when(orderedQuantityRepository.sumTotalPriceByPlanId("plan-1")).thenReturn(null);
        
        Long result = service.calculateTotalPlanPrice("plan-1");
        
        assertThat(result).isEqualTo(0L);
    }

    // ========== PACKAGE STATUS UPDATE TESTS ==========
    
    @Test
    void processPlan_updatesPackageStatusWhenAllPlansProcessed() {
        // Setup: 2 plans, both will be processed
        Plan plan2 = TestDataFactory.plan("plan-2", "pkg-1");
        plan2.setStatus("Processed");
        
        plan.setStatus("Pending");
        plan.setOrderedQuantities(List.of(TestDataFactory.oq("oq-1", "plan-1", "act-1")));
        
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        when(orderedQuantityRepository.sumTotalPriceByPlanId("plan-1")).thenReturn(3000000L);
        when(planRepository.findByPackageId("pkg-1")).thenReturn(List.of(plan, plan2));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> {
            Plan p = inv.getArgument(0);
            p.setStatus("Processed");
            return p;
        });
        
        service.processPlan("plan-1");
        
        // Package should be updated to Processed
        verify(packageRepository, times(1)).save(argThat(p -> "Processed".equals(p.getStatus())));
    }
    
    @Test
    void processPlan_doesNotUpdatePackageIfSomePlansStillPending() {
        Plan plan2 = TestDataFactory.plan("plan-2", "pkg-1");
        plan2.setStatus("Pending"); // Still pending
        
        plan.setStatus("Pending");
        plan.setOrderedQuantities(List.of(TestDataFactory.oq("oq-1", "plan-1", "act-1")));
        
        when(planRepository.findByIdWithOrderedQuantities("plan-1")).thenReturn(Optional.of(plan));
        when(orderedQuantityRepository.sumTotalPriceByPlanId("plan-1")).thenReturn(3000000L);
        when(planRepository.findByPackageId("pkg-1")).thenReturn(List.of(plan, plan2));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        service.processPlan("plan-1");
        
        // Package should NOT be updated
        verify(packageRepository, never()).save(any());
    }
}