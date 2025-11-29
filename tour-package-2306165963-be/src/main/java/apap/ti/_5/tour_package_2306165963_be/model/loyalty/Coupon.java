package apap.ti._5.tour_package_2306165963_be.model.loyalty;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    private String id;
    
    private String name;
    private String description;
    private Integer points; // Harga poin
    private Double percentOff; // Diskon (%)
    private Boolean isDeleted = false;

    @PrePersist
    public void generateId() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
    }
}