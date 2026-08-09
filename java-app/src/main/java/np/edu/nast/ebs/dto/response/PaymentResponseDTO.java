package np.edu.nast.ebs.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Integer paymentId;
    private Integer bookingId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentDate;
}
