package apap.ti._5.tour_package_2306165963_be.dto.loyalty.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    private String id;
    private String name;
    private Integer loyaltyPoints;
    
    public String getFormattedPoints() {
        return String.format("%,d points", loyaltyPoints != null ? loyaltyPoints : 0);
    }
}