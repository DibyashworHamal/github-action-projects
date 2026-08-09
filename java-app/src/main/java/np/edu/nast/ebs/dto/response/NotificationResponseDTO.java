package np.edu.nast.ebs.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Integer notificationId;
    private Integer userId;
    private String userName;
    private String title;
    private String message;
    private String status;
    private LocalDateTime sentAt;
}
