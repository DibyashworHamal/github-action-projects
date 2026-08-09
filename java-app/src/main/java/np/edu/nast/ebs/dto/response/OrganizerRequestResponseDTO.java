package np.edu.nast.ebs.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrganizerRequestResponseDTO {
    private Integer requestId;
    private Integer userId;
    
    // Fields from the related User entity
    private String userFullName;
    private String userEmail;
    
    // Fields from the OrganizerRequest entity
    private String businessName;
    private String taxId;
    private String contactPhone; 
    private String documentPath;
    private String paymentStatus;
    private String approvalStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String processedByAdminName;
    private String adminComments;
}