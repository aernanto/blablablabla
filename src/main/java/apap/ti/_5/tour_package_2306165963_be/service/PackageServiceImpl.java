package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import apap.ti._5.tour_package_2306165963_be.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class PackageServiceImpl implements PackageService {
    @Autowired private PackageRepository packageRepository;
    @Autowired private PlanRepository planRepository;

    @Override
    public List<Package> getAllPackages() {
        // AC: Sorting startDate ascending
        List<Package> list = packageRepository.findAll();
        list.sort(Comparator.comparing(Package::getStartDate));
        return list;
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Package> getPackageById(String id) { return packageRepository.findById(id); }

    @Override
    public Optional<Package> getPackageWithPlans(String id) { return packageRepository.findByIdWithPlans(id); }

    @Override
    public Package createPackage(Package pkg) {
        // AC: ID PKG-{YYYYMMDD}-{XXX}
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        pkg.setId("PKG-" + datePart + "-" + randomPart);
        pkg.setStatus("Pending");
        if(pkg.getPlans() == null) pkg.setPlans(new ArrayList<>());
        return packageRepository.save(pkg);
    }

    @Override
    public Package updatePackage(Package pkg) {
        @SuppressWarnings("null")
        Package existing = packageRepository.findById(pkg.getId()).orElseThrow();
        if(!"Pending".equals(existing.getStatus())) throw new IllegalStateException("Only Pending");
        existing.setPackageName(pkg.getPackageName());
        existing.setQuota(pkg.getQuota());
        existing.setStartDate(pkg.getStartDate());
        existing.setEndDate(pkg.getEndDate());
        return packageRepository.save(existing);
    }

    @Override
    public boolean deletePackage(String id) {
        @SuppressWarnings("null")
        Package pkg = packageRepository.findById(id).orElse(null);
        if(pkg == null) return false;
        if(!"Pending".equals(pkg.getStatus())) throw new IllegalStateException("Only Pending");
        
        planRepository.deleteByPackageId(id);
        packageRepository.delete(pkg);
        return true;
    }

    @Override
    public void processPackage(String id) {
        Package pkg = packageRepository.findByIdWithPlans(id).orElseThrow();
        if(!"Pending".equals(pkg.getStatus())) throw new IllegalStateException("Only Pending");
        
        // AC: Ubah status
        pkg.setStatus("Processed");
        packageRepository.save(pkg);
    }

    // Dummy
    @Override public List<Package> getPackagesByUserId(String u) { return List.of(); }
    @Override public List<Package> getPackagesByStatus(String s) { return List.of(); }
}