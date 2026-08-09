package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.CategoryRequestDTO;
import np.edu.nast.ebs.dto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO create(CategoryRequestDTO dto);
    List<CategoryResponseDTO> getAll();
}
