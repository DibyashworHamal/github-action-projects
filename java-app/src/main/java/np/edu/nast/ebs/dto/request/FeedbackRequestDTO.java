package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequestDTO {
	@NotNull(message = "BookinId is Required")
    private Integer bookingId;
    private int rating;
    @NotBlank(message = "Comment is Required")
    private String comment;
}
