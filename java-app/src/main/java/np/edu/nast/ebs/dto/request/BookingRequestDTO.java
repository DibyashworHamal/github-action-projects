package np.edu.nast.ebs.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty; 
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Event ID is required")
    @JsonProperty("eventId")
    private Integer eventId;

    @NotNull(message = "Customer ID is required")
    private Integer customerId;
}
