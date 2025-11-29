package apap.ti._5.tour_package_2306165963_be.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping({"/"})
    public String home(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        return "home"; 
    }
}