package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.FeedbackRequestDTO;
import np.edu.nast.ebs.dto.response.FeedbackResponseDTO;
import np.edu.nast.ebs.model.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    @Mapping(source = "bookingId", target = "booking.bookingId")
    @Mapping(target = "feedbackId", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    Feedback toEntity(FeedbackRequestDTO dto);

    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "booking.event.title", target = "eventTitle")
    @Mapping(source = "booking.customer.userId", target = "customerId")
    FeedbackResponseDTO toDto(Feedback feedback);
}