package apap.ti._5.tour_package_2306165963_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Package {
    private String id;
    private String userId;
    private String packageName;
    private int quota;
    private Long price;
    private String status; 
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    
    @Builder.Default
    private List<Plan> plans = new ArrayList<>();
    
    public boolean canBeEdited() {
        return "Pending".equals(this.status);
    }
    
    public String getFormattedStartDate() {
        if (this.startDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return this.startDate.format(formatter);
    }
    
    public String getFormattedEndDate() {
        if (this.endDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return this.endDate.format(formatter);
    }
    
    public String getFormattedPrice() {
        if (this.price == null) return "Rp 0";
        return String.format("Rp %,d", this.price);
    }
    
    public int getTotalPlans() {
        return plans != null ? plans.size() : 0;
    }
}