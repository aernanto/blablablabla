package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.plan.*;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PlanRestController {

    @Autowired private PlanService planService;
    @Autowired private DtoMapper dtoMapper;

    // GET Plans by Package
    @GetMapping("/packages/{packageId}/plans")
    public ResponseEntity<List<ReadPlanDto>> getPlansByPackage(@PathVariable String packageId) {
        List<Plan> plans = planService.getPlansByPackageId(packageId);
        List<ReadPlanDto> dtos = plans.stream().map(dtoMapper::toReadDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/packages/{packageId}/plans")
    public ResponseEntity<ReadPlanDto> createPlan(@PathVariable String packageId, @Valid @RequestBody CreatePlanDto dto) {
        Plan plan = dtoMapper.toEntity(dto);
        // Validasi Activity Type & Package ID
        Plan saved = planService.createPlan(packageId, plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReadDto(saved));
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<ReadPlanDto> getPlanById(@PathVariable String id) {
        return planService.getPlanWithOrderedQuantities(id)
                .map(p -> ResponseEntity.ok(dtoMapper.toReadDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<ReadPlanDto> updatePlan(@PathVariable String id, @Valid @RequestBody UpdatePlanDto dto) {
        Plan plan = dtoMapper.toEntity(dto);
        plan.setId(id);
        Plan updated = planService.updatePlan(plan);
        return ResponseEntity.ok(dtoMapper.toReadDto(updated));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        if(planService.deletePlan(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}