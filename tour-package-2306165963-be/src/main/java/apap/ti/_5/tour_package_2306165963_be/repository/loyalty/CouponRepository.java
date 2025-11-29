package apap.ti._5.tour_package_2306165963_be.repository.loyalty;

import apap.ti._5.tour_package_2306165963_be.model.loyalty.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    List<Coupon> findByIsDeletedFalse();
    Optional<Coupon> findByIdAndIsDeletedFalse(String id);
    boolean existsByNameAndIsDeletedFalse(String name);
}