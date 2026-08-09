package np.edu.nast.ebs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;

    // Optional: for simplified testing when using "name"
    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String name) {
        String[] parts = name.split(" ", 2);
        this.firstName = parts.length > 0 ? parts[0] : "";
        this.lastName = parts.length > 1 ? parts[1] : "";
    }
}
