package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.orderedquantity.*;
import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.service.OrderedQuantityService;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import apap.ti._5.tour_package_2306165963_be.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderedQuantityRestController {

    @Autowired
    private OrderedQuantityService orderedQuantityService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private DtoMapper dtoMapper;
    
    @Autowired
    private JwtUtils jwtUtils;

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor')")
    @GetMapping("/ordered-quantities/{id}")
    public ResponseEntity<?> getById(@PathVariable String id,
                                     @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            Optional<OrderedQuantity> oq = orderedQuantityService.getOrderedQuantityById(id);
            
            if (oq.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Ordered quantity not found",
                        "timestamp", new Date()
                    ));
            }
            
            if (!canAccessOrderedQuantity(oq.get(), userId, role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You don't have access to this ordered quantity",
                        "timestamp", new Date()
                    ));
            }
            
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Successfully retrieved ordered quantity",
                "timestamp", new Date(),
                "data", dtoMapper.toReadDto(oq.get())
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
    @PostMapping("/plans/{planId}/ordered-quantities")
    public ResponseEntity<?> create(@PathVariable String planId,
                                    @Valid @RequestBody CreateOrderedQuantityDto dto,
                                    @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            if (!canAccessPlan(planId, userId, role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You don't have access to modify this plan",
                        "timestamp", new Date()
                    ));
            }
            
            OrderedQuantity oq = dtoMapper.toEntity(dto);
            OrderedQuantity saved = orderedQuantityService.createOrderedQuantity(planId, oq);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "status", HttpStatus.CREATED.value(),
                    "message", "Ordered quantity created successfully",
                    "timestamp", new Date(),
                    "data", dtoMapper.toReadDto(saved)
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

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor')")
    @PutMapping("/ordered-quantities/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestParam("newQuota") Integer newQuota,
                                    @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            Optional<OrderedQuantity> existingOpt = orderedQuantityService.getOrderedQuantityById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Ordered quantity not found",
                        "timestamp", new Date()
                    ));
            }
            
            if (!canAccessOrderedQuantity(existingOpt.get(), userId, role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You don't have access to modify this ordered quantity",
                        "timestamp", new Date()
                    ));
            }
            
            OrderedQuantity updated = orderedQuantityService.updateOrderedQuantity(id, newQuota);
            
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Ordered quantity updated successfully",
                "timestamp", new Date(),
                "data", dtoMapper.toReadDto(updated)
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

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor')")
    @DeleteMapping("/ordered-quantities/{id}")
    public ResponseEntity<?> delete(@PathVariable String id,
                                    @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            Optional<OrderedQuantity> existingOpt = orderedQuantityService.getOrderedQuantityById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Ordered quantity not found",
                        "timestamp", new Date()
                    ));
            }
            
            if (!canAccessOrderedQuantity(existingOpt.get(), userId, role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You don't have access to delete this ordered quantity",
                        "timestamp", new Date()
                    ));
            }
            
            boolean deleted = orderedQuantityService.deleteOrderedQuantity(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Ordered quantity deleted successfully",
                    "timestamp", new Date()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Ordered quantity not found",
                        "timestamp", new Date()
                    ));
            }
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "status", HttpStatus.CONFLICT.value(),
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

    private boolean canAccessOrderedQuantity(OrderedQuantity oq, String userId, String role) {
        // Superadmin and TourPackageVendor can access all
        if ("Superadmin".equals(role) || "TourPackageVendor".equals(role)) {
            return true;
        }
        
        // Customer can only access their own package's ordered quantities
        return canAccessPlan(oq.getPlanId(), userId, role);
    }
    
    private boolean canAccessPlan(String planId, String userId, String role) {
        // Superadmin and TourPackageVendor can access all
        if ("Superadmin".equals(role) || "TourPackageVendor".equals(role)) {
            return true;
        }

        return true; // Placeholder - should check package ownership
    }
}