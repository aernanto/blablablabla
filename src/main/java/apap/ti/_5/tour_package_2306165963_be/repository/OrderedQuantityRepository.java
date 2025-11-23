package apap.ti._5.tour_package_2306165963_be.repository;

import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderedQuantityRepository extends JpaRepository<OrderedQuantity, String> {
    List<OrderedQuantity> findByPlanId(String planId);
    
    List<OrderedQuantity> findByActivityId(String activityId);

    List<OrderedQuantity> findByPlanIdAndActivityId(String planId, String activityId);

    @Query("SELECT SUM(oq.orderedQuota) FROM OrderedQuantity oq WHERE oq.planId = :planId")
    Integer sumOrderedQuotaByPlanId(@Param("planId") String planId);

    @Query("SELECT SUM(oq.price * oq.orderedQuota) FROM OrderedQuantity oq WHERE oq.planId = :planId")
    Long sumTotalPriceByPlanId(@Param("planId") String planId);

    long countByPlanId(String planId);

    void deleteByPlanId(String planId);

    boolean existsByActivityId(String activityId);

    @Query("SELECT CASE WHEN COUNT(oq) > 0 THEN true ELSE false END FROM OrderedQuantity oq, Plan p " + 
            "WHERE oq.planId = p.id " + // Hubungkan OrderedQuantity ke Plan menggunakan ID
            "AND oq.activityId = :activityId " +
            "AND p.status <> :status")
    boolean existsByActivityIdAndStatusNot(@Param("activityId") String activityId, @Param("status") String status);
}