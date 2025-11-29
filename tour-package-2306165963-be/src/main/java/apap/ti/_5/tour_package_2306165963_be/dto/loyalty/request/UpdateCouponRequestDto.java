package apap.ti._5.tour_package_2306165963_be.dto.loyalty.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCouponRequestDto {
    
    @NotBlank(message = "Coupon name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500)
    private String description;
    
    @NotNull(message = "Points required is mandatory")
    @Min(value = 1)
    @Max(value = 100000)
    private Integer points;
    
    @NotNull(message = "Discount percentage is mandatory")
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "100.0")
    private Double percentOff;
}