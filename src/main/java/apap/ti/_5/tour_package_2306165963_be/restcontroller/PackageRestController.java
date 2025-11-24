package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.packagedto.*;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/packages")
public class PackageRestController {

    @Autowired private PackageService packageService;
    @Autowired private DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<ReadPackageDto>> getAll() {
        List<Package> list = packageService.getAllPackages();
        return ResponseEntity.ok(list.stream().map(dtoMapper::toReadDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadPackageDto> getById(@PathVariable String id) {
        return packageService.getPackageWithPlans(id)
                .map(p -> ResponseEntity.ok(dtoMapper.toReadDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReadPackageDto> create(@Valid @RequestBody CreatePackageDto dto) {
        Package pkg = dtoMapper.toEntity(dto);
        Package saved = packageService.createPackage(pkg);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toReadDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadPackageDto> update(@PathVariable String id, @Valid @RequestBody UpdatePackageDto dto) {
        Package pkg = dtoMapper.toEntity(dto);
        pkg.setId(id);
        Package updated = packageService.updatePackage(pkg);
        return ResponseEntity.ok(dtoMapper.toReadDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if(packageService.deletePackage(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<Void> process(@PathVariable String id) {
        packageService.processPackage(id);
        return ResponseEntity.ok().build();
    }
}