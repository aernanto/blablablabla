package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/package")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @GetMapping
    public String getAllPackage(HttpServletRequest request, Model model) {
        model.addAttribute("listPackage", packageService.getAllPackages());
        model.addAttribute("currentUri", request.getRequestURI());
        return "package/view-all";
    }

    @GetMapping("/{id}")
    public String getPackageById(@PathVariable String id, Model model, HttpServletRequest request) {
        Optional<Package> packageOptional = packageService.getPackageById(id);

        if (packageOptional.isEmpty()) {
            model.addAttribute("title", "Package Not Found");
            model.addAttribute("message", "Package with ID " + id + " not found.");
            return "error/404";
        }

        model.addAttribute("currentPackage", packageOptional.get());
        model.addAttribute("currentUri", request.getRequestURI());
        return "package/detail";
    }

    @GetMapping("/create")
    public String formCreatePackage(Model model, HttpServletRequest request) {
        model.addAttribute("isEdit", false);
        model.addAttribute("packageData", new Package());
        model.addAttribute("currentUri", request.getRequestURI());
        return "package/form";
    }

    @PostMapping("/create")
    public String createPackage(@ModelAttribute Package packageData, RedirectAttributes redirectAttributes) {
        try {
            Package created = packageService.createPackage(packageData);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Successfully created package: " + created.getPackageName());
            return "redirect:/package";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/create";
        }
    }

    @GetMapping("/update/{id}")
    public String formEditPackage(@PathVariable String id, Model model, HttpServletRequest request) {
        Optional<Package> packageOptional = packageService.getPackageById(id);

        if (packageOptional.isEmpty()) {
            model.addAttribute("title", "Package Not Found");
            model.addAttribute("message", "Package with ID " + id + " not found.");
            return "error/404";
        }

        Package pkg = packageOptional.get();
        if (!pkg.canBeEdited()) {
            model.addAttribute("title", "Cannot Edit Package");
            model.addAttribute("message", "Only packages with 'Pending' status can be edited.");
            return "error/404";
        }

        model.addAttribute("isEdit", true);
        model.addAttribute("packageId", id);
        model.addAttribute("packageData", pkg);
        model.addAttribute("currentUri", request.getRequestURI());
        return "package/form";
    }

    @PostMapping("/update/{id}")
    public String updatePackage(@PathVariable String id, @ModelAttribute Package updatedPackage, 
                                RedirectAttributes redirectAttributes) {
        try {
            updatedPackage.setId(id);
            packageService.updatePackage(updatedPackage);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Successfully updated package: " + updatedPackage.getPackageName());
            return "redirect:/package/" + id;
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/update/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePackage(@PathVariable String id, RedirectAttributes redirectAttributes) {
        boolean removed = packageService.deletePackage(id);

        if (removed) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Successfully deleted package with ID: " + id);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Package not found for deletion.");
        }
        return "redirect:/package";
    }

    @PostMapping("/process/{id}")
    public String processPackage(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Package pkg = packageService.processPackage(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Package '" + pkg.getPackageName() + "' has been processed!");
            return "redirect:/package/" + id;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/" + id;
        }
    }
}