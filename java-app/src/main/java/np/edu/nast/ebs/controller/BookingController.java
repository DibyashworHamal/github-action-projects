package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.BookingRequestDTO;
import np.edu.nast.ebs.dto.response.BookingResponseDTO;
import np.edu.nast.ebs.service.BookingService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingResponseDTO book(@Valid @RequestBody BookingRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<BookingResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }
}
