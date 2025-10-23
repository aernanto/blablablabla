package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final List<Activity> activityDB = new ArrayList<>();
    private final Faker faker = new Faker();
    private final String[] ACTIVITY_TYPES = {"Flight", "Accommodation", "Vehicle Rental"};
    
    private String getRandomLocation() {
        return faker.address().city() + ", " + faker.address().country();
    }

    public ActivityServiceImpl() {
        initializeDummyData();
    }

    private void initializeDummyData() {
        for (int i = 0; i < 15; i++) {
            String activityType = ACTIVITY_TYPES[i % 3];
            long basePrice = (long) (faker.number().numberBetween(500, 5000) * 1000);
            int capacity = faker.number().numberBetween(10, 50);

            LocalDateTime start = faker.date().future(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime end = start.plusDays(faker.number().numberBetween(1, activityType.equals("Flight") ? 0 : 7));
            end = activityType.equals("Flight") ? start.plusHours(faker.number().numberBetween(1, 10)) : end;


            Activity activity = Activity.builder()
                .id(UUID.randomUUID().toString())
                .activityType(activityType)
                .activityName(faker.company().name() + " " + activityType)
                .activityItem(faker.commerce().productName())
                .capacity(capacity)
                .price(basePrice)
                .startDate(start)
                .endDate(end)
                .startLocation(getRandomLocation()) 
                .endLocation(getRandomLocation()) 
                .build();
            
            activityDB.add(activity);
        }
    }

    @Override
    public List<Activity> getAllActivities() {
        return new ArrayList<>(activityDB);
    }

    @Override
    public Optional<Activity> getActivityById(String id) {
        return activityDB.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    @Override
    public Activity createActivity(Activity activity) {
        activity.setId(UUID.randomUUID().toString());
        if (activity.getCapacity() <= 0) activity.setCapacity(1); 
        if (activity.getPrice() == null) activity.setPrice(0L);
        if (activity.getActivityItem() == null) activity.setActivityItem("General Service");

        activityDB.add(activity);
        return activity;
    }

    @Override
    public Activity updateActivity(Activity updatedActivity) {
        Optional<Activity> activityOpt = getActivityById(updatedActivity.getId());

        if (activityOpt.isEmpty()) {
            throw new IllegalStateException("Activity not found.");
        }

        Activity existing = activityOpt.get();
        int index = activityDB.indexOf(existing);

        if (index != -1) {
            updatedActivity.setId(existing.getId()); 
            activityDB.set(index, updatedActivity);
        }

        return updatedActivity;
    }

    @Override
    public boolean deleteActivity(String id) {
        return activityDB.removeIf(a -> a.getId().equals(id));
    }

    @Override
    public List<Activity> getActivitiesByActivityType(String activityType) {
        return activityDB.stream()
                .filter(a -> a.getActivityType() != null && a.getActivityType().equalsIgnoreCase(activityType))
                .collect(Collectors.toList());
    }
}