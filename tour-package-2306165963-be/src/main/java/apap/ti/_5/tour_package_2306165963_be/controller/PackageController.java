package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.dto.DtoMapper;
import apap.ti._5.tour_package_2306165963_be.dto.packagedto.*;
import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/package")
public class PackageController {

    @Autowired
    private PackageService packageService;
    
    @Autowired
    private DtoMapper dtoMapper;

    @GetMapping
    public String getAllPackages(Model model, HttpServletRequest request, Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : "";
        String role = getUserRole(authentication);
        
        List<ReadPackageDto> packages = packageService.getAllPackagesWithRBAC(userId, role)
                .stream()
                .map(pkg -> {
                    if (pkg.getPlans() == null) {
                        pkg.setPlans(new ArrayList<>());
                    }
                    return dtoMapper.toReadDto(pkg);
                })
                .collect(Collectors.toList());
        
        model.addAttribute("listPackage", packages);
        model.addAttribute("currentUri", request.getRequestURI());
        return "package/view-all";
    }


    @GetMapping("/{id}")
    public String getPackageById(@PathVariable String id, Model model, HttpServletRequest request, 
                                 Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : "";
        String role = getUserRole(authentication);
        
        try {
            Package pkg = packageService.getPackageDetail(id, userId, role);
            
            ReadPackageDto packageDto = dtoMapper.toReadDto(pkg);
            model.addAttribute("currentPackage", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            return "package/detail";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("title", "Package Not Found");
            model.addAttribute("message", e.getMessage());
            return "error/404";
        } catch (IllegalStateException e) {
            model.addAttribute("title", "Access Denied");
            model.addAttribute("message", e.getMessage());
            return "error/403";
        }
    }

    @GetMapping("/create")
    public String formCreatePackage(Model model, HttpServletRequest request) {
        model.addAttribute("isEdit", false);
        model.addAttribute("packageData", new CreatePackageDto());
        model.addAttribute("currentUri", request.getRequestURI());
        return "package/form";
    }

    @PostMapping("/create")
    public String createPackage(@Valid @ModelAttribute("packageData") CreatePackageDto packageDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model,
                                HttpServletRequest request,
                                Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("packageData", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ Please fix the validation errors.");
            return "package/form";
        }
        
        try {
            String userId = authentication != null ? authentication.getName() : packageDto.getUserId();
            packageDto.setUserId(userId);
            
            Package packageEntity = dtoMapper.toEntity(packageDto);
            packageService.createPackage(packageEntity);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Package created successfully!");
            return "redirect:/package";
        } catch (Exception e) {
            model.addAttribute("isEdit", false);
            model.addAttribute("packageData", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ " + e.getMessage());
            return "package/form";
        }
    }

    @GetMapping("/update/{id}")
    public String formEditPackage(@PathVariable String id, Model model, HttpServletRequest request,
                                  Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : "";
        String role = getUserRole(authentication);
        
        try {
            Package packageEntity = packageService.getPackageDetail(id, userId, role);
            
            if (!"Pending".equals(packageEntity.getStatus()) && !"Processed".equals(packageEntity.getStatus())) {
                model.addAttribute("title", "Cannot Edit Package");
                model.addAttribute("message", "Only Pending or Processed packages can be edited.");
                return "error/403";
            }

            UpdatePackageDto packageDto = dtoMapper.toUpdateDto(packageEntity);
            model.addAttribute("isEdit", true);
            model.addAttribute("packageId", id);
            model.addAttribute("packageData", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            return "package/form";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("title", "Package Not Found");
            model.addAttribute("message", e.getMessage());
            return "error/404";
        } catch (IllegalStateException e) {
            model.addAttribute("title", "Access Denied");
            model.addAttribute("message", e.getMessage());
            return "error/403";
        }
    }

    @PostMapping("/update/{id}")
    public String updatePackage(@PathVariable String id,
                               @Valid @ModelAttribute("packageData") UpdatePackageDto packageDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model,
                               HttpServletRequest request,
                               Authentication authentication) {
        
        String userId = authentication != null ? authentication.getName() : "";
        String role = getUserRole(authentication);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("packageId", id);
            model.addAttribute("packageData", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ Please fix the validation errors.");
            return "package/form";
        }
        
        try {
            packageService.getPackageDetail(id, userId, role);
            
            packageDto.setId(id);
            Package packageEntity = dtoMapper.toEntity(packageDto);
            packageService.updatePackage(packageEntity);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Package updated successfully!");
            return "redirect:/package/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("isEdit", true);
            model.addAttribute("packageId", id);
            model.addAttribute("packageData", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ " + e.getMessage());
            return "package/form";
        } catch (IllegalStateException e) {
            model.addAttribute("title", "Access Denied");
            model.addAttribute("message", e.getMessage());
            return "error/403";
        } catch (Exception e) {
            model.addAttribute("isEdit", true);
            model.addAttribute("packageId", id);
            model.addAttribute("packageData", packageDto);
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("errorMessage", "❌ " + e.getMessage());
            return "package/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePackage(@PathVariable String id, RedirectAttributes redirectAttributes,
                                Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : "";
        String role = getUserRole(authentication);
        
        try {
            packageService.getPackageDetail(id, userId, role);
            
            boolean removed = packageService.deletePackage(id);
            
            if (removed) {
                redirectAttributes.addFlashAttribute("successMessage", "✅ Package deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Package not found for deletion.");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Cannot delete package: " + e.getMessage());
        }
        return "redirect:/package";
    }

    @PostMapping("/{id}/process")
    public String processPackage(@PathVariable String id, RedirectAttributes redirectAttributes,
                                 Authentication authentication) {
        String userId = authentication != null ? authentication.getName() : "";
        String role = getUserRole(authentication);
        
        try {
            packageService.getPackageDetail(id, userId, role);
            
            packageService.processPackage(id);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Package processed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Failed to process package: " + e.getMessage());
        }
        return "redirect:/package/" + id;
    }
    
    private String getUserRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return "Customer";
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("Customer");
    }
}