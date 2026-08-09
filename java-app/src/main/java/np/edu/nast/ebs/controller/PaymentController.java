package np.edu.nast.ebs.controller;

import np.edu.nast.ebs.dto.response.PaymentResponseDTO;
import np.edu.nast.ebs.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<PaymentResponseDTO> getAll() {
        return service.getAll();
    }
}
