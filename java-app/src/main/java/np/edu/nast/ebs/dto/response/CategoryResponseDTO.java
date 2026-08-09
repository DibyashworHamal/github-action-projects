package np.edu.nast.ebs.dto.response;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Integer categoryId;
    private String name;
    private String description;
}
