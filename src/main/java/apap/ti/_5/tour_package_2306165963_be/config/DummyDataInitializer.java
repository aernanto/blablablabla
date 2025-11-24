package apap.ti._5.tour_package_2306165963_be.config;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.model.Customer;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.repository.ActivityRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306165963_be.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

// @Component
public class DummyDataInitializer implements CommandLineRunner {

    @Autowired private PackageRepository packageRepository;
    @Autowired private ActivityRepository activityRepository;
        @Autowired private CustomerRepository customerRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (packageRepository.count() > 0) {
            System.out.println("✅ Data already exists. Skipping initialization.");
            return;
        }

        System.out.println("Generating Dummy Data...");

        // 1. Activity
        Activity act = new Activity();
        act.setId(UUID.randomUUID().toString()); 
        act.setActivityName("Garuda Flight Executive");
        act.setActivityType("Flight");
        act.setActivityItem("Business Class Seat");
        act.setPrice(2500000L);
        act.setCapacity(50);
        act.setStartDate(LocalDate.now().plusDays(10).atStartOfDay());
        act.setEndDate(LocalDate.now().plusDays(10).atTime(2,0));
        act.setStartLocation("Jakarta");
        act.setEndLocation("Bali");
        act.setIsDeleted(false);
        activityRepository.save(act);

        // 2. Package
        Package pkg = new Package();
        pkg.setId("PKG-" + System.currentTimeMillis()); 
        pkg.setUserId("admin");
        pkg.setPackageName("Bali Luxury Trip");
        pkg.setQuota(10);
        pkg.setPrice(5000000L);
        pkg.setStatus("Pending");
        pkg.setStartDate(LocalDate.now().plusDays(20).atStartOfDay());
        pkg.setEndDate(LocalDate.now().plusDays(25).atStartOfDay());
        pkg.setPlans(new ArrayList<>());
        packageRepository.save(pkg);
        
        System.out.println("Dummy Data Ready!");

        // 3. Customer & Points
        Customer cust = new Customer();
        cust.setId("user-1");
        cust.setName("Budi Santoso");
        cust.setLoyaltyPoints(1000); 
        customerRepo.save(cust);

        System.out.println("Data Ready with Customer Points!");
    }
}