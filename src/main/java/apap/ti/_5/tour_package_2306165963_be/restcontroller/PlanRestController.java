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

    @GetMapping("/packages/{packageId}/plans")
    public ResponseEntity<List<ReadPlanDto>> getPlansByPackage(@PathVariable String packageId) {
        List<Plan> plans = planService.getPlansByPackageId(packageId);
        return ResponseEntity.ok(plans.stream().map(dtoMapper::toReadDto).collect(Collectors.toList()));
    }

    @PostMapping("/packages/{packageId}/plans")
    public ResponseEntity<ReadPlanDto> createPlan(@PathVariable String packageId, @Valid @RequestBody CreatePlanDto dto) {
        Plan plan = dtoMapper.toEntity(dto);
        Plan saved = planService.createPlan(packageId, plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReadDto(saved));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        if (planService.deletePlan(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}