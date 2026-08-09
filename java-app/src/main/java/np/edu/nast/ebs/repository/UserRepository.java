package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.model.User.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
	Optional<User> findByPhoneNumber(String phoneNumber);
	List<User> findAllByRole(Role admin);
}
