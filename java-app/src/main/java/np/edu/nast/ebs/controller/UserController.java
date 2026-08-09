package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.dto.response.UserResponseDTO;
import np.edu.nast.ebs.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO dto) {
        return service.createUser(dto);
    }

    @GetMapping
    public List<UserResponseDTO> getAll() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Integer id) {
        return service.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteUser(id);
    }
}
