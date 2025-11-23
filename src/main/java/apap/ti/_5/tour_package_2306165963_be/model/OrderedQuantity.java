package apap.ti._5.tour_package_2306165963_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordered_quantities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedQuantity {
   
    @Id
    private String id;
   
    @Column(name = "plan_id")
    private String planId;
   
    @Column(name = "activity_id")
    private String activityId;
   
    @Column(name = "ordered_quota")
    private Integer orderedQuota;
   
    private Integer quota;
   
    private Long price;

    @Column(name = "activity_name")
    private String activityName;
   
    @Column(name = "activity_item")
    private String activityItem;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "start_date")
    private LocalDateTime startDate;
   
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "end_date")
    private LocalDateTime endDate;
   
    // ✨ NEW FIELD: Track if this ordered quantity is fulfilled
    @Column(name = "is_fulfilled")
    private Boolean isFulfilled;
   
    @Transient
    public Long getTotalPrice() {
        if (this.price == null || this.orderedQuota == null) return 0L;
        return this.price * this.orderedQuota;
    }
   
    @Transient
    public String getFormattedPrice() {
        if (this.price == null) return "Rp 0";
        return String.format("Rp %,d", this.price);
    }

    @Transient
    public String getFormattedTotalPrice() {
        return String.format("Rp %,d", getTotalPrice());
    }
}