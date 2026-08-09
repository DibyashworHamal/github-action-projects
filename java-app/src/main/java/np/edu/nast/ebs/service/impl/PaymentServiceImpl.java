package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.request.PaymentRequestDTO;
import np.edu.nast.ebs.dto.response.PaymentResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.PaymentMapper;
import np.edu.nast.ebs.model.Booking;
import np.edu.nast.ebs.model.Payment;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.BookingRepository;
import np.edu.nast.ebs.repository.PaymentRepository;
import np.edu.nast.ebs.service.NotificationService;
import np.edu.nast.ebs.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              PaymentMapper paymentMapper,
                              NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentMapper = paymentMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public PaymentResponseDTO makePayment(Integer bookingId, PaymentRequestDTO dto) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(BigDecimal.valueOf(dto.getAmount()));
        payment.setStatus(Payment.Status.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        bookingRepository.save(booking);

        User organizer = booking.getEvent().getOrganizer();
        User customer = booking.getCustomer();
        String eventTitle = booking.getEvent().getTitle();
        
        NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
        notificationDTO.setUserId(organizer.getUserId());
        notificationDTO.setTitle("New Booking for Your Event!");
        notificationDTO.setMessage(
            "'" + customer.getFullName() + "' has booked your event: '" + eventTitle + "'. Visit 'View Bookings' for details."
        );
        notificationService.sendNotification(notificationDTO);

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public List<PaymentResponseDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }
}