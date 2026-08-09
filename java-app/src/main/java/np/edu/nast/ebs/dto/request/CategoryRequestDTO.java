package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDTO {
	@NotBlank(message = "Name is Required")
    private String name;
	@NotBlank(message = "Description is Required")
    private String description;
}