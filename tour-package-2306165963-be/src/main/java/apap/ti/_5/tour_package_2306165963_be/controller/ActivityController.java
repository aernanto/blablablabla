package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.model.Activity;
import apap.ti._5.tour_package_2306165963_be.service.ActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public String getAllActivities(Model model, HttpServletRequest request) {
        model.addAttribute("listActivity", activityService.getAllActivities());
        model.addAttribute("currentUri", request.getRequestURI());
        return "activity/view-all";
    }

    @GetMapping("/{id}")
    public String getActivityById(@PathVariable String id, Model model, HttpServletRequest request) {
        Optional<Activity> activityOptional = activityService.getActivityById(id);

        if (activityOptional.isEmpty()) {
            model.addAttribute("title", "Activity Not Found");
            model.addAttribute("message", "Activity with ID " + id + " not found.");
            return "error/404";
        }

        model.addAttribute("activity", activityOptional.get());
        model.addAttribute("currentUri", request.getRequestURI());
        return "activity/detail";
    }

    @GetMapping("/create")
    public String formCreateActivity(Model model, HttpServletRequest request) {
        model.addAttribute("isEdit", false);
        model.addAttribute("activityData", new Activity());
        model.addAttribute("activityTypes", new String[]{"Flight", "Accommodation", "Vehicle Rental"}); 
        model.addAttribute("currentUri", request.getRequestURI());
        return "activity/form";
    }

    @PostMapping("/create")
    public String createActivity(@ModelAttribute Activity activityData, RedirectAttributes redirectAttributes) {
        activityService.createActivity(activityData);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Activity created successfully!");
        return "redirect:/activities";
    }

    @GetMapping("/update/{id}")
    public String formEditActivity(@PathVariable String id, Model model, HttpServletRequest request) {
        Optional<Activity> activityOptional = activityService.getActivityById(id);

        if (activityOptional.isEmpty()) {
            model.addAttribute("title", "Activity Not Found");
            model.addAttribute("message", "Activity with ID " + id + " not found.");
            return "error/404";
        }

        model.addAttribute("isEdit", true);
        model.addAttribute("activityId", id);
        model.addAttribute("activityData", activityOptional.get());
        model.addAttribute("activityTypes", new String[]{"Flight", "Accommodation", "Vehicle Rental"}); 
        model.addAttribute("currentUri", request.getRequestURI());
        return "activity/form";
    }

    @PostMapping("/update/{id}")
    public String updateActivity(@PathVariable String id, @ModelAttribute Activity updatedActivity,
                                 RedirectAttributes redirectAttributes) {
        try {
            updatedActivity.setId(id);
            activityService.updateActivity(updatedActivity);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Activity updated successfully!");
            return "redirect:/activities/" + id;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/activities/update/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteActivity(@PathVariable String id, RedirectAttributes redirectAttributes) {
        boolean removed = activityService.deleteActivity(id);

        if (removed) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Activity deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Activity not found for deletion.");
        }
        return "redirect:/activities";
    }
}