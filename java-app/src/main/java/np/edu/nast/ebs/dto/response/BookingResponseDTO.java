package np.edu.nast.ebs.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Integer bookingId;
    private Integer eventId;
    private String eventTitle;
    private Integer customerId;
    private String customerName;
    private LocalDateTime bookingDate;
    private String paymentStatus;
    private BigDecimal eventPrice;
}
