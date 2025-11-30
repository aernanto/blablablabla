package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.OrderedQuantityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private OrderedQuantityRepository orderedQuantityRepository;

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"))
                .stream()
                .filter(activity -> !activity.isDeleted()) // ✅ Only active activities
                .collect(Collectors.toList());
    }

    public List<Activity> getAllActivities(Boolean includeDeleted) {
        List<Activity> activities = activityRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
        
        if (includeDeleted != null && includeDeleted) {
            return activities; // Include deleted activities
        }
        
        return activities.stream()
                .filter(activity -> !activity.isDeleted())
                .collect(Collectors.toList());
    }
    
    public List<Activity> searchAndFilterActivities(
            String activityType,
            String startLocation,
            String endLocation,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String searchTerm) {
        
        List<Activity> activities = getAllActivities();
        
        if (activityType != null && !activityType.isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.getActivityType().equalsIgnoreCase(activityType))
                    .collect(Collectors.toList());
        }
        
        if (startLocation != null && !startLocation.isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.getStartLocation().toLowerCase().contains(startLocation.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (endLocation != null && !endLocation.isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.getEndLocation().toLowerCase().contains(endLocation.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (startDate != null) {
            activities = activities.stream()
                    .filter(a -> !a.getStartDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        
        if (endDate != null) {
            activities = activities.stream()
                    .filter(a -> !a.getEndDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String lowerSearchTerm = searchTerm.toLowerCase();
            activities = activities.stream()
                    .filter(a -> 
                        a.getActivityName().toLowerCase().contains(lowerSearchTerm) ||
                        a.getActivityItem().toLowerCase().contains(lowerSearchTerm)
                    )
                    .collect(Collectors.toList());
        }
        
        return activities;
    }

    @Override
    public Optional<Activity> getActivityById(String id) {
        return activityRepository.findById(id);
    }

    @Override
    public Activity createActivity(Activity activity) {
        validateActivity(activity);
        
        if (activity.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create activity in the past. Start date must be now or future.");
        }
        
        activity.setId(UUID.randomUUID().toString());
        activity.setDeleted(false);
        
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        Optional<Activity> existingActivity = activityRepository.findById(activity.getId());
        if (existingActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with ID: " + activity.getId());
        }
        
        Activity existing = existingActivity.get();
        
        if (existing.isDeleted()) {
            throw new IllegalStateException("Cannot update deleted activity");
        }
        
        boolean hasFulfilledOrders = orderedQuantityRepository.findByActivityId(activity.getId())
                .stream()
                .anyMatch(oq -> {
                    return true;
                });
        
        if (hasFulfilledOrders) {
            throw new IllegalStateException(
                "Cannot update activity that has fulfilled ordered activities. " +
                "This activity is already being used in completed plans."
            );
        }
        
        validateActivity(activity);
        
        if (activity.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date must be now or in the future");
        }
        
        existing.setActivityName(activity.getActivityName());
        existing.setActivityItem(activity.getActivityItem());
        existing.setCapacity(activity.getCapacity());
        existing.setPrice(activity.getPrice());
        existing.setStartDate(activity.getStartDate());
        existing.setEndDate(activity.getEndDate());
        existing.setStartLocation(activity.getStartLocation());
        existing.setEndLocation(activity.getEndLocation());
        
        return activityRepository.save(existing);
    }

    @Override
    public boolean deleteActivity(String id) {
        Optional<Activity> activityOpt = activityRepository.findById(id);
        
        if (activityOpt.isEmpty()) {
            return false;
        }
        
        Activity activity = activityOpt.get();
        
        boolean hasUnfulfilledOrders = orderedQuantityRepository.findByActivityId(id)
                .stream()
                .anyMatch(oq -> {
                    return true; 
                });
        
        if (hasUnfulfilledOrders) {
            throw new IllegalStateException(
                "Cannot delete activity that is used in unfulfilled plans. " +
                "Only activities with fulfilled or no orders can be deleted."
            );
        }
        
        activity.setDeleted(true);
        activityRepository.save(activity);
        
        return true;
    }

    @Override
    public List<Activity> getActivitiesByActivityType(String type) {
        return activityRepository.findByActivityType(type)
                .stream()
                .filter(activity -> !activity.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> searchActivitiesByName(String name) {
        return activityRepository.findByActivityNameContainingIgnoreCase(name)
                .stream()
                .filter(activity -> !activity.isDeleted())
                .collect(Collectors.toList());
    }

    
    private void validateActivity(Activity activity) {
        if (activity.getEndDate().isBefore(activity.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        if (activity.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        
        if (activity.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        
        if (!isValidActivityType(activity.getActivityType())) {
            throw new IllegalArgumentException("Invalid activity type: " + activity.getActivityType());
        }
    }
    
    private boolean isValidActivityType(String type) {
        return "Flight".equalsIgnoreCase(type) || 
               "Accommodation".equalsIgnoreCase(type) || 
               "Vehicle Rental".equalsIgnoreCase(type);
    }
}