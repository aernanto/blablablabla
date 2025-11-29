package apap.ti._5.tour_package_2306165963_be.model.loyalty;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchased_coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasedCoupon {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true, name = "unique_code")
    private String uniqueCode;
    
    @Column(nullable = false, name = "customer_id")
    private String customerId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @Column(name = "is_used", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isUsed = false;
    
    @Column(name = "purchased_date")
    private LocalDateTime purchasedDate;
    
    @Column(name = "used_date")
    private LocalDateTime usedDate;
    
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.purchasedDate = LocalDateTime.now();
        if (this.isUsed == null) {
            this.isUsed = false;
        }
    }
}