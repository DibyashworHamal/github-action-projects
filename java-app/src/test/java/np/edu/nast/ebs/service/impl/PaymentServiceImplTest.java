package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.PaymentRequestDTO;
import np.edu.nast.ebs.dto.response.PaymentResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.PaymentMapper;
import np.edu.nast.ebs.model.Booking;
import np.edu.nast.ebs.model.Event;
import np.edu.nast.ebs.model.Payment;
import np.edu.nast.ebs.model.User; 
import np.edu.nast.ebs.repository.BookingRepository;
import np.edu.nast.ebs.repository.PaymentRepository;
import np.edu.nast.ebs.service.NotificationService; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal; 
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    private PaymentRepository paymentRepository;
    private BookingRepository bookingRepository;
    private PaymentMapper paymentMapper;
    private NotificationService notificationService; 
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        // Mock all dependencies
        paymentRepository = mock(PaymentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        paymentMapper = mock(PaymentMapper.class);
        notificationService = mock(NotificationService.class);

        paymentService = new PaymentServiceImpl(paymentRepository, bookingRepository, paymentMapper, notificationService);
    }

    @Test
    void testMakePayment_Success() {
      
    	Integer bookingId = 1;
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAmount(100.0);

        User mockOrganizer = new User();
        mockOrganizer.setUserId(10);

        User mockCustomer = new User();
        mockCustomer.setUserId(20);
        mockCustomer.setFullName("Test Customer");

        Event mockEvent = new Event();
        mockEvent.setOrganizer(mockOrganizer);
        mockEvent.setTitle("Test Event");

        Booking booking = new Booking();
        booking.setBookingId(1);
        booking.setEvent(mockEvent); 
        booking.setCustomer(mockCustomer); 

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(100.0)); // Use BigDecimal

        Payment savedPayment = new Payment();
        savedPayment.setPaymentId(1);
        savedPayment.setAmount(BigDecimal.valueOf(100.0));

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setPaymentId(1);

        // Mock the repository and mapper calls
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(paymentMapper.toEntity(dto)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking); 
        when(paymentMapper.toDto(savedPayment)).thenReturn(responseDTO);

        // Act
        paymentService.makePayment(bookingId, dto);

        // Assert
        verify(notificationService, times(1)).sendNotification(any());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testMakePayment_BookingNotFound() {
        Integer bookingId = 99;
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAmount(100.0);

        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.makePayment(bookingId, dto));
    }

    @Test
    void testGetAllPayments() {
        Payment payment1 = new Payment();
        payment1.setPaymentId(1);

        Payment payment2 = new Payment();
        payment2.setPaymentId(2);

        PaymentResponseDTO dto1 = new PaymentResponseDTO();
        dto1.setPaymentId(1);

        PaymentResponseDTO dto2 = new PaymentResponseDTO();
        dto2.setPaymentId(2);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));
        when(paymentMapper.toDto(payment1)).thenReturn(dto1);
        when(paymentMapper.toDto(payment2)).thenReturn(dto2);

        List<PaymentResponseDTO> result = paymentService.getAll();

        assertEquals(2, result.size());
        verify(paymentRepository).findAll();
    }
}