package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Date and time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "Organizer ID is required")
    private Integer organizerId;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

}
