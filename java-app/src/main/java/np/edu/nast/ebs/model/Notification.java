package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime sentAt;

    public enum Status {
        UNREAD, READ, CLEARED
    }
}
