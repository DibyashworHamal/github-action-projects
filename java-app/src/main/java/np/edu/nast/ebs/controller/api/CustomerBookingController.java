package np.edu.nast.ebs.controller.api;

import jakarta.validation.Valid;
import np.edu.nast.ebs.dto.request.BookingRequestDTO;
import np.edu.nast.ebs.dto.request.PaymentRequestDTO;
import np.edu.nast.ebs.dto.response.BookingResponseDTO;
import np.edu.nast.ebs.dto.response.MessageResponse;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.BookingService;
import np.edu.nast.ebs.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/bookings")
public class CustomerBookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    public CustomerBookingController(BookingService bookingService, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsForCustomer(userDetails.getId());
        return ResponseEntity.ok(bookings);
    }
 
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> payload,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("User not authenticated."));
        }
        try {
            Object eventIdObject = payload.get("eventId");
            if (eventIdObject == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("eventId is missing from the request."));
            }
            Integer eventId = (Integer) eventIdObject;
            BookingRequestDTO bookingRequest = new BookingRequestDTO();
            bookingRequest.setEventId(eventId);
            bookingRequest.setCustomerId(userDetails.getUser().getUserId());
            BookingResponseDTO createdBooking = bookingService.create(bookingRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid format for eventId."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{bookingId}/pay")
    public ResponseEntity<MessageResponse> makePaymentForBooking(@PathVariable Integer bookingId,
                                                                 @Valid @RequestBody PaymentRequestDTO paymentDto) {
        paymentService.makePayment(bookingId, paymentDto);
        return ResponseEntity.ok(new MessageResponse("Booking confirmed successfully!"));
    }
}