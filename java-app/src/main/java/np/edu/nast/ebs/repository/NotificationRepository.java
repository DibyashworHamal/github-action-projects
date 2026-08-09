package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
