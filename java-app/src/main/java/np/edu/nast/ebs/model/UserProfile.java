package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String profilePicture;
    private String bio;
    private String phoneNumber;
    private Boolean notificationEmail;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    private String language;
    private LocalDateTime updatedAt;

    public enum Theme {
        LIGHT, DARK
    }
}
