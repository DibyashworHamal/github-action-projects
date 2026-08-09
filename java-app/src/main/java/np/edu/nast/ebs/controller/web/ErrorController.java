package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.OrganizerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    private final OrganizerRequestService organizerRequestService;

    @Autowired
    public ErrorController(OrganizerRequestService organizerRequestService) {
        this.organizerRequestService = organizerRequestService;
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String message = "You do not have permission to access this page.";
        String subMessage = "Please contact support if you believe this is an error.";

        // Check if the user is an organizer
        if (userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"))) {
            Optional<OrganizerRequestResponseDTO> latestRequest =
                organizerRequestService.findLatestRequestByUserId(userDetails.getUser().getUserId());

            String status = latestRequest.map(OrganizerRequestResponseDTO::getApprovalStatus).orElse("NOT_REQUESTED");

            switch (status) {
                case "PENDING":
                    message = "Your Approval Request is Currently Pending";
                    subMessage = "You will gain access to this feature once an administrator approves your request.";
                    break;
                case "REJECTED":
                    message = "Access Restricted: Previous Request was Rejected";
                    subMessage = "Please submit a new approval request from your dashboard to gain access.";
                    break;
                case "NOT_REQUESTED":
                    message = "Access Restricted: Approval Required";
                    subMessage = "You need to make an approval request first to access this feature.";
                    break;
            }
        }

        model.addAttribute("errorMessage", message);
        model.addAttribute("errorSubMessage", subMessage);
        return "access-denied";
    }
}