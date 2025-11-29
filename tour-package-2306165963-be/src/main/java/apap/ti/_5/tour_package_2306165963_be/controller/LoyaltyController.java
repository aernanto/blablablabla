package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.dto.loyalty.request.*;
import apap.ti._5.tour_package_2306165963_be.dto.loyalty.response.*;
import apap.ti._5.tour_package_2306165963_be.service.loyalty.LoyaltyService;
import apap.ti._5.tour_package_2306165963_be.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/loyalty")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private JwtUtils jwtUtils;
    
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    @GetMapping("/admin/coupons")
    public String viewAllCoupons(Model model, HttpServletRequest request) {
        List<CouponResponseDto> coupons = loyaltyService.getAllAvailableCoupons();
        model.addAttribute("listCoupon", coupons);
        model.addAttribute("currentUri", request.getRequestURI());
        return "loyalty/admin-coupons";
    }

    @GetMapping("/admin/coupons/create")
    public String formCreateCoupon(Model model, HttpServletRequest request) {
        model.addAttribute("isEdit", false);
        model.addAttribute("couponData", new CreateCouponRequestDto());
        model.addAttribute("currentUri", request.getRequestURI());
        return "loyalty/coupon-form";
    }

    @PostMapping("/admin/coupons/create")
    public String createCoupon(@Valid @ModelAttribute("couponData") CreateCouponRequestDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model,
                               HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("couponData", dto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ Please fix the validation errors.");
            return "loyalty/coupon-form";
        }

        try {
            loyaltyService.createCoupon(dto);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Coupon created successfully!");
            return "redirect:/loyalty/admin/coupons";
        } catch (Exception e) {
            model.addAttribute("isEdit", false);
            model.addAttribute("couponData", dto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ " + e.getMessage());
            return "loyalty/coupon-form";
        }
    }

    @GetMapping("/admin/coupons/{id}/edit")
    public String formEditCoupon(@PathVariable String id, Model model, HttpServletRequest request) {
        try {
            CouponResponseDto coupon = loyaltyService.getCouponById(id);
            
            UpdateCouponRequestDto dto = UpdateCouponRequestDto.builder()
                    .name(coupon.getName())
                    .description(coupon.getDescription())
                    .points(coupon.getPoints())
                    .percentOff(coupon.getPercentOff())
                    .build();
            
            model.addAttribute("isEdit", true);
            model.addAttribute("couponId", id);
            model.addAttribute("couponData", dto);
            model.addAttribute("currentUri", request.getRequestURI());
            return "loyalty/coupon-form";
        } catch (Exception e) {
            model.addAttribute("title", "Coupon Not Found");
            model.addAttribute("message", e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/admin/coupons/{id}/update")
    public String updateCoupon(@PathVariable String id,
                              @Valid @ModelAttribute("couponData") UpdateCouponRequestDto dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("couponId", id);
            model.addAttribute("couponData", dto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ Please fix the validation errors.");
            return "loyalty/coupon-form";
        }

        try {
            loyaltyService.updateCoupon(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Coupon updated successfully!");
            return "redirect:/loyalty/admin/coupons";
        } catch (Exception e) {
            model.addAttribute("isEdit", true);
            model.addAttribute("couponId", id);
            model.addAttribute("couponData", dto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ " + e.getMessage());
            return "loyalty/coupon-form";
        }
    }

    @PostMapping("/admin/coupons/{id}/delete")
    public String deleteCoupon(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            loyaltyService.deleteCoupon(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Coupon deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        return "redirect:/loyalty/admin/coupons";
    }

    @GetMapping
    public String loyaltyIndex(Model model, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                model.addAttribute("errorMessage", "Authentication required");
                return "loyalty/index";
            }
            
            String customerId = jwtUtils.getIdFromJwtToken(token);
            
            // Get customer points
            CustomerResponseDto customer = loyaltyService.getCustomerPoints(customerId);
            
            // Get available coupons
            List<CouponResponseDto> coupons = loyaltyService.getAllAvailableCoupons();
            
            model.addAttribute("customer", customer);
            model.addAttribute("coupons", coupons);
            model.addAttribute("currentUri", request.getRequestURI());
            return "loyalty/index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading loyalty page: " + e.getMessage());
            return "loyalty/index";
        }
    }

    @GetMapping("/my-coupons")
    public String myCoupons(Model model, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                model.addAttribute("errorMessage", "Authentication required");
                return "loyalty/my-coupons";
            }
            
            String customerId = jwtUtils.getIdFromJwtToken(token);
            
            List<PurchasedCouponResponseDto> purchasedCoupons = loyaltyService.getPurchasedCoupons(customerId);
            CustomerResponseDto customer = loyaltyService.getCustomerPoints(customerId);
            
            model.addAttribute("purchasedCoupons", purchasedCoupons);
            model.addAttribute("customer", customer);
            model.addAttribute("currentUri", request.getRequestURI());
            return "loyalty/my-coupons";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading your coupons: " + e.getMessage());
            return "loyalty/my-coupons";
        }
    }

    @PostMapping("/purchase")
    public String purchaseCoupon(@RequestParam String couponId,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Authentication required");
                return "redirect:/loyalty";
            }
            
            String customerId = jwtUtils.getIdFromJwtToken(token);
            
            PurchaseCouponRequestDto purchaseRequest = PurchaseCouponRequestDto.builder()
                    .customerId(customerId)
                    .couponId(couponId)
                    .build();
            
            PurchasedCouponResponseDto purchased = loyaltyService.purchaseCoupon(purchaseRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Coupon purchased! Your code: " + purchased.getUniqueCode());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Purchase failed: " + e.getMessage());
        }
        return "redirect:/loyalty";
    }

    @PostMapping("/add-points-test")
    public String addPointsTest(@RequestParam Integer points,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Authentication required");
                return "redirect:/loyalty";
            }
            
            String customerId = jwtUtils.getIdFromJwtToken(token);
            
            AddPointsRequestDto addPointsRequest = AddPointsRequestDto.builder()
                    .customerId(customerId)
                    .points(points)
                    .build();
            
            loyaltyService.addPoints(addPointsRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Added " + points + " points successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        return "redirect:/loyalty";
    }

    @PostMapping("/use-coupon")
    public String useCoupon(@RequestParam String code,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Authentication required");
                return "redirect:/loyalty/my-coupons";
            }
            
            String customerId = jwtUtils.getIdFromJwtToken(token);
            
            UseCouponRequestDto useCouponRequest = UseCouponRequestDto.builder()
                    .code(code)
                    .customerId(customerId)
                    .build();
            
            UseCouponResponseDto result = loyaltyService.useCoupon(useCouponRequest);
            
            if (result.getSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✅ Coupon applied! " + result.getFormattedDiscount() + " discount");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ " + result.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        return "redirect:/loyalty/my-coupons";
    }
}