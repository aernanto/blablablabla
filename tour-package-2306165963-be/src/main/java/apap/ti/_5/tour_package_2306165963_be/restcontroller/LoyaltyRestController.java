package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.loyalty.request.*;
import apap.ti._5.tour_package_2306165963_be.dto.loyalty.response.*;
import apap.ti._5.tour_package_2306165963_be.service.loyalty.LoyaltyService;
import apap.ti._5.tour_package_2306165963_be.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyRestController {

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private JwtUtils jwtUtils;

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor')")
    @GetMapping("/coupons")
    public ResponseEntity<?> getAllCoupons() {
        try {
            List<CouponResponseDto> coupons = loyaltyService.getAllAvailableCoupons();
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Successfully retrieved all coupons",
                    "timestamp", new Date(),
                    "data", coupons
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor')")
    @GetMapping("/coupons/{id}")
    public ResponseEntity<?> getCouponById(@PathVariable String id) {
        try {
            CouponResponseDto coupon = loyaltyService.getCouponById(id);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Successfully retrieved coupon",
                    "timestamp", new Date(),
                    "data", coupon
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAuthority('Superadmin')")
    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {
        try {
            CouponResponseDto coupon = loyaltyService.createCoupon(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", HttpStatus.CREATED.value(),
                            "message", "Coupon created successfully",
                            "timestamp", new Date(),
                            "data", coupon
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAuthority('Superadmin')")
    @PutMapping("/coupons/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable String id,
                                          @Valid @RequestBody UpdateCouponRequestDto request) {
        try {
            CouponResponseDto coupon = loyaltyService.updateCoupon(id, request);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Coupon updated successfully",
                    "timestamp", new Date(),
                    "data", coupon
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAuthority('Superadmin')")
    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable String id) {
        try {
            loyaltyService.deleteCoupon(id);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Coupon deleted successfully",
                    "timestamp", new Date()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAnyAuthority('Customer', 'TourPackageVendor', 'Superadmin')")
    @GetMapping("/points")
    public ResponseEntity<?> getMyPoints(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtUtils.getIdFromJwtToken(token);

            CustomerResponseDto customer = loyaltyService.getCustomerPoints(customerId);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Successfully retrieved loyalty points",
                    "timestamp", new Date(),
                    "data", customer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAuthority('Superadmin')")
    @GetMapping("/points/{customerId}")
    public ResponseEntity<?> getCustomerPoints(@PathVariable String customerId) {
        try {
            CustomerResponseDto customer = loyaltyService.getCustomerPoints(customerId);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Successfully retrieved customer points",
                    "timestamp", new Date(),
                    "data", customer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/points/add")
    public ResponseEntity<?> addPoints(@Valid @RequestBody AddPointsRequestDto request) {
        try {
            CustomerResponseDto customer = loyaltyService.addPoints(request);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Points added successfully",
                    "timestamp", new Date(),
                    "data", customer
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }


    @PreAuthorize("hasAnyAuthority('Customer', 'TourPackageVendor', 'Superadmin')")
    @GetMapping("/my-coupons")
    public ResponseEntity<?> getMyCoupons(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtUtils.getIdFromJwtToken(token);

            List<PurchasedCouponResponseDto> coupons = loyaltyService.getPurchasedCoupons(customerId);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Successfully retrieved purchased coupons",
                    "timestamp", new Date(),
                    "data", coupons
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }


    @PreAuthorize("hasAnyAuthority('Customer', 'TourPackageVendor', 'Superadmin')")
    @GetMapping("/my-coupons/available")
    public ResponseEntity<?> getMyAvailableCoupons(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtUtils.getIdFromJwtToken(token);

            List<PurchasedCouponResponseDto> coupons = loyaltyService.getAvailableCoupons(customerId);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Successfully retrieved available coupons",
                    "timestamp", new Date(),
                    "data", coupons
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAnyAuthority('Customer', 'TourPackageVendor')")
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseCoupon(@Valid @RequestBody PurchaseCouponRequestDto request,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtUtils.getIdFromJwtToken(token);

            // Ensure user can only purchase for themselves
            if (!customerId.equals(request.getCustomerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", HttpStatus.FORBIDDEN.value(),
                                "message", "You can only purchase coupons for yourself",
                                "timestamp", new Date()
                        ));
            }

            PurchasedCouponResponseDto purchased = loyaltyService.purchaseCoupon(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", HttpStatus.CREATED.value(),
                            "message", "Coupon purchased successfully",
                            "timestamp", new Date(),
                            "data", purchased
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PreAuthorize("hasAnyAuthority('Customer', 'TourPackageVendor')")
    @PostMapping("/use")
    public ResponseEntity<?> useCoupon(@Valid @RequestBody UseCouponRequestDto request,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtUtils.getIdFromJwtToken(token);

            // Ensure user can only use their own coupons
            if (!customerId.equals(request.getCustomerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", HttpStatus.FORBIDDEN.value(),
                                "message", "You can only use your own coupons",
                                "timestamp", new Date()
                        ));
            }

            UseCouponResponseDto result = loyaltyService.useCoupon(request);
            
            if (result.getSuccess()) {
                return ResponseEntity.ok(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", result.getMessage(),
                        "timestamp", new Date(),
                        "data", result
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "message", result.getMessage(),
                                "timestamp", new Date(),
                                "data", result
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Error: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }
}