package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.response.NotificationResponseDTO;
import np.edu.nast.ebs.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public NotificationResponseDTO send(@Valid @RequestBody NotificationRequestDTO dto) {
        return service.sendNotification(dto);
    }

    @GetMapping
    public List<NotificationResponseDTO> getAll() {
        return service.getAll();
    }
}
