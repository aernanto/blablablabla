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
public class CreateCouponRequestDto {
    
    @NotBlank(message = "Coupon name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;
    
    @NotNull(message = "Points required is mandatory")
    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100000, message = "Points cannot exceed 100000")
    private Integer points;
    
    @NotNull(message = "Discount percentage is mandatory")
    @DecimalMin(value = "0.01", message = "Discount must be at least 0.01%")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    private Double percentOff;
}