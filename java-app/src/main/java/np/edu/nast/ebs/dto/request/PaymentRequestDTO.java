package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be greater than 0")
    private Double amount;
}
