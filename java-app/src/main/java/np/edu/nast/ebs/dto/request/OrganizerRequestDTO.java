package np.edu.nast.ebs.dto.request;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class OrganizerRequestDTO {
    private Integer userId;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Tax ID is required")
    private String taxId;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^9[78]\\d{8}$", message = "Please enter a valid 10-digit Nepali mobile number (e.g., 98... or 97...).")
    private String contactPhone;

    @NotNull(message = "Document is required")
    private MultipartFile document;
}