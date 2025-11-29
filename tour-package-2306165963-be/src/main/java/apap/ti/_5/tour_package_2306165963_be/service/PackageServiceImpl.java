package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private PlanRepository planRepository;

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public Optional<Package> getPackageById(String id) {
        return packageRepository.findById(id);
    }

    @Override
    public Optional<Package> getPackageWithPlans(String id) {
        return packageRepository.findByIdWithPlans(id);
    }

    @Override
    public Package createPackage(Package packageEntity) {
        validatePackage(packageEntity);
        packageEntity.setId(UUID.randomUUID().toString());
        packageEntity.setStatus("Pending");
        return packageRepository.save(packageEntity);
    }

    @Override
    public Package updatePackage(Package packageEntity) {
        Optional<Package> existingPackage = packageRepository.findById(packageEntity.getId());
        if (existingPackage.isEmpty()) {
            throw new IllegalArgumentException("Package not found with ID: " + packageEntity.getId());
        }
        
        Package existing = existingPackage.get();
        
        // Check if can be edited (only Pending or Processed)
        if (!"Pending".equals(existing.getStatus()) && !"Processed".equals(existing.getStatus())) {
            throw new IllegalStateException("Cannot update package with status: " + existing.getStatus());
        }
        
        validatePackage(packageEntity);
        
        existing.setUserId(packageEntity.getUserId());
        existing.setPackageName(packageEntity.getPackageName());
        existing.setQuota(packageEntity.getQuota());
        existing.setPrice(packageEntity.getPrice());
        existing.setStartDate(packageEntity.getStartDate());
        existing.setEndDate(packageEntity.getEndDate());
        
        return packageRepository.save(existing);
    }

    @Override
    public boolean deletePackage(String id) {
        Optional<Package> packageOptional = packageRepository.findById(id);
        
        if (packageOptional.isEmpty()) {
            return false;
        }
        
        Package packageEntity = packageOptional.get();
        
        // Can only delete if status = Pending
        if (!"Pending".equals(packageEntity.getStatus())) {
            throw new IllegalStateException("Can only delete packages with status Pending");
        }
        
        planRepository.deleteByPackageId(id);
        packageRepository.deleteById(id);
        return true;
    }

    @Override
    public void processPackage(String id) {
        Optional<Package> packageOptional = packageRepository.findByIdWithPlans(id);
        
        if (packageOptional.isEmpty()) {
            throw new IllegalArgumentException("Package not found with ID: " + id);
        }
        
        Package packageEntity = packageOptional.get();
        
        if (!"Pending".equals(packageEntity.getStatus())) {
            throw new IllegalStateException("Only Pending packages can be processed");
        }
        
        // Check if has plans
        if (packageEntity.getPlans() == null || packageEntity.getPlans().isEmpty()) {
            throw new IllegalStateException("Cannot process package without plans");
        }
        
        for (Plan plan : packageEntity.getPlans()) {
            if (!"Fulfilled".equals(plan.getStatus())) {
                throw new IllegalStateException("All plan statuses must be 'Fulfilled' before processing package. Plan " + plan.getId() + " has status: " + plan.getStatus());
            }
        }
        
        // Calculate total package price from all fulfilled plans
        long totalPrice = packageEntity.getPlans().stream()
                .mapToLong(plan -> plan.getPrice() != null ? plan.getPrice() : 0L)
                .sum();
        
        packageEntity.setPrice(totalPrice);
        packageEntity.setStatus("Processed");
        
        packageRepository.save(packageEntity);
    }

    @Override
    public List<Package> getPackagesByUserId(String userId) {
        return packageRepository.findByUserId(userId);
    }

    @Override
    public List<Package> getPackagesByStatus(String status) {
        return packageRepository.findByStatus(status);
    }

    private void validatePackage(Package packageEntity) {
        if (packageEntity.getEndDate().isBefore(packageEntity.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        if (packageEntity.getQuota() <= 0) {
            throw new IllegalArgumentException("Quota must be greater than 0");
        }
        
        if (packageEntity.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        if (packageEntity.getUserId() == null || packageEntity.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        if (packageEntity.getPackageName() == null || packageEntity.getPackageName().trim().isEmpty()) {
            throw new IllegalArgumentException("Package name is required");
        }
    }
}