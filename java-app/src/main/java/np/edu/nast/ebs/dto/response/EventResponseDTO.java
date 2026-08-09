package np.edu.nast.ebs.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventResponseDTO {
	
    private Integer eventId;
    private Integer organizerId;
    private Integer categoryId;
    private String organizerName;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal price;
    private Integer totalTickets;
    private LocalDateTime createdAt;
    private String coverImagePath;
    private String categoryName;
    private String customCategory;
   
    private boolean featured;
    private boolean registrationRequired;
    
    private String organizerContact;
    private String eventWebsite;
}