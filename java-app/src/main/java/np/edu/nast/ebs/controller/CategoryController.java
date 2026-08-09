package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.CategoryRequestDTO;
import np.edu.nast.ebs.dto.response.CategoryResponseDTO;
import np.edu.nast.ebs.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryResponseDTO create(@Valid @RequestBody CategoryRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<CategoryResponseDTO> getAll() {
        return service.getAll();
    }
}
