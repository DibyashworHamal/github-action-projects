package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.EventPhotoRequestDTO;
import np.edu.nast.ebs.dto.response.EventPhotoResponseDTO;
import np.edu.nast.ebs.service.EventPhotoService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/event-photos")
public class EventPhotoController {

    private final EventPhotoService service;

    public EventPhotoController(EventPhotoService service) {
        this.service = service;
    }

    @PostMapping
    public EventPhotoResponseDTO add(@Valid @RequestBody EventPhotoRequestDTO dto) {
        return service.add(dto);
    }

    @GetMapping("/event/{eventId}")
    public List<EventPhotoResponseDTO> getByEvent(@PathVariable Integer eventId) {
        return service.getByEventId(eventId);
    }
}
