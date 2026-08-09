package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    private LocalDateTime bookingDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        PENDING, PAID, FAILED
    }
}
