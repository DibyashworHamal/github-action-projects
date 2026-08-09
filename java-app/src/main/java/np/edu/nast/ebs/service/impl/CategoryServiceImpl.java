package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.CategoryRequestDTO;
import np.edu.nast.ebs.dto.response.CategoryResponseDTO;
import np.edu.nast.ebs.mapper.CategoryMapper;
import np.edu.nast.ebs.model.Category;
import np.edu.nast.ebs.repository.CategoryRepository;
import np.edu.nast.ebs.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        Category category = mapper.toEntity(dto);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return mapper.toDto(repository.save(category));
    }

    @Override
    public List<CategoryResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
