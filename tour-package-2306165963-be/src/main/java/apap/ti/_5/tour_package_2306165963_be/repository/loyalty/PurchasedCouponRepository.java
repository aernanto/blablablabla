package apap.ti._5.tour_package_2306165963_be.repository.loyalty;

import apap.ti._5.tour_package_2306165963_be.model.loyalty.PurchasedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasedCouponRepository extends JpaRepository<PurchasedCoupon, String> {
    List<PurchasedCoupon> findByCustomerId(String customerId);
    List<PurchasedCoupon> findByCustomerIdAndIsUsedFalse(String customerId);
    Optional<PurchasedCoupon> findByUniqueCode(String uniqueCode);
    boolean existsByUniqueCode(String uniqueCode);
}