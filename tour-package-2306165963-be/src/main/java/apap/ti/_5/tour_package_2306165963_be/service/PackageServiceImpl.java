package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private PlanRepository planRepository;

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
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
        
        if (packageEntity.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date must be in the future or now");
        }
        
        packageEntity.setId(generatePackageId());
        packageEntity.setStatus("Pending");
        packageEntity.setDeleted(false);
        return packageRepository.save(packageEntity);
    }

    @Override
    public Package updatePackage(Package packageEntity) {
        Optional<Package> existingPackage = packageRepository.findById(packageEntity.getId());
        if (existingPackage.isEmpty()) {
            throw new IllegalArgumentException("Package not found with ID: " + packageEntity.getId());
        }
        
        Package existing = existingPackage.get();
        
        if (!"Pending".equals(existing.getStatus()) && !"Processed".equals(existing.getStatus())) {
            throw new IllegalStateException("Cannot update package with status: " + existing.getStatus());
        }
        
        validatePackage(packageEntity);
        
        if (packageEntity.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date must be in the future or now");
        }
        
        existing.setUserId(packageEntity.getUserId());
        existing.setPackageName(packageEntity.getPackageName());
        existing.setQuota(packageEntity.getQuota());
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
        
        if (!"Pending".equals(packageEntity.getStatus())) {
            throw new IllegalStateException("Can only delete packages with status Pending");
        }
        
        packageEntity.setDeleted(true);
        packageRepository.save(packageEntity);
        
        planRepository.deleteByPackageId(id);
        
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
        
        if (packageEntity.getPlans() == null || packageEntity.getPlans().isEmpty()) {
            throw new IllegalStateException("Cannot process package without plans");
        }

        boolean allPlansFulfilled = packageEntity.getPlans().stream()
                .allMatch(plan -> "Fulfilled".equals(plan.getStatus()));
        
        if (!allPlansFulfilled) {
            String unfulfilledPlans = packageEntity.getPlans().stream()
                    .filter(plan -> !"Fulfilled".equals(plan.getStatus()))
                    .map(plan -> "Plan " + plan.getId() + " (status: " + plan.getStatus() + ")")
                    .collect(java.util.stream.Collectors.joining(", "));
        
            throw new IllegalStateException(
                "All plan statuses must be 'Fulfilled' before processing package. " +
                "Unfulfilled plans: " + unfulfilledPlans
            );
        }
        
        for (Plan plan : packageEntity.getPlans()) {
            for (OrderedQuantity oq : plan.getOrderedQuantities()) {
                if (oq.getOrderedQuota() > oq.getQuota()) {
                    throw new IllegalStateException(
                        "OrderedQuantity exceeds Activity Capacity for activity: " + oq.getActivityName()
                    );
                }
            }
        }
        
        long totalPrice = packageEntity.getPlans().stream()
                .mapToLong(plan -> plan.getPrice() != null ? plan.getPrice() : 0L)
                .sum();
        
        packageEntity.setPrice(totalPrice);
        
        packageEntity.setStatus("Waiting for Payment");
        
        packageRepository.save(packageEntity);
        
        // ⚠️ TODO: Create bill on bill module (not implemented yet)
        // billService.createBillForPackage(packageEntity);
    }

    @Override
    public List<Package> getPackagesByUserId(String userId) {
        return packageRepository.findByUserId(userId);
    }

    @Override
    public List<Package> getPackagesByStatus(String status) {
        return packageRepository.findByStatus(status);
    }

    @Override
    public List<Package> getPackagesByVendorId(String vendorId) {
        return packageRepository.findPackagesByVendorId(vendorId);
    }

    @Override
    public Package getPackageDetail(String packageId, String userId, String role) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));

        boolean isOwner = pkg.getUserId().equals(userId);
        boolean isSuperadmin = "Superadmin".equals(role);
        boolean isVendor = "TourPackageVendor".equals(role);

        if (!isOwner && !isSuperadmin && !isVendor) {
            throw new IllegalStateException("You don't have access to this package");
        }

        return pkg;
    }

    @Override
    public List<Package> getAllPackagesWithRBAC(String userId, String role) {
        if ("Superadmin".equals(role) || "TourPackageVendor".equals(role)) {
            return getAllPackages();
        } else {
            return packageRepository.findByUserIdOrIsPublic(userId);
        }
    }

    // Helper methods
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

    private String generatePackageId() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = packageRepository.countByIdStartingWith("PKG-" + date);
        return String.format("PKG-%s-%03d", date, count + 1);
    }
}