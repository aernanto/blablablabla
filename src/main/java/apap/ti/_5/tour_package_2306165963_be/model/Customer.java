package apap.ti._5.tour_package_2306165963_be.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    private String id; 
    private String name;
    private Integer loyaltyPoints;
}