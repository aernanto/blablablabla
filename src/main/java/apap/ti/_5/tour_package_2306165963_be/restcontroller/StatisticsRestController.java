package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.restservice.StatisticsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {
    @Autowired private StatisticsRestService statsService;

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(@RequestParam Integer year, @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(statsService.getRevenue(year, month));
    }
}