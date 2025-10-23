package apap.ti._5.tour_package_2306165963_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    private String id;
    private String packageId;
    private Long price;
    private String activityType; // Flight, Accommodation, Vehicle
    private String status; // Unfinished, Pending, Processed
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    
    private String startLocation;
    private String endLocation;
    
    @Builder.Default
    private List<OrderedQuantity> orderedQuantities = new ArrayList<>();
    
    public String getFormattedPrice() {
        if (this.price == null) return "Rp 0";
        return String.format("Rp %,d", this.price);
    }
    
    public String getActivityTypeIcon() {
        if (activityType == null) return "📋";
        switch (activityType.toLowerCase()) {
            case "flight": return "✈️";
            case "accommodation": return "🏨";
            case "vehicle": return "🚗";
            default: return "📋";
        }
    }
}