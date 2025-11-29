package apap.ti._5.tour_package_2306165963_be.repository;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {
    // Find by user ID
    List<Package> findByUserId(String userId);
    
    // Find by status
    List<Package> findByStatus(String status);
    
    // Find by user ID and status
    List<Package> findByUserIdAndStatus(String userId, String status);
    
    // Find packages with price range
    List<Package> findByPriceBetween(Long minPrice, Long maxPrice);
    
    // Search by package name
    List<Package> findByPackageNameContainingIgnoreCase(String name);
    
    // Find pending packages
    @Query("SELECT p FROM Package p WHERE p.status = 'Pending' ORDER BY p.startDate DESC")
    List<Package> findAllPendingPackages();
    
    // Find packages with plans
    @Query("SELECT DISTINCT p FROM Package p LEFT JOIN FETCH p.plans WHERE p.id = :id")
    Optional<Package> findByIdWithPlans(@Param("id") String id);
    
    // Count packages by user
    long countByUserId(String userId);

    @Query("SELECT DISTINCT p FROM Package p " +
           "JOIN p.plans plan " +
           "JOIN plan.orderedQuantities oq " +
           "WHERE oq.activityId IN " +
           "(SELECT a.id FROM Activity a WHERE a.vendorId = :vendorId) " +
           "AND p.isDeleted = false")
    List<Package> findPackagesByVendorId(@Param("vendorId") String vendorId);

    @Query("SELECT DISTINCT p FROM Package p " +
           "WHERE p.userId = :userId " +
           "AND p.isDeleted = false")
    List<Package> findByUserIdOrIsPublic(@Param("userId") String userId);

    // Count untuk generate ID (Auto-generate prefix PKG-YYYYMMDD)
    long countByIdStartingWith(String prefix);
}