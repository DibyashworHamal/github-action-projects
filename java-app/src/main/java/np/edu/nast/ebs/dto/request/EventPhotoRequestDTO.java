package np.edu.nast.ebs.dto.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventPhotoRequestDTO {
	@NotNull(message = "EvendId is Required")
    private Integer eventId;
	@NotBlank(message = "PhotoUrl is Required")
	@URL(message = "Url must be valid")
    private String photoUrl;
}
