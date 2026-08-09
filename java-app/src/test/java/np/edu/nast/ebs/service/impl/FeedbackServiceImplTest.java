package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.FeedbackRequestDTO;
import np.edu.nast.ebs.dto.response.FeedbackResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.FeedbackMapper;
import np.edu.nast.ebs.model.Booking;
import np.edu.nast.ebs.model.Feedback;
import np.edu.nast.ebs.repository.BookingRepository;
import np.edu.nast.ebs.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FeedbackServiceImplTest {

    private FeedbackRepository feedbackRepository;
    private BookingRepository bookingRepository;
    private FeedbackMapper feedbackMapper;
    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        feedbackRepository = mock(FeedbackRepository.class);
        bookingRepository = mock(BookingRepository.class);
        feedbackMapper = mock(FeedbackMapper.class);
        feedbackService = new FeedbackServiceImpl(feedbackRepository, bookingRepository, feedbackMapper);
    }

    @Test
    void testGiveFeedback_Success() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setBookingId(1);
        dto.setRating(4);
        dto.setComment("Good");

        Booking booking = new Booking();
        booking.setBookingId(1);

        Feedback feedback = new Feedback();
        feedback.setRating(4);
        feedback.setComment("Good");

        Feedback savedFeedback = new Feedback();
        savedFeedback.setFeedbackId(1);
        savedFeedback.setRating(4);

        FeedbackResponseDTO responseDTO = new FeedbackResponseDTO();
        responseDTO.setFeedbackId(1);

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(feedbackMapper.toEntity(dto)).thenReturn(feedback);
        when(feedbackRepository.save(feedback)).thenReturn(savedFeedback);
        when(feedbackMapper.toDto(savedFeedback)).thenReturn(responseDTO);

        FeedbackResponseDTO result = feedbackService.giveFeedback(dto);

        assertNotNull(result);
        assertEquals(1, result.getFeedbackId());
    }

    @Test
    void testGiveFeedback_BookingNotFound() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setBookingId(99);
        dto.setRating(4);
        dto.setComment("Not good");

        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.giveFeedback(dto));
    }

    @Test
    void testGetAllFeedback() {
        Feedback feedback1 = new Feedback();
        feedback1.setFeedbackId(1);

        Feedback feedback2 = new Feedback();
        feedback2.setFeedbackId(2);

        FeedbackResponseDTO dto1 = new FeedbackResponseDTO();
        dto1.setFeedbackId(1);

        FeedbackResponseDTO dto2 = new FeedbackResponseDTO();
        dto2.setFeedbackId(2);

        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(feedback1, feedback2));
        when(feedbackMapper.toDto(feedback1)).thenReturn(dto1);
        when(feedbackMapper.toDto(feedback2)).thenReturn(dto2);

        List<FeedbackResponseDTO> result = feedbackService.getAll();

        assertEquals(2, result.size());
        verify(feedbackRepository).findAll();
    }
}
