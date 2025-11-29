package apap.ti._5.tour_package_2306165963_be.dto.loyalty;

import apap.ti._5.tour_package_2306165963_be.dto.loyalty.request.*;
import apap.ti._5.tour_package_2306165963_be.dto.loyalty.response.*;
import apap.ti._5.tour_package_2306165963_be.model.loyalty.*;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyMapper {
    
    // Coupon mappings
    public Coupon toEntity(CreateCouponRequestDto dto) {
        return Coupon.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .points(dto.getPoints())
                .percentOff(dto.getPercentOff())
                .isDeleted(false)
                .build();
    }
    
    public void updateEntity(Coupon coupon, UpdateCouponRequestDto dto) {
        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setPoints(dto.getPoints());
        coupon.setPercentOff(dto.getPercentOff());
    }
    
    public CouponResponseDto toResponseDto(Coupon coupon) {
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .points(coupon.getPoints())
                .percentOff(coupon.getPercentOff())
                .isDeleted(coupon.getIsDeleted())
                .createdDate(coupon.getCreatedDate())
                .updatedDate(coupon.getUpdatedDate())
                .build();
    }
    
    // PurchasedCoupon mappings
    public PurchasedCouponResponseDto toResponseDto(PurchasedCoupon purchasedCoupon) {
        return PurchasedCouponResponseDto.builder()
                .id(purchasedCoupon.getId())
                .uniqueCode(purchasedCoupon.getUniqueCode())
                .customerId(purchasedCoupon.getCustomerId())
                .coupon(toResponseDto(purchasedCoupon.getCoupon()))
                .isUsed(purchasedCoupon.getIsUsed())
                .purchasedDate(purchasedCoupon.getPurchasedDate())
                .usedDate(purchasedCoupon.getUsedDate())
                .build();
    }
    
    // Customer mappings
    public CustomerResponseDto toResponseDto(Customer customer) {
        return CustomerResponseDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .build();
    }
}