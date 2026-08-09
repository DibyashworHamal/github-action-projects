package np.edu.nast.ebs.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponseDTO {
    private Integer feedbackId;
    private Integer bookingId;
    private String eventTitle;
    private Integer customerId;
    private String comment;
    private Integer rating;
    private LocalDateTime submittedAt;
}
