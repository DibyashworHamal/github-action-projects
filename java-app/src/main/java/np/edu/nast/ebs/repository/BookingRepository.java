package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.Booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
	 List<Booking> findByEvent_Organizer_UserIdOrderByBookingDateDesc(Integer organizerId);
	 List<Booking> findByCustomer_UserIdOrderByBookingDateDesc(Integer customerId);
	 boolean existsByCustomer_UserIdAndEvent_EventId(Integer customerId, Integer eventId);
}
