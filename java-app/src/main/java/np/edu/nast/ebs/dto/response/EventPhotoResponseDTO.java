package np.edu.nast.ebs.dto.response;

import lombok.Data;

@Data
public class EventPhotoResponseDTO {
    private Integer photoId;
    private Integer eventId;
    private String photoUrl;
}
