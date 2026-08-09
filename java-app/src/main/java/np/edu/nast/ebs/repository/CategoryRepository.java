package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
