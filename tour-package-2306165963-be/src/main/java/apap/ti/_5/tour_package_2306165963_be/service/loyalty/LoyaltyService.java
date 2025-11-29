package apap.ti._5.tour_package_2306165963_be.service.loyalty;

import apap.ti._5.tour_package_2306165963_be.dto.loyalty.request.*;
import apap.ti._5.tour_package_2306165963_be.dto.loyalty.response.*;

import java.util.List;

public interface LoyaltyService {
    
    // Coupon operations
    List<CouponResponseDto> getAllAvailableCoupons();
    CouponResponseDto getCouponById(String id);
    CouponResponseDto createCoupon(CreateCouponRequestDto request);
    CouponResponseDto updateCoupon(String id, UpdateCouponRequestDto request);
    void deleteCoupon(String id);
    
    // Customer operations
    CustomerResponseDto getCustomerPoints(String customerId);
    CustomerResponseDto addPoints(AddPointsRequestDto request);
    
    // Purchase & Usage
    List<PurchasedCouponResponseDto> getPurchasedCoupons(String customerId);
    List<PurchasedCouponResponseDto> getAvailableCoupons(String customerId);
    PurchasedCouponResponseDto purchaseCoupon(PurchaseCouponRequestDto request);
    UseCouponResponseDto useCoupon(UseCouponRequestDto request);
}