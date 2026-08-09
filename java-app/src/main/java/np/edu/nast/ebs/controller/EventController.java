package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.EventRequestDTO;
import np.edu.nast.ebs.dto.response.EventResponseDTO;
import np.edu.nast.ebs.service.EventService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    public EventResponseDTO create(@Valid @RequestBody EventRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<EventResponseDTO> getAllOrSearchEvents(
            @RequestParam(name = "query", required = false) String searchQuery) {
        
        if (searchQuery != null && !searchQuery.isEmpty()) {
            // If a search query is provided, use the search service method
            return service.searchEvents(searchQuery);
        } else {
            // If no query, return all events as before
            return service.getAll();
        }
    }

    @GetMapping("/{id}")
    public EventResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
