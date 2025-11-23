package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class PackageServiceImpl implements PackageService {

    @Autowired private PackageRepository packageRepository;

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll(); // Sorting bisa ditambah di sini
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Package> getPackageById(String id) {
        return packageRepository.findById(id);
    }

    @Override
    public Optional<Package> getPackageWithPlans(String id) {
        // Asumsi method ini ada di repo (fetch join)
        return packageRepository.findByIdWithPlans(id); 
    }

    @Override
    public Package createPackage(Package pkg) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        pkg.setId("PKG-" + datePart + "-" + randomPart);
        
        pkg.setStatus("Pending"); // Default status
        
        if(pkg.getPlans() == null) pkg.setPlans(new ArrayList<>());
        
        // Validasi Basic
        if(pkg.getStartDate().isAfter(pkg.getEndDate())) throw new IllegalArgumentException("Start date > End date");
        
        return packageRepository.save(pkg);
    }

    @Override
    public Package updatePackage(Package pkg) {
        @SuppressWarnings("null")
        Package existing = packageRepository.findById(pkg.getId()).orElseThrow();
        
        if(!"Pending".equals(existing.getStatus())) {
            throw new IllegalStateException("Only Pending packages can be updated");
        }
        
        existing.setPackageName(pkg.getPackageName());
        existing.setQuota(pkg.getQuota());
        existing.setStartDate(pkg.getStartDate());
        existing.setEndDate(pkg.getEndDate());
        // Price dihitung otomatis sebenernya, tapi kalo mau override di sini bisa
        existing.setPrice(pkg.getPrice()); 
        
        return packageRepository.save(existing);
    }

    @Override
    public boolean deletePackage(String id) {
        @SuppressWarnings("null")
        Package pkg = packageRepository.findById(id).orElse(null);
        if(pkg == null) return false;
        if(!"Pending".equals(pkg.getStatus())) throw new IllegalStateException("Only Pending can be deleted");
        
        packageRepository.delete(pkg);
        return true;
    }

    @Override
    public void processPackage(String id) {
        Package pkg = packageRepository.findByIdWithPlans(id).orElseThrow();
        if(!"Pending".equals(pkg.getStatus())) throw new IllegalStateException("Only Pending can be processed");
        
        // Logic check plans fulfilled bisa ditaruh sini
        pkg.setStatus("Processed");
        packageRepository.save(pkg);
    }

    // Implementasi dummy method interface lain biar ga error
    @Override public List<Package> getPackagesByUserId(String userId) { return List.of(); }
    @Override public List<Package> getPackagesByStatus(String status) { return List.of(); }
}