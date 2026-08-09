package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.response.NotificationResponseDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String showNotifications(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponseDTO> notifications = notificationService.findByUserId(userDetails.getId());
        model.addAttribute("notifications", notifications);
        return "admin/view_notifications";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteNotification(@PathVariable("id") Integer notificationId, 
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            NotificationResponseDTO notification = notificationService.findById(notificationId);
            
            if (!Objects.equals(notification.getUserId(), userDetails.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: You cannot clear this notification.");
                return "redirect:/admin/notifications";
            }
            notificationService.clearById(notificationId);
            redirectAttributes.addFlashAttribute("successMessage", "Notification cleared successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error clearing notification: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
    }
    
    @PostMapping("/delete/all")
    public String deleteAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            notificationService.clearAllByUserId(userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "All notifications have been cleared.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error clearing notifications: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
    }
}