package apap.ti._5.tour_package_2306165963_be.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Package {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(nullable = false)
    private int quota;

    @Column(nullable = false, updatable = false)
    private Long price;

    @Column(nullable = false)
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    @Builder.Default
    private List<Plan> plans = new ArrayList<>();

    @Transient
    public boolean canBeEdited() {
        return "Pending".equals(this.status);
    }

    @Transient
    public String getFormattedStartDate() {
        if (this.startDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return this.startDate.format(formatter);
    }

    @Transient
    public String getFormattedEndDate() {
        if (this.endDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return this.endDate.format(formatter);
    }

    @Transient
    public String getFormattedPrice() {
        if (this.price == null) return "Rp 0";
        return String.format("Rp %,d", this.price);
    }

    @Transient
    public int getTotalPlans() {
        return plans != null ? plans.size() : 0;
    }

    public List<Plan> getPlans() {
        if (plans == null) {
            plans = new ArrayList<>();
        }
        return plans;
    }
}