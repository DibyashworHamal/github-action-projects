package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.FeedbackRequestDTO;
import np.edu.nast.ebs.dto.response.FeedbackResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.FeedbackMapper;
import np.edu.nast.ebs.model.Booking;
import np.edu.nast.ebs.model.Feedback;
import np.edu.nast.ebs.repository.BookingRepository;
import np.edu.nast.ebs.repository.FeedbackRepository;
import np.edu.nast.ebs.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookingRepository bookingRepository;
    private final FeedbackMapper mapper;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               BookingRepository bookingRepository,
                               FeedbackMapper mapper) {
        this.feedbackRepository = feedbackRepository;
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
    }

    @Override
    public FeedbackResponseDTO giveFeedback(FeedbackRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Feedback feedback = mapper.toEntity(dto);
        feedback.setBooking(booking);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedback.setSubmittedAt(LocalDateTime.now());

        return mapper.toDto(feedbackRepository.save(feedback));
    }

    @Override
    public List<FeedbackResponseDTO> getAll() {
        return feedbackRepository.findAll()
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
