package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "First Name is required")
    private String firstName;
    
    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Enter a valid email address (e.g. user@example.com)"
        )
    private String email;
    
    @NotNull(message = "Phonenumber is required")
    @Pattern(regexp = "^9[78]\\d{8}$", message = "Please enter a valid 10-digit Nepali mobile number (e.g., 98... or 97...).")
    private String phoneNumber;


    @NotBlank(message = "Password is required")
    @Size(min = 6 , max = 8)
    private String password;
    
    @NotBlank(message = "Confirm Password is required")
    @Size(min = 6 , max = 8)
    private String confirmPassword;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|CUSTOMER|ORGANIZER", message = "Role must be ADMIN, CUSTOMER, or ORGANIZER")
    private String role;
}
