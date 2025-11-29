package apap.ti._5.tour_package_2306165963_be.dto.loyalty.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseCouponRequestDto {
    
    @NotBlank(message = "Unique code is required")
    private String code;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
}