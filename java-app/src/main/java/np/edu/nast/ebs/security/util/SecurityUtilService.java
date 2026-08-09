package np.edu.nast.ebs.security.util;

import np.edu.nast.ebs.model.OrganizerRequest;
import np.edu.nast.ebs.repository.OrganizerRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityUtilService") 
public class SecurityUtilService {
	
	private static final String SUPER_ADMIN_EMAIL = "admin@example.com";

    private final OrganizerRequestRepository requestRepository;

    @Autowired
    public SecurityUtilService(OrganizerRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Checks if the currently authenticated organizer is approved.
     * This is the method we'll call from our security annotations.
     * @return true if the organizer has an APPROVED request, false otherwise.
     */
    public boolean isOrganizerApproved() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is not logged in, they are definitely not approved.
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get the email (username) from the security context
        String email = authentication.getName();

        // Check the database for the latest request status for this user.
        // We query the repository directly for performance and to avoid circular dependencies.
        Optional<OrganizerRequest> latestRequest = requestRepository.findTopByUser_EmailOrderByRequestedAtDesc(email);

        // Return true ONLY if a request exists AND its status is APPROVED
        return latestRequest
                .map(req -> req.getApprovalStatus() == OrganizerRequest.ApprovalStatus.APPROVED)
                .orElse(false);
    }
    
    public boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get the email (username) from the security context
        String email = authentication.getName();

        // Return true ONLY if the authenticated user's email matches our defined super admin email.
        return SUPER_ADMIN_EMAIL.equalsIgnoreCase(email);
    }
}