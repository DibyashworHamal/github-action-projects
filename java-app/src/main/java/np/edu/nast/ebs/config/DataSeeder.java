package np.edu.nast.ebs.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import np.edu.nast.ebs.model.*;
import np.edu.nast.ebs.repository.*;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import np.edu.nast.ebs.model.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final FeedbackRepository feedbackRepository;
    private final OrganizerRequestRepository organizerRequestRepository;
    private final EventPhotoRepository eventPhotoRepository;
    private final NotificationRepository notificationRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedData() {
        User admin, organizer, customer;

        if (userRepository.count() == 0) {
            admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPhoneNumber("9875469820");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);

            organizer = new User();
            organizer.setFullName("Organizer One");
            organizer.setEmail("organizer@example.com");
            organizer.setPhoneNumber("9875469821");
            organizer.setPassword(passwordEncoder.encode("organizer123")); 
            organizer.setRole(User.Role.ORGANIZER);
            organizer.setEnabled(true);
            organizer.setCreatedAt(LocalDateTime.now());
            userRepository.save(organizer);

            customer = new User();
            customer.setFullName("Customer One");
            customer.setEmail("customer@example.com");
            customer.setPhoneNumber("9875469822");
            customer.setPassword(passwordEncoder.encode("customer123")); 
            customer.setRole(User.Role.CUSTOMER);
            customer.setEnabled(true);
            customer.setCreatedAt(LocalDateTime.now());
            userRepository.save(customer);

            admin = userRepository.save(admin);
            organizer = userRepository.save(organizer);
            customer = userRepository.save(customer);
        } else {
            admin = userRepository.findByEmail("admin@example.com").orElse(null);
            organizer = userRepository.findByEmail("organizer@example.com").orElse(null);
            customer = userRepository.findByEmail("customer@example.com").orElse(null);
        }

        if (categoryRepository.count() == 0) {
            Category category = new Category();
            category.setName("Music");
            category.setDescription("Live music events");
            categoryRepository.save(category);
        }

        if (eventRepository.count() == 0 && organizer != null) {
            Event event = new Event();
            event.setTitle("Spring Festival");
            event.setDescription("Seasonal music event");
            event.setLocation("Kathmandu");
            event.setStartDateTime(LocalDateTime.now());
            event.setEndDateTime(LocalDateTime.now().plusDays(15));
            event.setPrice(new BigDecimal("500.00"));
            event.setOrganizer(organizer);
            event.setCategory(categoryRepository.findAll().get(0));
            event.setTotalTickets(100); 
            event.setCreatedAt(LocalDateTime.now());
            eventRepository.save(event);
        }
       
        if (bookingRepository.count() == 0 && customer != null && eventRepository.count() > 0) {
            Booking booking = new Booking();
            booking.setEvent(eventRepository.findAll().get(0));
            booking.setCustomer(customer);
            booking.setBookingDate(LocalDateTime.now());
            booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
            bookingRepository.save(booking);
        }

        if (paymentRepository.count() == 0 && bookingRepository.count() > 0) {
            Payment payment = new Payment();
            payment.setBooking(bookingRepository.findAll().get(0));
            payment.setAmount(BigDecimal.valueOf(500));
            payment.setStatus(Payment.Status.PAID);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        if (feedbackRepository.count() == 0) {
            Feedback feedback = new Feedback();
            feedback.setBooking(bookingRepository.findAll().get(0));
            feedback.setRating(4);
            feedback.setComment("Great event!");
            feedback.setSubmittedAt(LocalDateTime.now());
            feedbackRepository.save(feedback);
        }

        if (organizerRequestRepository.count() == 0 && customer != null) {
            OrganizerRequest request = new OrganizerRequest();
            request.setUser(customer);
            request.setBusinessName("Event Co. Pvt. Ltd.");
            request.setContactPhone("9800000000");
            request.setTaxId("PAN1234567");
            request.setPaymentStatus(OrganizerRequest.PaymentStatus.PENDING);
            request.setApprovalStatus(OrganizerRequest.ApprovalStatus.PENDING);
            request.setRequestedAt(LocalDateTime.now());
            organizerRequestRepository.save(request);
        }

        if (eventPhotoRepository.count() == 0) {
            EventPhoto photo = new EventPhoto();
            photo.setEvent(eventRepository.findAll().get(0));
            photo.setPhotoUrl("http://example.com/photo.jpg");
            eventPhotoRepository.save(photo);
        }

        if (notificationRepository.count() == 0 && customer != null) {
            Notification notification = new Notification();
            notification.setUser(customer);
            notification.setTitle("Booking Confirmed");
            notification.setMessage("Your booking is confirmed.");
            notification.setStatus(Notification.Status.UNREAD);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        if (userProfileRepository.count() == 0 && customer != null) {
            UserProfile profile = new UserProfile();
            profile.setUser(customer);
            profile.setBio("Hello, I am a customer.");
            profile.setPhoneNumber("9876543210");
            profile.setProfilePicture("http://example.com/profile.jpg");
            userProfileRepository.save(profile);
        }

        System.out.println("All sample data seeded successfully.");
    }
}