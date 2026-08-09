package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 8, message = "New password must be between 6 and 8 characters")
    private String newPassword;

    @NotBlank(message = "Confirmation password is required")
    private String confirmPassword;
}