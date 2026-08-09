package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {
	@NotNull(message = "UserId is Required")
    private Integer userId;
	@NotBlank(message = "Title is Required")
    private String title;
	@NotBlank(message = "Message is required")
    private String message;
}
