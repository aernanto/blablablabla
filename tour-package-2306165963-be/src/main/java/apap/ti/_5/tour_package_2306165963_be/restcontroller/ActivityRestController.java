package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.activity.*;
import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.service.ActivityService;
import apap.ti._5.tour_package_2306165963_be.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private DtoMapper dtoMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor', 'FlightAirline', 'AccomodationOwner', 'RentalVendor')")
    @GetMapping
    public ResponseEntity<?> getAllActivities(
            @RequestParam(required = false) Boolean includeDeleted,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) String startLocation,
            @RequestParam(required = false) String endLocation,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String search) {
        try {
            List<ReadActivityDto> activities;
            
            if (activityType != null || startLocation != null || endLocation != null || 
                startDate != null || endDate != null || search != null) {
                
                activities = activityService.searchAndFilterActivities(
                        activityType, startLocation, endLocation, startDate, endDate, search)
                    .stream()
                    .map(dtoMapper::toReadDto)
                    .collect(Collectors.toList());
                    
            } else {

                activities = activityService.getAllActivities(includeDeleted)
                    .stream()
                    .map(dtoMapper::toReadDto)
                    .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Successfully retrieved activities",
                "timestamp", new Date(),
                "data", activities
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

    @PreAuthorize("hasAnyAuthority('Superadmin', 'Customer', 'TourPackageVendor', 'FlightAirline', 'AccomodationOwner', 'RentalVendor')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable String id) {
        try {
            Optional<Activity> activity = activityService.getActivityById(id);
            
            if (activity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Activity not found",
                        "timestamp", new Date()
                    ));
            }
            
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Successfully retrieved activity",
                "timestamp", new Date(),
                "data", dtoMapper.toReadDto(activity.get())
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

    @PreAuthorize("hasAnyAuthority('Superadmin', 'TourPackageVendor', 'FlightAirline', 'AccomodationOwner', 'RentalVendor')")
    @PostMapping
    public ResponseEntity<?> createActivity(@Valid @RequestBody CreateActivityDto dto,
                                           @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String vendorId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            if (!canVendorCreateActivityType(role, dto.getActivityType())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You are not authorized to create " + dto.getActivityType() + " activities",
                        "timestamp", new Date()
                    ));
            }
            
            Activity activity = dtoMapper.toEntity(dto);
            activity.setVendorId(vendorId); 
            
            Activity saved = activityService.createActivity(activity);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "status", HttpStatus.CREATED.value(),
                    "message", "Activity created successfully",
                    "timestamp", new Date(),
                    "data", dtoMapper.toReadDto(saved)
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

    @PreAuthorize("hasAnyAuthority('Superadmin', 'TourPackageVendor', 'FlightAirline', 'AccomodationOwner', 'RentalVendor')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable String id,
                                           @Valid @RequestBody UpdateActivityDto dto,
                                           @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String vendorId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            Optional<Activity> existingOpt = activityService.getActivityById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Activity not found",
                        "timestamp", new Date()
                    ));
            }
            
            Activity existing = existingOpt.get();
            
            if (!"Superadmin".equals(role) && !existing.getVendorId().equals(vendorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You can only update activities you created",
                        "timestamp", new Date()
                    ));
            }
            
            dto.setId(id);
            Activity activity = dtoMapper.toEntity(dto);
            Activity updated = activityService.updateActivity(activity);
            
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Activity updated successfully",
                "timestamp", new Date(),
                "data", dtoMapper.toReadDto(updated)
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "status", HttpStatus.CONFLICT.value(),
                    "message", e.getMessage(),
                    "timestamp", new Date()
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

    /**
     * ✅ DELETE with RBAC
     * - Vendors can only delete their own activities
     * - Superadmin can delete all activities
     */
    @PreAuthorize("hasAnyAuthority('Superadmin', 'TourPackageVendor', 'FlightAirline', 'AccomodationOwner', 'RentalVendor')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable String id,
                                           @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String vendorId = jwtUtils.getIdFromJwtToken(jwt);
            String role = jwtUtils.getRoleFromJwtToken(jwt);
            
            Optional<Activity> existingOpt = activityService.getActivityById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "Activity not found",
                        "timestamp", new Date()
                    ));
            }
            
            Activity existing = existingOpt.get();
            
            if (!"Superadmin".equals(role) && !existing.getVendorId().equals(vendorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "You can only delete activities you created",
                        "timestamp", new Date()
                    ));
            }
            
            activityService.deleteActivity(id);
            
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Activity deleted successfully",
                "timestamp", new Date()
            ));
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
    
    private boolean canVendorCreateActivityType(String role, String activityType) {
        switch (role) {
            case "Superadmin":
            case "TourPackageVendor":
                return true; // Can create all types
            case "FlightAirline":
                return "Flight".equalsIgnoreCase(activityType);
            case "AccomodationOwner":
                return "Accommodation".equalsIgnoreCase(activityType);
            case "RentalVendor":
                return "Vehicle Rental".equalsIgnoreCase(activityType);
            default:
                return false;
        }
    }
}