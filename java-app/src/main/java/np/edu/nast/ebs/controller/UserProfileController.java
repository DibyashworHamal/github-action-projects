package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.UserProfileRequestDTO;
import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import np.edu.nast.ebs.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping
    public UserProfileResponseDTO create(@Valid @RequestBody UserProfileRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<UserProfileResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserProfileResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
