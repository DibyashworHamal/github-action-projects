package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.PaymentRequestDTO;
import np.edu.nast.ebs.dto.response.PaymentResponseDTO;
import np.edu.nast.ebs.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "booking", ignore = true)
    Payment toEntity(PaymentRequestDTO dto);

    @Mapping(source = "booking.bookingId", target = "bookingId")
    PaymentResponseDTO toDto(Payment payment);
}