package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.model.Package;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.service.ActivityService;
import apap.ti._5.tour_package_2306165963_be.service.PackageService;
import apap.ti._5.tour_package_2306165963_be.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/package/{packageId}/plan")
public class PlanController {

    @Autowired
    private PackageService packageService;

    @Autowired
    private PlanService planService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/create")
    public String formCreatePlan(@PathVariable String packageId, Model model, HttpServletRequest request) {
        Optional<Package> packageOptional = packageService.getPackageById(packageId);

        if (packageOptional.isEmpty()) {
            model.addAttribute("title", "Package Not Found");
            model.addAttribute("message", "Package with ID " + packageId + " not found.");
            return "error/404";
        }

        Package pkg = packageOptional.get();
        if (!pkg.canBeEdited()) {
            model.addAttribute("title", "Cannot Add Plan");
            model.addAttribute("message", "Plans cannot be added to processed packages.");
            return "error/404";
        }

        model.addAttribute("currentPackage", pkg);
        model.addAttribute("isEdit", false);
        model.addAttribute("planData", new Plan());
        model.addAttribute("activityTypes", new String[]{"Flight", "Accommodation", "Vehicle"});
        model.addAttribute("currentUri", request.getRequestURI());
        return "plan/form";
    }

    @PostMapping("/create")
    public String createPlan(@PathVariable String packageId, @ModelAttribute Plan planData,
                            RedirectAttributes redirectAttributes) {
        try {
            planService.createPlan(packageId, planData);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Successfully added new plan!");
            return "redirect:/package/" + packageId;
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/" + packageId + "/plan/create";
        }
    }

    @GetMapping("/{planId}/update")
    public String formEditPlan(@PathVariable String packageId, @PathVariable String planId,
                              Model model, HttpServletRequest request) {
        Optional<Package> packageOptional = packageService.getPackageById(packageId);

        if (packageOptional.isEmpty()) {
            model.addAttribute("title", "Package Not Found");
            model.addAttribute("message", "Package with ID " + packageId + " not found.");
            return "error/404";
        }

        Optional<Plan> planOptional = planService.getPlanById(packageId, planId);

        if (planOptional.isEmpty()) {
            model.addAttribute("title", "Plan Not Found");
            model.addAttribute("message", "Plan with ID " + planId + " not found.");
            return "error/404";
        }

        Plan plan = planOptional.get();
        if ("Processed".equals(plan.getStatus())) {
            model.addAttribute("title", "Cannot Edit Plan");
            model.addAttribute("message", "Processed plans cannot be edited.");
            return "error/404";
        }

        model.addAttribute("currentPackage", packageOptional.get());
        model.addAttribute("isEdit", true);
        model.addAttribute("planId", planId);
        model.addAttribute("planData", plan);
        model.addAttribute("activityTypes", new String[]{"Flight", "Accommodation", "Vehicle"});
        model.addAttribute("currentUri", request.getRequestURI());
        return "plan/form";
    }

    @PostMapping("/{planId}/update")
    public String updatePlan(@PathVariable String packageId, @PathVariable String planId,
                            @ModelAttribute Plan updatedPlan, RedirectAttributes redirectAttributes) {
        try {
            planService.updatePlan(packageId, planId, updatedPlan);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Successfully updated plan!");
            return "redirect:/package/" + packageId;
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/" + packageId + "/plan/" + planId + "/update";
        }
    }

    @PostMapping("/{planId}/delete")
    public String deletePlan(@PathVariable String packageId, @PathVariable String planId,
                            RedirectAttributes redirectAttributes) {
        boolean removed = planService.deletePlan(packageId, planId);

        if (removed) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Successfully deleted plan!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Plan not found for deletion.");
        }
        return "redirect:/package/" + packageId;
    }

    @PostMapping("/{planId}/process")
    public String processPlan(@PathVariable String packageId, @PathVariable String planId,
                             RedirectAttributes redirectAttributes) {
        try {
            planService.processPlan(packageId, planId);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Plan has been processed!");
            return "redirect:/package/" + packageId;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/" + packageId;
        }
    }
}