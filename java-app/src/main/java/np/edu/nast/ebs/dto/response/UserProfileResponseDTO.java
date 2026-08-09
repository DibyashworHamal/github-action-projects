package np.edu.nast.ebs.dto.response;

import lombok.Data;

@Data
public class UserProfileResponseDTO {
    private Integer settingId;
    private Integer userId;
    private String profilePicture;
    private String bio;
    private String phoneNumber;
    private Boolean notificationEmail;
    private String theme;
    private String language;
}
