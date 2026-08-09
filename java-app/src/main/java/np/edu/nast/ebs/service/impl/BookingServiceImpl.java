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
import np.edu.nast.ebs.service.BookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository,
                              BookingMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public BookingResponseDTO create(BookingRequestDTO dto) {
    	 if (bookingRepository.existsByCustomer_UserIdAndEvent_EventId(dto.getCustomerId(), dto.getEventId())) {
    		 
             throw new IllegalArgumentException("You have already booked this event.");
         }
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Booking booking = mapper.toEntity(dto);
        booking.setEvent(event);
        booking.setCustomer(customer);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);

        return mapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDTO getById(Integer id) {
        return bookingRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Override
    public List<BookingResponseDTO> getAll() {
        return bookingRepository.findAll()
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookingResponseDTO> getBookingsForOrganizer(Integer organizerId) {
        return bookingRepository.findByEvent_Organizer_UserIdOrderByBookingDateDesc(organizerId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookingResponseDTO> getBookingsForCustomer(Integer customerId) {
        List<Booking> bookings = bookingRepository.findByCustomer_UserIdOrderByBookingDateDesc(customerId);
        List<BookingResponseDTO> dtos = bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        for (int i = 0; i < bookings.size(); i++) {
            Booking entity = bookings.get(i);
            BookingResponseDTO dto = dtos.get(i);
            if (entity.getEvent() != null) {
                dto.setEventPrice(entity.getEvent().getPrice());
            }
        }
        return dtos;
    }
}
