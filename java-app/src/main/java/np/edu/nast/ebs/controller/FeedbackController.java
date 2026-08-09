package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.FeedbackRequestDTO;
import np.edu.nast.ebs.dto.response.FeedbackResponseDTO;
import np.edu.nast.ebs.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping
    public FeedbackResponseDTO submit(@Valid @RequestBody FeedbackRequestDTO dto) {
        return service.giveFeedback(dto);
    }

    @GetMapping
    public List<FeedbackResponseDTO> getAll() {
        return service.getAll();
    }
}
