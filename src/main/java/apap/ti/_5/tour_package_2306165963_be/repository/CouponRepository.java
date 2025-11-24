package apap.ti._5.tour_package_2306165963_be.repository;
import apap.ti._5.tour_package_2306165963_be.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, String> {
    List<Coupon> findByIsDeletedFalse();
}