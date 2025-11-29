package apap.ti._5.tour_package_2306165963_be.dto.coupon;
import lombok.Data;

@Data
public class CouponRequestDto {
    private String name;
    private String description;
    private Integer points;
    private Double percentOff;
}