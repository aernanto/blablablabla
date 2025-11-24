package apap.ti._5.tour_package_2306165963_be.repository;
import apap.ti._5.tour_package_2306165963_be.model.PurchasedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PurchasedCouponRepository extends JpaRepository<PurchasedCoupon, String> {
    List<PurchasedCoupon> findByCustomerId(String customerId);
    Optional<PurchasedCoupon> findByUniqueCode(String code);
}