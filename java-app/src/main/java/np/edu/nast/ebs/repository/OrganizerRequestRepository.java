package np.edu.nast.ebs.repository;

import np.edu.nast.ebs.model.OrganizerRequest;
import np.edu.nast.ebs.model.OrganizerRequest.ApprovalStatus;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.model.OrganizerRequest.PaymentStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRequestRepository extends JpaRepository<OrganizerRequest, Integer> {
	boolean existsByUserAndApprovalStatus(User user, OrganizerRequest.ApprovalStatus approvalStatus);
	boolean existsByUserAndApprovalStatusIn(User user, List<ApprovalStatus> ststuses);
	List<OrganizerRequest> findByApprovalStatus(OrganizerRequest.ApprovalStatus status);
	List<OrganizerRequest> findByBusinessNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(String businessName, String taxId);
	Optional<OrganizerRequest> findTopByUserOrderByRequestedAtDesc(User user);
	boolean existsByUserAndPaymentStatus(User user, PaymentStatus paymentStatus);
    Optional<OrganizerRequest> findTopByUser_EmailOrderByRequestedAtDesc(String email);
}
