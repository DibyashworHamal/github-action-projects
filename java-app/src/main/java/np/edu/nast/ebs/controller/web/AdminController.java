package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.request.AdminCreationRequestDTO;
import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.dto.response.EventResponseDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.dto.response.UserResponseDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.security.util.SecurityUtilService;
import np.edu.nast.ebs.service.EventService;
import np.edu.nast.ebs.service.NotificationService;
import np.edu.nast.ebs.service.OrganizerRequestService;
import np.edu.nast.ebs.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid; 

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final OrganizerRequestService organizerRequestService;
    private final UserService userService;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final SecurityUtilService securityUtilService;
    
    public AdminController(OrganizerRequestService organizerRequestService, 
                           UserService userService, 
                           EventService eventService,
                           NotificationService notificationService,
                           SecurityUtilService securityUtilService) {
        this.organizerRequestService = organizerRequestService;
        this.userService = userService;
        this.eventService = eventService;
        this.notificationService = notificationService;
        this.securityUtilService = securityUtilService;
        
    }
    
    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "admin/admin_dashboard";
    }
    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<UserResponseDTO> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "admin/view_users"; // Path to the new HTML page
    }
    @GetMapping("/events")
    public String viewAllEvents(Model model) {
        List<EventResponseDTO> allEvents = eventService.getAll();
        model.addAttribute("events", allEvents);
        return "admin/view_events"; // Path to the new HTML page
    }
    @GetMapping("/requests")
    public String showOrganizerRequests(Model model) { 
        model.addAttribute("requests", organizerRequestService.findPendingRequests());
        return "admin/organizer_requests";
    }
    
    @GetMapping("/requests/detail/{id}")
    public String showRequestDetail(@PathVariable("id") Integer requestId, Model model, RedirectAttributes redirectAttributes) {
        Optional<OrganizerRequestResponseDTO> requestOpt = organizerRequestService.findRequestById(requestId);
        if (requestOpt.isPresent()) {
            model.addAttribute("request", requestOpt.get());
            return "admin/organizer_request_detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Request not found with ID: " + requestId);
            return "redirect:/admin/requests";
        }
    }
    
    @PostMapping("/requests/approve/{id}")
    public String approveRequest(@PathVariable("id") Integer requestId,
                                 @AuthenticationPrincipal CustomUserDetails adminDetails, // <-- GET LOGGED-IN ADMIN
                                 RedirectAttributes redirectAttributes) {
        try {
        	 OrganizerRequestResponseDTO approvedRequest = organizerRequestService.approveRequest(requestId, adminDetails.getUser());
            
            // Send notification to the organizer
            NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
            notificationDTO.setUserId(approvedRequest.getUserId());
            notificationDTO.setTitle("Congratulations! Your Application is Approved");
            notificationDTO.setMessage("Your organizer application has been approved. You can now start creating events from your dashboard.");
            notificationService.sendNotification(notificationDTO);
            
            redirectAttributes.addFlashAttribute("successMessage", "Request #" + requestId + " has been approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving request: " + e.getMessage());
        }
        return "redirect:/admin/requests";
    }
    
    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable("id") Integer requestId,
                                @RequestParam(value = "adminComments", required = false) String comments,
                                @AuthenticationPrincipal CustomUserDetails adminDetails, // <-- GET LOGGED-IN ADMIN
                                RedirectAttributes redirectAttributes) {
        try {
        	
        	 OrganizerRequestResponseDTO rejectedRequest = organizerRequestService.rejectRequest(requestId, comments, adminDetails.getUser());
            
            NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
            notificationDTO.setUserId(rejectedRequest.getUserId());
            notificationDTO.setTitle("Update on Your Organizer Application");
            String message = "Your organizer application has been rejected. Reason: " + 
                             (comments != null && !comments.isEmpty() ? comments : "No reason provided.");
            notificationDTO.setMessage(message);
            notificationService.sendNotification(notificationDTO);

            redirectAttributes.addFlashAttribute("successMessage", "Request #" + requestId + " has been rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting request: " + e.getMessage());
        }
        return "redirect:/admin/requests";
    }

	public NotificationService getNotificationService() {
		return notificationService;
	}
	
	 @GetMapping("/add-admin")
	    @PreAuthorize("@securityUtilService.isSuperAdmin()")
	    public String showAddAdminForm(Model model) {
	        model.addAttribute("adminUser", new AdminCreationRequestDTO());
	        return "admin/add-admin";
	    }
    
	 @PostMapping("/add-admin")
	    @PreAuthorize("@securityUtilService.isSuperAdmin()")
	    public String processAddAdminForm(@Valid @ModelAttribute("adminUser") AdminCreationRequestDTO adminDto, 
	                                      BindingResult result,
	                                      RedirectAttributes redirectAttributes) {
	        
	        if (result.hasErrors()) {
	            return "admin/add-admin";
	        }

	        if (!adminDto.getPassword().equals(adminDto.getConfirmPassword())) {
	            result.rejectValue("confirmPassword", "error.user", "Passwords do not match.");
	            return "admin/add-admin";
	        }

	        try {
	        	
	            UserRequestDTO userDto = new UserRequestDTO();
	            userDto.setFirstName(adminDto.getFirstName());
	            userDto.setLastName(adminDto.getLastName());
	            userDto.setEmail(adminDto.getEmail());
	            userDto.setPhoneNumber(adminDto.getPhoneNumber());
	            userDto.setPassword(adminDto.getPassword());
	            userDto.setConfirmPassword(adminDto.getConfirmPassword());
	            userDto.setRole("ADMIN"); 

	            userService.createUser(userDto);
	            redirectAttributes.addFlashAttribute("successMessage", "New admin user created successfully!");
	            return "redirect:/admin/users";
	        } catch (IllegalArgumentException e) {
	            result.rejectValue("email", "error.user", e.getMessage());
	            return "admin/add-admin";
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
	            return "redirect:/admin/add-admin";
	        }
	    }

	public SecurityUtilService getSecurityUtilService() {
		return securityUtilService;
	}
}