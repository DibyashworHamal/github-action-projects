package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdminCreationRequestDTO {

    @NotBlank(message = "First Name is required")
    private String firstName;
    
    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^9[78]\\d{8}$", message = "Please enter a valid 10-digit Nepali mobile number.")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 8, message = "Password must be between 6 and 8 characters.")
    private String password;
    
    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;
}