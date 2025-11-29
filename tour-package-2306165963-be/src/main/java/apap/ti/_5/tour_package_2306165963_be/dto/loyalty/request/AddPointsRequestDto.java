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
public class AddPointsRequestDto {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Points amount is required")
    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100000, message = "Points cannot exceed 100000")
    private Integer points;
    
    private String apiKey; 
}