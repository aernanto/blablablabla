package apap.ti._5.tour_package_2306165963_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueMonthlyDetailResponseDTO {
    private String period; // "2025-03"
    private Long totalRevenue;
    private List<ActivityTypeBreakdownDTO> breakdown;
}