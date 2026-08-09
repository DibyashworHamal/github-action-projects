package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Profile picture URL is required")
    private String profilePicture;

    @NotBlank(message = "Bio cannot be blank")
    @Size(max = 255, message = "Bio must be at most 255 characters")
    private String bio;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$",
        message = "Phone number must be a valid 10-digit number"
    )
    private String phoneNumber;

    @NotNull(message = "Notification email preference is required")
    private Boolean notificationEmail;

    @NotBlank(message = "Theme preference is required")
    private String theme;

    @NotBlank(message = "Language preference is required")
    private String language;
}
