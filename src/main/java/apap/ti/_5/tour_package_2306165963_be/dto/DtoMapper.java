package apap.ti._5.tour_package_2306165963_be.dto;

import apap.ti._5.tour_package_2306165963_be.dto.activity.*;
import apap.ti._5.tour_package_2306165963_be.dto.orderedquantity.*;
import apap.ti._5.tour_package_2306165963_be.dto.packagedto.*;
import apap.ti._5.tour_package_2306165963_be.dto.plan.*;
import apap.ti._5.tour_package_2306165963_be.model.*;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    // Activity
    public Activity toEntity(CreateActivityDto dto) {
        Activity a = new Activity();
        a.setActivityName(dto.getActivityName());
        a.setActivityType(dto.getActivityType());
        a.setActivityItem(dto.getActivityItem());
        a.setCapacity(dto.getCapacity());
        a.setPrice(dto.getPrice());
        a.setStartDate(dto.getStartDate()); // Asumsi tipe data cocok (LocalDate/LocalDateTime)
        a.setEndDate(dto.getEndDate());
        a.setStartLocation(dto.getStartLocation());
        a.setEndLocation(dto.getEndLocation());
        return a;
    }
    
    public Activity toEntity(UpdateActivityDto dto) {
        Activity a = new Activity();
        a.setId(dto.getId());
        a.setActivityName(dto.getActivityName());
        a.setActivityItem(dto.getActivityItem());
        a.setCapacity(dto.getCapacity());
        a.setPrice(dto.getPrice());
        a.setStartDate(dto.getStartDate());
        a.setEndDate(dto.getEndDate());
        a.setStartLocation(dto.getStartLocation());
        a.setEndLocation(dto.getEndLocation());
        // Activity Type tidak diupdate sesuai AC
        return a;
    }

    public ReadActivityDto toReadDto(Activity entity) {
        return new ReadActivityDto(
            entity.getId(), entity.getActivityName(), entity.getActivityItem(),
            entity.getCapacity(), entity.getPrice(), entity.getActivityType(),
            entity.getStartDate(), entity.getEndDate(),
            entity.getStartLocation(), entity.getEndLocation()
        );
    }

    // Package
    public Package toEntity(CreatePackageDto dto) {
        Package p = new Package();
        p.setUserId(dto.getUserId());
        p.setPackageName(dto.getPackageName());
        p.setQuota(dto.getQuota());
        p.setPrice(dto.getPrice());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        return p;
    }

    public Package toEntity(UpdatePackageDto dto) {
        Package p = new Package();
        p.setId(dto.getUserId()); // Pastikan di DTO ada field ID
        p.setPackageName(dto.getPackageName());
        p.setQuota(dto.getQuota());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        // p.setPrice(dto.getPrice());
        return p;
    }

    public ReadPackageDto toReadDto(Package entity) {
        ReadPackageDto dto = new ReadPackageDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setPackageName(entity.getPackageName());
        dto.setQuota(entity.getQuota());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        
        if (entity.getPlans() != null) {
            dto.setPlans(entity.getPlans().stream().map(this::toReadDto).collect(Collectors.toList()));
        } else {
            dto.setPlans(new ArrayList<>());
        }
        return dto;
    }

    // Plan
    public Plan toEntity(CreatePlanDto dto) {
        Plan p = new Plan();
        p.setActivityType(dto.getActivityType());
        p.setPrice(dto.getPrice());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setStartLocation(dto.getStartLocation());
        p.setEndLocation(dto.getEndLocation());
        return p;
    }
    
    public Plan toEntity(UpdatePlanDto dto) {
        Plan p = new Plan();
        p.setId(dto.getId());
        p.setPrice(dto.getPrice());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setStartLocation(dto.getStartLocation());
        p.setEndLocation(dto.getEndLocation());
        return p;
    }

    public ReadPlanDto toReadDto(Plan entity) {
        ReadPlanDto dto = new ReadPlanDto();
        dto.setId(entity.getId());
        dto.setPackageId(entity.getPackageId());
        dto.setActivityType(entity.getActivityType());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStartLocation(entity.getStartLocation());
        dto.setEndLocation(entity.getEndLocation());

        if (entity.getOrderedQuantities() != null) {
            dto.setOrderedQuantities(entity.getOrderedQuantities().stream().map(this::toReadDto).collect(Collectors.toList()));
        } else {
            dto.setOrderedQuantities(new ArrayList<>());
        }
        return dto;
    }

    // OrderedQuantity
    public OrderedQuantity toEntity(CreateOrderedQuantityDto dto) {
        OrderedQuantity oq = new OrderedQuantity();
        oq.setActivityId(dto.getActivityId());
        oq.setOrderedQuota(dto.getOrderedQuota());
        return oq;
    }

    public ReadOrderedQuantityDto toReadDto(OrderedQuantity entity) {
        return ReadOrderedQuantityDto.builder()
            .id(entity.getId())
            .planId(entity.getPlanId())
            .activityId(entity.getActivityId())
            .orderedQuota(entity.getOrderedQuota())
            .quota(entity.getQuota())
            .price(entity.getPrice())
            .activityName(entity.getActivityName())
            .activityItem(entity.getActivityItem())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .build();
    }
}