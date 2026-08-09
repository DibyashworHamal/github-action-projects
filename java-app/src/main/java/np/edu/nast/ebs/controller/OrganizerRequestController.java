package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.request.OrganizerRequestDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.service.OrganizerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizer-requests")
public class OrganizerRequestController {

    private final OrganizerRequestService organizerRequestService;

    @Autowired
    public OrganizerRequestController(OrganizerRequestService organizerRequestService) {
        this.organizerRequestService = organizerRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> createOrganizerRequest(
            @ModelAttribute OrganizerRequestDTO dto) { 
        try {
            OrganizerRequestResponseDTO response = organizerRequestService.createRequest(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}