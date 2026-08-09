package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.UserProfile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
	
	 Optional<UserProfile> findByUser_UserId(Integer userId);
}
