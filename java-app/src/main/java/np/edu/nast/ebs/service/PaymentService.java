package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.PaymentRequestDTO;
import np.edu.nast.ebs.dto.response.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO makePayment(Integer bookingId, PaymentRequestDTO dto);
    List<PaymentResponseDTO> getAll();
}
