package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PackageServiceImpl implements PackageService {

    private final List<Package> packageDB = new ArrayList<>();

    public PackageServiceImpl() {
        initializeDummyData();
    }

    private void initializeDummyData() {
        Package package1 = Package.builder()
                .id(UUID.randomUUID().toString())
                .userId("user001")
                .packageName("Jakarta - Bali Adventure Package")
                .quota(20)
                .price(18500000L)
                .status("Processed")
                .startDate(LocalDateTime.of(2025, 11, 1, 9, 0))
                .endDate(LocalDateTime.of(2025, 11, 7, 18, 0))
                .plans(new ArrayList<>())
                .build();

        Plan plan1 = Plan.builder()
                .id(UUID.randomUUID().toString())
                .packageId(package1.getId())
                .price(7200000L)
                .activityType("Flight")
                .status("Processed")
                .startDate(LocalDateTime.of(2025, 11, 1, 10, 0))
                .endDate(LocalDateTime.of(2025, 11, 1, 13, 30))
                .startLocation("Jakarta (CGK)")
                .endLocation("Bali (DPS)")
                .orderedQuantities(new ArrayList<>())
                .build();

        Plan plan2 = Plan.builder()
                .id(UUID.randomUUID().toString())
                .packageId(package1.getId())
                .price(9000000L)
                .activityType("Accommodation")
                .status("Processed")
                .startDate(LocalDateTime.of(2025, 11, 1, 15, 0))
                .endDate(LocalDateTime.of(2025, 11, 6, 12, 0))
                .startLocation("Kuta, Bali")
                .endLocation("Kuta, Bali")
                .orderedQuantities(new ArrayList<>())
                .build();

        package1.getPlans().add(plan1);
        package1.getPlans().add(plan2);
        packageDB.add(package1);

        Package package2 = Package.builder()
                .id(UUID.randomUUID().toString())
                .userId("user002")
                .packageName("Lombok Island Getaway")
                .quota(15)
                .price(12200000L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 12, 10, 8, 0))
                .endDate(LocalDateTime.of(2025, 12, 15, 20, 0))
                .plans(new ArrayList<>())
                .build();

        Plan plan3 = Plan.builder()
                .id(UUID.randomUUID().toString())
                .packageId(package2.getId())
                .price(5000000L)
                .activityType("Flight")
                .status("Unfinished")
                .startDate(LocalDateTime.of(2025, 12, 10, 9, 0))
                .endDate(LocalDateTime.of(2025, 12, 10, 11, 30))
                .startLocation("Jakarta (CGK)")
                .endLocation("Lombok (LOP)")
                .orderedQuantities(new ArrayList<>())
                .build();

        package2.getPlans().add(plan3);
        packageDB.add(package2);

        Package package3 = Package.builder()
                .id(UUID.randomUUID().toString())
                .userId("user001")
                .packageName("Surabaya - Malang City Break")
                .quota(10)
                .price(8500000L)
                .status("Pending")
                .startDate(LocalDateTime.of(2025, 10, 15, 7, 0))
                .endDate(LocalDateTime.of(2025, 10, 18, 22, 0))
                .plans(new ArrayList<>())
                .build();
        packageDB.add(package3);

        Package package4 = Package.builder()
                .id(UUID.randomUUID().toString())
                .userId("user004")
                .packageName("Yogyakarta Cultural Tour")
                .quota(30)
                .price(6500000L)
                .status("Processed")
                .startDate(LocalDateTime.of(2025, 11, 20, 6, 0))
                .endDate(LocalDateTime.of(2025, 11, 23, 20, 0))
                .plans(new ArrayList<>())
                .build();

        Plan plan4 = Plan.builder()
                .id(UUID.randomUUID().toString())
                .packageId(package4.getId())
                .price(3500000L)
                .activityType("Vehicle")
                .status("Processed")
                .startDate(LocalDateTime.of(2025, 11, 20, 8, 0))
                .endDate(LocalDateTime.of(2025, 11, 23, 18, 0))
                .startLocation("Yogyakarta Station")
                .endLocation("Yogyakarta Station")
                .orderedQuantities(new ArrayList<>())
                .build();

        package4.getPlans().add(plan4);
        packageDB.add(package4);
    }

    @Override
    public List<Package> getAllPackages() {
        return new ArrayList<>(packageDB);
    }

    @Override
    public Optional<Package> getPackageById(String id) {
        return packageDB.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public Package createPackage(Package pkg) {
        if (pkg.getStartDate() != null && pkg.getEndDate() != null && 
            pkg.getEndDate().isBefore(pkg.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        pkg.setId(UUID.randomUUID().toString());
        pkg.setStatus("Pending");
        pkg.setPlans(new ArrayList<>());
        packageDB.add(pkg);
        return pkg;
    }

    @Override
    public Package updatePackage(Package updatedPkg) {
        Optional<Package> pkgOpt = getPackageById(updatedPkg.getId());

        if (pkgOpt.isEmpty()) {
            throw new IllegalStateException("Package not found.");
        }

        Package existingPackage = pkgOpt.get();

        if (!"Pending".equals(existingPackage.getStatus())) {
            throw new IllegalStateException("Only packages with 'Pending' status can be edited.");
        }

        if (updatedPkg.getStartDate() != null && updatedPkg.getEndDate() != null && 
            updatedPkg.getEndDate().isBefore(updatedPkg.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        updatedPkg.setPlans(existingPackage.getPlans());
        updatedPkg.setStatus("Pending");

        int index = packageDB.indexOf(existingPackage);
        if (index != -1) {
            packageDB.set(index, updatedPkg);
        }

        return updatedPkg;
    }

    @Override
    public boolean deletePackage(String id) {
        return packageDB.removeIf(p -> p.getId().equals(id));
    }

    @Override
    public Package processPackage(String id) {
        Optional<Package> packageOptional = getPackageById(id);

        if (packageOptional.isEmpty()) {
            throw new IllegalStateException("Package not found.");
        }

        Package pkg = packageOptional.get();

        if (pkg.getPlans().isEmpty()) {
            throw new IllegalStateException("Cannot process package: No plans added yet.");
        }

        boolean allPlansProcessed = pkg.getPlans().stream()
                .allMatch(p -> "Processed".equals(p.getStatus()));

        if (!allPlansProcessed) {
            throw new IllegalStateException("Cannot process package: Not all plans are processed.");
        }

        pkg.setStatus("Processed");
        return pkg;
    }
}