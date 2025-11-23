package apap.ti._5.tour_package_2306165963_be.repository;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    List<Activity> findByActivityType(String activityType);
   
    List<Activity> findByActivityNameContainingIgnoreCase(String name);
   
    List<Activity> findByPriceBetween(Long minPrice, Long maxPrice);
   
    List<Activity> findByCapacityGreaterThanEqual(Integer capacity);
   
    List<Activity> findByCapacityGreaterThan(Integer capacity);
   
    boolean existsByActivityName(String activityName);
    
    List<Activity> findByIsDeletedOrderByStartDateAsc(Boolean isDeleted);
    
    List<Activity> findAllByOrderByStartDateAsc();
    
    List<Activity> findByActivityTypeAndIsDeletedOrderByStartDateAsc(String activityType, Boolean isDeleted);
    
    List<Activity> findByActivityNameContainingIgnoreCaseAndIsDeletedOrderByStartDateAsc(String name, Boolean isDeleted);
    
    List<Activity> findByActivityItemContainingIgnoreCaseAndIsDeletedOrderByStartDateAsc(String item, Boolean isDeleted);
    
    List<Activity> findByIdStartingWith(String prefix);
    
    List<Activity> findByStartLocationContainingIgnoreCaseAndIsDeletedOrderByStartDateAsc(String location, Boolean isDeleted);
    
    List<Activity> findByEndLocationContainingIgnoreCaseAndIsDeletedOrderByStartDateAsc(String location, Boolean isDeleted);
}