package apap.ti._5.tour_package_2306165963_be.service.loyalty;

import apap.ti._5.tour_package_2306165963_be.dto.loyalty.*;
import apap.ti._5.tour_package_2306165963_be.dto.loyalty.request.*;
import apap.ti._5.tour_package_2306165963_be.dto.loyalty.response.*;
import apap.ti._5.tour_package_2306165963_be.model.loyalty.*;
import apap.ti._5.tour_package_2306165963_be.repository.loyalty.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {

    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PurchasedCouponRepository purchasedCouponRepository;
    
    @Autowired
    private LoyaltyMapper loyaltyMapper;
    
    @Override
    public List<CouponResponseDto> getAllAvailableCoupons() {
        return couponRepository.findByIsDeletedFalse()
                .stream()
                .map(loyaltyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponseDto getCouponById(String id) {
        Coupon coupon = couponRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        return loyaltyMapper.toResponseDto(coupon);
    }

    @Override
    public CouponResponseDto createCoupon(CreateCouponRequestDto request) {
        if (couponRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new IllegalArgumentException("Coupon with this name already exists");
        }
        
        Coupon coupon = loyaltyMapper.toEntity(request);
        Coupon saved = couponRepository.save(coupon);
        return loyaltyMapper.toResponseDto(saved);
    }

    @Override
    public CouponResponseDto updateCoupon(String id, UpdateCouponRequestDto request) {
        Coupon coupon = couponRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        
        loyaltyMapper.updateEntity(coupon, request);
        Coupon updated = couponRepository.save(coupon);
        return loyaltyMapper.toResponseDto(updated);
    }

    @Override
    public void deleteCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        
        coupon.setIsDeleted(true);
        couponRepository.save(coupon);
    }


    @Override
    public CustomerResponseDto getCustomerPoints(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElse(Customer.builder()
                        .id(customerId)
                        .name("Customer " + customerId)
                        .loyaltyPoints(0)
                        .build());
        
        return loyaltyMapper.toResponseDto(customer);
    }

    @Override
    public CustomerResponseDto addPoints(AddPointsRequestDto request) {
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElse(Customer.builder()
                        .id(request.getCustomerId())
                        .name("Customer " + request.getCustomerId())
                        .loyaltyPoints(0)
                        .build());
        
        customer.setLoyaltyPoints(
            (customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0) + request.getPoints()
        );
        
        Customer saved = customerRepository.save(customer);
        return loyaltyMapper.toResponseDto(saved);
    }

    @Override
    public List<PurchasedCouponResponseDto> getPurchasedCoupons(String customerId) {
        return purchasedCouponRepository.findByCustomerId(customerId)
                .stream()
                .map(loyaltyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchasedCouponResponseDto> getAvailableCoupons(String customerId) {
        return purchasedCouponRepository.findByCustomerIdAndIsUsedFalse(customerId)
                .stream()
                .map(loyaltyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchasedCouponResponseDto purchaseCoupon(PurchaseCouponRequestDto request) {
        // Get customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        // Get coupon
        Coupon coupon = couponRepository.findByIdAndIsDeletedFalse(request.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        
        // Check points
        if (customer.getLoyaltyPoints() < coupon.getPoints()) {
            throw new IllegalStateException("Insufficient loyalty points");
        }
        
        // Deduct points
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - coupon.getPoints());
        customerRepository.save(customer);
        
        // Generate unique code
        String uniqueCode = generateUniqueCode(coupon.getName(), customer.getName());
        
        // Create purchased coupon
        PurchasedCoupon purchasedCoupon = PurchasedCoupon.builder()
                .uniqueCode(uniqueCode)
                .customerId(customer.getId())
                .coupon(coupon)
                .isUsed(false)
                .build();
        
        PurchasedCoupon saved = purchasedCouponRepository.save(purchasedCoupon);
        return loyaltyMapper.toResponseDto(saved);
    }

    @Override
    public UseCouponResponseDto useCoupon(UseCouponRequestDto request) {
        PurchasedCoupon purchasedCoupon = purchasedCouponRepository.findByUniqueCode(request.getCode())
                .orElse(null);
        
        // Validations
        if (purchasedCoupon == null) {
            return UseCouponResponseDto.builder()
                    .code(request.getCode())
                    .discountPercent(0.0)
                    .success(false)
                    .message("Invalid coupon code")
                    .build();
        }
        
        if (!purchasedCoupon.getCustomerId().equals(request.getCustomerId())) {
            return UseCouponResponseDto.builder()
                    .code(request.getCode())
                    .discountPercent(0.0)
                    .success(false)
                    .message("This coupon does not belong to you")
                    .build();
        }
        
        if (purchasedCoupon.getIsUsed()) {
            return UseCouponResponseDto.builder()
                    .code(request.getCode())
                    .discountPercent(0.0)
                    .success(false)
                    .message("Coupon already used")
                    .build();
        }
        
        // Mark as used
        purchasedCoupon.setIsUsed(true);
        purchasedCoupon.setUsedDate(LocalDateTime.now());
        purchasedCouponRepository.save(purchasedCoupon);
        
        return UseCouponResponseDto.builder()
                .code(request.getCode())
                .discountPercent(purchasedCoupon.getCoupon().getPercentOff())
                .success(true)
                .message("Coupon applied successfully")
                .build();
    }

    private String generateUniqueCode(String couponName, String customerName) {
        long count = purchasedCouponRepository.count();
        
        String couponPart = couponName.length() > 5 
            ? couponName.substring(0, 5).toUpperCase().replace(" ", "") 
            : couponName.toUpperCase().replace(" ", "");
        
        String customerPart = customerName.length() > 5 
            ? customerName.substring(0, 5).toUpperCase().replace(" ", "") 
            : customerName.toUpperCase().replace(" ", "");
        
        String code = String.format("%s-%s-%d", couponPart, customerPart, count + 1);
        
        // Ensure uniqueness
        int attempt = 0;
        while (purchasedCouponRepository.existsByUniqueCode(code) && attempt < 100) {
            code = String.format("%s-%s-%d-%d", couponPart, customerPart, count + 1, attempt);
            attempt++;
        }
        
        return code;
    }
}