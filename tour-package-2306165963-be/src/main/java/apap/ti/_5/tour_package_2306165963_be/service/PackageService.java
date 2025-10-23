package apap.ti._5.tour_package_2306165963_be.service;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import java.util.List;
import java.util.Optional;

public interface PackageService {
    List<Package> getAllPackages();
    Optional<Package> getPackageById(String id);
    Package createPackage(Package pkg);
    Package updatePackage(Package pkg);
    boolean deletePackage(String id);
    Package processPackage(String id);
}