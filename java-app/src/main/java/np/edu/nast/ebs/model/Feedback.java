package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private int rating;
    private String comment;
    private LocalDateTime submittedAt;
}
