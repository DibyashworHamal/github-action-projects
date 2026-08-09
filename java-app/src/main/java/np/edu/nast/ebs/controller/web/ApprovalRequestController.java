package np.edu.nast.ebs.controller.web;

import jakarta.validation.Valid;
import np.edu.nast.ebs.dto.request.NotificationRequestDTO; 
import np.edu.nast.ebs.dto.request.OrganizerRequestDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.NotificationService;
import np.edu.nast.ebs.service.OrganizerRequestService;
import np.edu.nast.ebs.model.User; 
import np.edu.nast.ebs.repository.UserRepository; 

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organizer")
@PreAuthorize("hasRole('ORGANIZER')")
public class ApprovalRequestController {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalRequestController.class);
    private final OrganizerRequestService requestService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public ApprovalRequestController(OrganizerRequestService requestService, NotificationService notificationService, UserRepository userRepository) {
        this.requestService = requestService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/request")
    public String showRequestForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isApproved = false;
        boolean isPending = false;
        Optional<OrganizerRequestResponseDTO> latestRequestOpt =
                requestService.findLatestRequestByUserId(userDetails.getUser().getUserId());

        if (latestRequestOpt.isPresent()) {
            OrganizerRequestResponseDTO latestRequest = latestRequestOpt.get();
            String status = latestRequest.getApprovalStatus(); 

            if ("APPROVED".equals(status)) {
                isApproved = true;
            } else if ("PENDING".equals(status)) {
                isPending = true;
            }
        }
        if (!isApproved && !isPending) {
            model.addAttribute("request", new OrganizerRequestDTO());
        }
 
        model.addAttribute("isApproved", isApproved);
        model.addAttribute("isPending", isPending);
        model.addAttribute("userEmail", userDetails.getUsername());
        
        return "organizer/approval_request";
    }

    @PostMapping("/request")
    public String submitRequest(@Valid @ModelAttribute("request") OrganizerRequestDTO dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors() || (dto.getDocument() != null && dto.getDocument().isEmpty())) {
            if (dto.getDocument() != null && dto.getDocument().isEmpty()) {
                 bindingResult.rejectValue("document", "error.document", "A document must be uploaded.");
            }
            logger.error("Validation errors found: {}", bindingResult.getAllErrors());
            model.addAttribute("userEmail", userDetails.getUsername());
            model.addAttribute("isApproved", false);
            model.addAttribute("isPending", false);
            return "organizer/approval_request";
        }

        try {
            dto.setUserId(userDetails.getUser().getUserId());
            OrganizerRequestResponseDTO createdRequest = requestService.createRequest(dto);
            List<User> admins = userRepository.findAllByRole(User.Role.ADMIN);
            
            for (User admin : admins) {
                NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
                notificationDTO.setUserId(admin.getUserId());
                notificationDTO.setTitle("New Organizer Request");
                notificationDTO.setMessage("Application from '" + userDetails.getUser().getFullName() + "' requires your approval.");
                notificationService.sendNotification(notificationDTO);
            }
             logger.info("Sent notifications to " + admins.size() + " admin(s) for the new request.");

            boolean alreadyPaid = requestService.hasPaidRequest(userDetails.getUser().getUserId());

            if (alreadyPaid) {
                requestService.processPaymentForRequest(createdRequest.getRequestId(), userDetails.getUser().getUserId(), BigDecimal.ZERO);
                logger.info("User {} has a previous paid request. Skipping payment for new request {}.", userDetails.getUsername(), createdRequest.getRequestId());
                redirectAttributes.addFlashAttribute("successMessage", "Your new request has been submitted successfully for review!");
                return "redirect:/organizer/dashboard";
            } else {
                redirectAttributes.addAttribute("requestId", createdRequest.getRequestId());
                logger.info("New user request created successfully with ID {}. Redirecting to payment.", createdRequest.getRequestId());
                return "redirect:/payment/make";
            }

        } catch (Exception e) {
            logger.error("Error during request submission for user {}", userDetails.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting request: " + e.getMessage());
            return "redirect:/organizer/dashboard";
        }
    }
    
    @GetMapping("/status")
    public String showStatusPage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<OrganizerRequestResponseDTO> latestRequest =
                requestService.findLatestRequestByUserId(userDetails.getUser().getUserId());

        if (latestRequest.isPresent()) {
            model.addAttribute("hasRequest", true);
            model.addAttribute("requestStatus", latestRequest.get());
        } else {
            model.addAttribute("hasRequest", false);
        }
        return "organizer/organizer_status";
    }
}