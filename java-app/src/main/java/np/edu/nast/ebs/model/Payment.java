package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime paymentDate;

    public enum Status {
        PENDING, PAID, FAILED
    }
}
