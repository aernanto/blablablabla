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
public class PurchasedCouponResponseDto {
    private String id;
    private String uniqueCode;
    private String customerId;
    private CouponResponseDto coupon;
    private Boolean isUsed;
    private LocalDateTime purchasedDate;
    private LocalDateTime usedDate;
    
    public String getStatusBadge() {
        return isUsed ? "Used" : "Available";
    }
    
    public String getStatusClass() {
        return isUsed ? "status-used" : "status-available";
    }
}