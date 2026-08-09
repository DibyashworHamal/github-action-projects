package np.edu.nast.ebs.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrganizerPaymentDTO {

    @NotNull
    private Integer requestId;

    @NotBlank(message = "Bank account number is required.")
    @Pattern(regexp = "^\\d{16}$", message = "Bank account number must be exactly 16 digits.")
    private String bankAccountNumber;

    @NotNull(message = "Payment amount is required.")
    @DecimalMin(value = "100.00", message = "The minimum payment amount is Rs. 100.")
    private BigDecimal amount;
}