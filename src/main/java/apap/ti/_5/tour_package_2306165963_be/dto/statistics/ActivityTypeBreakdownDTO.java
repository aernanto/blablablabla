// ========== ActivityTypeBreakdownDTO.java ==========
package apap.ti._5.tour_package_2306165963_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTypeBreakdownDTO {
    private String activityType; // "Flight", "Accommodation", "Vehicle Rental"
    private Long revenue;
}