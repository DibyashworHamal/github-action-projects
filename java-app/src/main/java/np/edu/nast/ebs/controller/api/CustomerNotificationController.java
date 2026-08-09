package np.edu.nast.ebs.controller.api;

import np.edu.nast.ebs.dto.response.NotificationResponseDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/customer/notifications")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerNotificationController {

    private final NotificationService notificationService;

    public CustomerNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Fetches all non-cleared notifications for the logged-in customer.
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponseDTO> notifications = notificationService.findByUserId(userDetails.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Clears a single notification by setting its status to CLEARED.
     */
    @PostMapping("/{id}/clear")
    public ResponseEntity<Void> clearNotification(@PathVariable("id") Integer notificationId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Security check: ensure the user is clearing their own notification
        NotificationResponseDTO notification = notificationService.findById(notificationId);
        if (!Objects.equals(notification.getUserId(), userDetails.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        notificationService.clearById(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Clears all of the user's notifications.
     */
    @PostMapping("/clear-all")
    public ResponseEntity<Void> clearAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.clearAllByUserId(userDetails.getId());
        return ResponseEntity.ok().build();
    }
}