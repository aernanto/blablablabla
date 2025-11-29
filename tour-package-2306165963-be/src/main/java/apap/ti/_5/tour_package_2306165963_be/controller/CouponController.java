package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.dto.coupon.*;
import apap.ti._5.tour_package_2306165963_be.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Tetap pakai ini biar return JSON (bukan HTML)
@RequestMapping("/api")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/coupons")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(couponService.getAllAvailableCoupons());
    }

    @GetMapping("/my-coupons")
    public ResponseEntity<?> getMyCoupons(@RequestParam String customerId) {
        return ResponseEntity.ok(couponService.getPurchasedCoupons(customerId));
    }

    @GetMapping("/loyalty-points")
    public ResponseEntity<?> getPoints(@RequestParam String customerId) {
        return ResponseEntity.ok(couponService.getCustomerPoints(customerId));
    }

    @PostMapping("/coupons")
    public ResponseEntity<?> create(@RequestBody CouponRequestDto dto) {
        return ResponseEntity.ok(couponService.createCoupon(dto));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CouponRequestDto dto) {
        return ResponseEntity.ok(couponService.updateCoupon(id, dto));
    }

    @PostMapping("/purchase-coupon")
    public ResponseEntity<?> purchase(@RequestBody PurchaseRequestDto dto) {
        try {
            return ResponseEntity.ok(couponService.purchaseCoupon(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Return error message if points insufficient
        }
    }
    
    @PostMapping("/add-points")
    public ResponseEntity<?> addPoints(@RequestBody AddPointsRequestDto dto) {
        return ResponseEntity.ok(couponService.addPoints(dto));
    }
    
    @PostMapping("/use-coupon")
    public ResponseEntity<?> useCoupon(@RequestBody UseCouponRequestDto dto) {
        return ResponseEntity.ok(couponService.useCoupon(dto));
    }
}