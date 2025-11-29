package apap.ti._5.tour_package_2306165963_be.dto.loyalty.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseCouponResponseDto {
    private String code;
    private Double discountPercent;
    private Boolean success;
    private String message;
    
    public String getFormattedDiscount() {
        return String.format("%.0f%%", discountPercent != null ? discountPercent : 0.0);
    }
}