package apap.ti._5.tour_package_2306165963_be.dto.loyalty.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDto {
    private String id;
    private String name;
    private String description;
    private Integer points;
    private Double percentOff;
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    public String getFormattedDiscount() {
        return String.format("%.0f%%", percentOff);
    }
    
    public String getFormattedPoints() {
        return String.format("%,d points", points);
    }
}