package apap.ti._5.tour_package_2306165963_be.model.loyalty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "loyalty_points", columnDefinition = "INTEGER DEFAULT 0")
    private Integer loyaltyPoints = 0;
}