package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
