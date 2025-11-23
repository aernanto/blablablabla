package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    @Autowired private ActivityRepository activityRepository;

    @Override
    public List<Activity> getAllActivities() {
        // Filter isDeleted = false
        return activityRepository.findByIsDeletedOrderByStartDateAsc(false);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Activity> getActivityById(String id) {
        return activityRepository.findById(id).filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()));
    }

    @Override
    public Activity createActivity(Activity activity) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        activity.setId("ACT-" + datePart + "-" + randomPart);
        activity.setIsDeleted(false);
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        @SuppressWarnings("null")
        Activity existing = activityRepository.findById(activity.getId()).orElseThrow();
        
        existing.setActivityName(activity.getActivityName());
        existing.setPrice(activity.getPrice());
        existing.setCapacity(activity.getCapacity());
        existing.setActivityItem(activity.getActivityItem());
        existing.setStartDate(activity.getStartDate());
        existing.setEndDate(activity.getEndDate());
        existing.setStartLocation(activity.getStartLocation());
        existing.setEndLocation(activity.getEndLocation());
        
        return activityRepository.save(existing);
    }

    @Override
    public boolean deleteActivity(String id) {
        @SuppressWarnings("null")
        Activity existing = activityRepository.findById(id).orElse(null);
        if(existing == null) return false;
        
        existing.setIsDeleted(true); // Soft Delete
        activityRepository.save(existing);
        return true;
    }

    @Override public List<Activity> getActivitiesByActivityType(String type) { return List.of(); }
    @Override public List<Activity> searchActivitiesByName(String name) { return List.of(); }
}