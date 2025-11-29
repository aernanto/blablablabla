package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.dto.coupon.*;
import apap.ti._5.tour_package_2306165963_be.model.loyalty.*;
import java.util.List;

public interface CouponService {
    List<Coupon> getAllAvailableCoupons();
    List<PurchasedCoupon> getPurchasedCoupons(String customerId);
    Integer getCustomerPoints(String customerId);
    Coupon createCoupon(CouponRequestDto dto);
    Coupon updateCoupon(String id, CouponRequestDto dto);
    PurchasedCoupon purchaseCoupon(PurchaseRequestDto dto);
    Customer addPoints(AddPointsRequestDto dto);
    Double useCoupon(UseCouponRequestDto dto);
}