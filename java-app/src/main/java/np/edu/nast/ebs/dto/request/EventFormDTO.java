package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventFormDTO {

    @NotBlank(message = "Event name is required.")
    @Size(min = 3, max = 100, message = "Event name must be between 3 and 100 characters.")
    private String eventName;

    @NotBlank(message = "Event description is required.")
    @Size(min = 20, message = "Description must be at least 20 characters long.")
    private String eventDescription;

    @NotNull(message = "Start date and time are required.")
    @Future(message = "Start date must be in the future.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time are required.")
    @Future(message = "End date must be in the future.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @NotBlank(message = "Location is required.")
    private String location;

    @NotNull(message = "Category is required.")
    private Integer categoryId;
    
    private String otherCategoryName;

    @NotNull(message = "Total tickets are required.")
    @Min(value = 1, message = "At least one ticket must be available.")
    private Integer totalTickets;

    @NotNull(message = "Ticket price is required.")
    @DecimalMin(value = "0.0", message = "Price can be 0, but not negative.")
    private BigDecimal ticketPrice;

    private MultipartFile eventImage; // For file upload

    private String organizerName;
    private String organizerContact;
    private String eventWebsite;

    private boolean featured;
    private boolean registrationRequired;

    @AssertTrue(message = "End date must be after start date.")
    public boolean isEndDateAfterStartDate() {
        return startDateTime == null || endDateTime == null || endDateTime.isAfter(startDateTime);
    }
}