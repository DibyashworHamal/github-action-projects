package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
