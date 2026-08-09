package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordResetRequest {

    private String email; 

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 8, message = "New password must be between 6 and 8 characters")
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}