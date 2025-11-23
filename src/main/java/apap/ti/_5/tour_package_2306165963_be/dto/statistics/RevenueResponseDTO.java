package apap.ti._5.tour_package_2306165963_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponseDTO {
    private String period; // "2025" or "2025-03"
    private Long totalRevenue;
    private List<RevenueYearlyResponseDTO> monthlyBreakdown; // Used when month param is null
    private List<ActivityTypeBreakdownDTO> breakdown; // Used when month param is provided
}