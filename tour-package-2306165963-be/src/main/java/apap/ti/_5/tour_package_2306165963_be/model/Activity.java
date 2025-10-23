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
public class Activity {
    
    private String id;
    private String activityName;
    private String activityItem; 
    private int capacity; 
    private Long price;
    private String activityType; 

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    
    private String startLocation;
    private String endLocation;
        
    public String getFormattedPrice() {
        if (this.price == null) return "Rp 0";
        return String.format("Rp %,d", this.price);
    }
    
    public String getActivityTypeIcon() {
        if (activityType == null) return "📋";
        switch (activityType.toLowerCase()) {
            case "flight": return "✈️";
            case "accommodation": return "🏨";
            case "vehicle rental": return "🚗"; 
            default: return "📋";
        }
    }

    public String getDuration() {
        if (startDate == null || endDate == null) return "N/A";
        return String.format("%s - %s", 
            startDate.toLocalDate().toString(), 
            endDate.toLocalDate().toString());
    }
}