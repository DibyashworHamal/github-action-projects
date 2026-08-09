package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.BookingRequestDTO;
import np.edu.nast.ebs.dto.response.BookingResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.BookingMapper;
import np.edu.nast.ebs.model.Booking;
import np.edu.nast.ebs.model.Event;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.BookingRepository;
import np.edu.nast.ebs.repository.EventRepository;
import np.edu.nast.ebs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

    private BookingRepository bookingRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, eventRepository, userRepository, bookingMapper);
    }

    @Test
    void testCreateBooking() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setEventId(1);
        dto.setCustomerId(2);

        Event event = new Event();
        event.setEventId(1);

        User customer = new User();
        customer.setUserId(2);

        Booking booking = new Booking();
        booking.setBookingId(1);
        booking.setEvent(event);
        booking.setCustomer(customer);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(1);

        when(bookingMapper.toEntity(dto)).thenReturn(booking); // fixed
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(userRepository.findById(2)).thenReturn(Optional.of(customer));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.create(dto);

        assertNotNull(result);
        assertEquals(1, result.getBookingId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testGetByIdBookingFound() {
        Booking booking = new Booking();
        booking.setBookingId(1);

        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(1);

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        BookingResponseDTO result = bookingService.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getBookingId());
    }

    @Test
    void testGetByIdBookingNotFound() {
        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getById(99));
    }

    @Test
    void testGetAllBookings() {
        Booking booking1 = new Booking();
        booking1.setBookingId(1);

        Booking booking2 = new Booking();
        booking2.setBookingId(2);

        BookingResponseDTO dto1 = new BookingResponseDTO();
        dto1.setBookingId(1);

        BookingResponseDTO dto2 = new BookingResponseDTO();
        dto2.setBookingId(2);

        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking1, booking2));
        when(bookingMapper.toDto(booking1)).thenReturn(dto1);
        when(bookingMapper.toDto(booking2)).thenReturn(dto2);

        List<BookingResponseDTO> result = bookingService.getAll();

        assertEquals(2, result.size());
        verify(bookingRepository).findAll();
    }
}
