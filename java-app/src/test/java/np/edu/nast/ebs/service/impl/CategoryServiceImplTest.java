package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.CategoryRequestDTO;
import np.edu.nast.ebs.dto.response.CategoryResponseDTO;
import np.edu.nast.ebs.mapper.CategoryMapper;
import np.edu.nast.ebs.model.Category;
import np.edu.nast.ebs.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setup() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = mock(CategoryMapper.class);
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper);
    }

    @Test
    void testCreateCategory() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Birthday");
        requestDTO.setDescription("Birthday Events");

        Category category = new Category();
        category.setName("Birthday");
        category.setDescription("Birthday Events");

        Category savedCategory = new Category();
        savedCategory.setCategoryId(1);
        savedCategory.setName("Birthday");
        savedCategory.setDescription("Birthday Events");

        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setCategoryId(1);
        responseDTO.setName("Birthday");
        responseDTO.setDescription("Birthday Events");

        when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Birthday", result.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category();
        category1.setCategoryId(1);
        category1.setName("Wedding");

        Category category2 = new Category();
        category2.setCategoryId(2);
        category2.setName("Conference");

        CategoryResponseDTO dto1 = new CategoryResponseDTO();
        dto1.setCategoryId(1);
        dto1.setName("Wedding");

        CategoryResponseDTO dto2 = new CategoryResponseDTO();
        dto2.setCategoryId(2);
        dto2.setName("Conference");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        List<CategoryResponseDTO> result = categoryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Wedding", result.get(0).getName());
        verify(categoryRepository).findAll();
    }
}
