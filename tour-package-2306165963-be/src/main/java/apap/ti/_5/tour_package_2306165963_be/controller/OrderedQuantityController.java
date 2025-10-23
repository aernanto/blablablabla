package apap.ti._5.tour_package_2306165963_be.controller;

import apap.ti._5.tour_package_2306165963_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306165963_be.model.Plan;
import apap.ti._5.tour_package_2306165963_be.service.ActivityService;
import apap.ti._5.tour_package_2306165963_be.service.OrderedQuantityService;
import apap.ti._5.tour_package_2306165963_be.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/package/{packageId}/plan/{planId}/ordered-quantity")
public class OrderedQuantityController {

    @Autowired
    private PlanService planService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OrderedQuantityService orderedQuantityService;

    @GetMapping("/create")
    public String formCreateOrderedQuantity(@PathVariable String packageId, @PathVariable String planId,
                                            Model model, HttpServletRequest request) {
        Optional<Plan> planOptional = planService.getPlanById(packageId, planId);

        if (planOptional.isEmpty()) {
            model.addAttribute("title", "Plan Not Found");
            model.addAttribute("message", "Plan with ID " + planId + " not found.");
            return "error/404";
        }

        Plan plan = planOptional.get();
        if ("Processed".equals(plan.getStatus())) {
            model.addAttribute("title", "Cannot Add Ordered Quantity");
            model.addAttribute("message", "Cannot add ordered quantity to processed plan.");
            return "error/404";
        }

        model.addAttribute("currentPlan", plan);
        model.addAttribute("isEdit", false);
        model.addAttribute("oqData", new OrderedQuantity());
        model.addAttribute("activities", activityService.getActivitiesByActivityType(plan.getActivityType())); 
        model.addAttribute("currentUri", request.getRequestURI());
        return "ordered-quantity/form";
    }

    @PostMapping("/create")
    public String createOrderedQuantity(@PathVariable String packageId, @PathVariable String planId,
                                        @ModelAttribute OrderedQuantity oqData, RedirectAttributes redirectAttributes) {
        try {
            orderedQuantityService.createOrderedQuantity(packageId, planId, oqData);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Successfully added ordered quantity!");
            return "redirect:/package/" + packageId + "/plan/" + planId; 
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/" + packageId + "/plan/" + planId + "/ordered-quantity/create";
        }
    }

    @GetMapping("/update/{oqId}")
    public String formUpdateOrderedQuantity(@PathVariable String packageId, @PathVariable String planId,
                                            @PathVariable String oqId, Model model, HttpServletRequest request) {
        Optional<OrderedQuantity> oqOptional = orderedQuantityService.getOrderedQuantityById(packageId, planId, oqId);
        Optional<Plan> planOptional = planService.getPlanById(packageId, planId);

        if (oqOptional.isEmpty() || planOptional.isEmpty()) {
            model.addAttribute("title", "Ordered Quantity Not Found");
            model.addAttribute("message", "Ordered Quantity with ID " + oqId + " not found in plan " + planId);
            return "error/404";
        }
        
        Plan plan = planOptional.get();
        if ("Processed".equals(plan.getStatus())) {
             model.addAttribute("title", "Cannot Edit Ordered Quantity");
             model.addAttribute("message", "Cannot edit ordered quantity in processed plan.");
             return "error/404";
        }

        model.addAttribute("currentPlan", plan);
        model.addAttribute("isEdit", true);
        model.addAttribute("oqData", oqOptional.get());
        model.addAttribute("currentUri", request.getRequestURI());
        return "ordered-quantity/form";
    }

    @PostMapping("/update/{oqId}")
    public String updateOrderedQuantity(@PathVariable String packageId, @PathVariable String planId,
                                        @PathVariable String oqId, @ModelAttribute OrderedQuantity updatedOq,
                                        RedirectAttributes redirectAttributes) {
        try {
            orderedQuantityService.updateOrderedQuantity(packageId, planId, oqId, updatedOq);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Successfully updated ordered quantity!");
            return "redirect:/package/" + packageId + "/plan/" + planId;
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
            return "redirect:/package/" + packageId + "/plan/" + planId + "/ordered-quantity/update/" + oqId;
        }
    }

    @PostMapping("/{oqId}/delete")
    public String deleteOrderedQuantity(@PathVariable String packageId, @PathVariable String planId,
                                        @PathVariable String oqId, RedirectAttributes redirectAttributes) {
        try {
            boolean removed = orderedQuantityService.deleteOrderedQuantity(packageId, planId, oqId);

            if (removed) {
                redirectAttributes.addFlashAttribute("successMessage", "✅ Successfully deleted ordered quantity!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Ordered quantity not found for deletion.");
            }
        } catch (IllegalStateException e) {
             redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        
        return "redirect:/package/" + packageId + "/plan/" + planId;
    }
}