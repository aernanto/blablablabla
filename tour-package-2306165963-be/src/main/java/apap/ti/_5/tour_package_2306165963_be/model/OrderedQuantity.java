package apap.ti._5.tour_package_2306165963_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedQuantity {
    private String id;
    private String planId; 
    private String activityId;
    private int orderedQuota; 
    private int quota; 
    private Long price; 

    private String activityName;
    private String activityItem; 

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
   
    public Long getTotalPrice() {
        if (this.price == null) return 0L;
        return this.price * this.orderedQuota;
    }
    
    public String getFormattedPrice() {
        if (this.price == null) return "Rp 0";
        return String.format("Rp %,d", this.price);
    }

    public String getFormattedTotalPrice() {
        return String.format("Rp %,d", getTotalPrice());
    }
}