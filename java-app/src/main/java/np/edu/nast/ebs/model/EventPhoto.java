package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_photo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer photoId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String photoUrl;
}
