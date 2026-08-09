package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.CategoryRequestDTO;
import np.edu.nast.ebs.dto.response.CategoryResponseDTO;
import np.edu.nast.ebs.model.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	@Mapping(target = "categoryId", ignore = true)
	Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toDto(Category category);
}