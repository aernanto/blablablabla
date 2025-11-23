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
    public ResponseEntity<List<ReadActivityDto>> getAllActivities() {
        List<Activity> list = activityService.getAllActivities();
        return ResponseEntity.ok(list.stream().map(dtoMapper::toReadDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadActivityDto> getActivityById(@PathVariable String id) {
        return activityService.getActivityById(id)
                .map(a -> ResponseEntity.ok(dtoMapper.toReadDto(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReadActivityDto> createActivity(@Valid @RequestBody CreateActivityDto dto) {
        Activity activity = dtoMapper.toEntity(dto);
        Activity saved = activityService.createActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReadDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadActivityDto> updateActivity(@PathVariable String id, @Valid @RequestBody UpdateActivityDto dto) {
        Activity activity = dtoMapper.toEntity(dto);
        activity.setId(id);
        Activity updated = activityService.updateActivity(activity);
        return ResponseEntity.ok(dtoMapper.toReadDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id) {
        if(activityService.deleteActivity(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}