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

    // ================= NEW METHODS (IMPLEMENTED) =================

    /**
     * Query untuk ambil packages by vendor ID.
     * ADAPTED: Menggunakan 'orderedQuantities' (Logic Lama) bukan 'orderedActivities' (Logic Baru)
     */
    @Query("SELECT DISTINCT p FROM Package p " +
           "JOIN p.plans plan " +
           "JOIN plan.orderedQuantities oq " +  // Sesuaikan dengan field di Model Plan lama
           "JOIN oq.activity act " +            // Relasi OrderedQuantity ke Activity
           "WHERE act.vendorId = :vendorId " +
           "AND p.isDeleted = false")
    List<Package> findPackagesByVendorId(@Param("vendorId") String vendorId);

    /**
     * Query untuk customer (lihat miliknya sendiri + public packages dari Vendor/Admin).
     * NOTE: Pastikan Entity 'EndUser' ada di project ini. Jika nama class User beda, ganti 'EndUser'.
     */
    @Query("SELECT p FROM Package p " +
           "WHERE (p.userId = :userId OR p.userId IN " +
           "(SELECT u.id FROM EndUser u WHERE u.role IN ('Superadmin', 'TourPackageVendor'))) " +
           "AND p.isDeleted = false")
    List<Package> findByUserIdOrIsPublic(@Param("userId") String userId);

    // Count untuk generate ID (Auto-generate prefix PKG-YYYYMMDD)
    long countByIdStartingWith(String prefix);
}