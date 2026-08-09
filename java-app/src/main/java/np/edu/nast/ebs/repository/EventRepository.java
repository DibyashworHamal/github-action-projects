package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.Event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
	
	List<Event> findByOrganizerUserIdOrderByStartDateTimeDesc(Integer userId);
	 List<Event> findByTitleContainingIgnoreCase(String searchTerm);
}
