package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.activity.*;
import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

    @Autowired private ActivityService activityService;
    @Autowired private DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<ReadActivityDto>> getAll() {
        return ResponseEntity.ok(activityService.getAllActivities().stream()
                .map(dtoMapper::toReadDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadActivityDto> getById(@PathVariable String id) {
        return activityService.getActivityById(id)
                .map(a -> ResponseEntity.ok(dtoMapper.toReadDto(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReadActivityDto> create(@Valid @RequestBody CreateActivityDto dto) {
        Activity activity = dtoMapper.toEntity(dto);
        Activity saved = activityService.createActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReadDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if(activityService.deleteActivity(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}