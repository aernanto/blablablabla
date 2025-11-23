package apap.ti._5.tour_package_2306165963_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueYearlyResponseDTO {
    private String period; // "2025-01", "2025-02", etc
    private String month; // "January", "February", etc
    private Long totalRevenue;
}