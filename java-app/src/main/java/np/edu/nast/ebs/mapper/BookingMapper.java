package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.BookingRequestDTO;
import np.edu.nast.ebs.dto.response.BookingResponseDTO;
import np.edu.nast.ebs.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "eventId", target = "event.eventId")
    @Mapping(source = "customerId", target = "customer.userId")
    // Added ignores for fields set by the system/JPA
    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "bookingDate", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    Booking toEntity(BookingRequestDTO dto);

    @Mapping(source = "event.eventId", target = "eventId")
    @Mapping(source = "event.title", target = "eventTitle")
    @Mapping(source = "event.price", target = "eventPrice")
    @Mapping(source = "customer.userId", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    BookingResponseDTO toDto(Booking booking);
}