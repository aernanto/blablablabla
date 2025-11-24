package apap.ti._5.tour_package_2306165963_be.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasedCoupon {
    @Id
    private String id;
    
    private String uniqueCode; // Format khusus
    private String customerId;
    
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Boolean isUsed = false;

    @PrePersist
    public void generateId() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
    }
}