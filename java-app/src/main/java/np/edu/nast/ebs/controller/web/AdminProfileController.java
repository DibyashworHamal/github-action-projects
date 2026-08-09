package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.request.ChangePasswordRequest;
import np.edu.nast.ebs.dto.request.UserProfileRequestDTO;
import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.UserProfileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/profile")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProfileController {

    private final UserProfileService userProfileService;

    public AdminProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public String showProfileSettings(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Fetch the existing profile data for display purposes
        UserProfileResponseDTO profileDTO = userProfileService.findByUserId(userDetails.getId())
            .orElse(new UserProfileResponseDTO());
        
        UserProfileRequestDTO formObject = new UserProfileRequestDTO();
     
        formObject.setBio(profileDTO.getBio());
        formObject.setTheme(profileDTO.getTheme() != null ? profileDTO.getTheme() : "LIGHT"); 
        formObject.setNotificationEmail(profileDTO.getNotificationEmail());
        
        model.addAttribute("settingsForm", formObject); 
        model.addAttribute("profile", profileDTO);    
        model.addAttribute("user", userDetails.getUser());
        
        return "admin/profile_settings"; 
    }
    
    @PostMapping("/picture/upload")
    public String uploadProfilePicture(@RequestParam("photo") MultipartFile photo,
                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        try {
            userProfileService.updateProfilePicture(userDetails.getId(), photo);
            redirectAttributes.addFlashAttribute("successMessage", "Profile picture updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading photo: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }
    
    @PostMapping("/save-settings")
    public String saveProfileSettings(@ModelAttribute("settingsForm") UserProfileRequestDTO settingsDto,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        try {
            userProfileService.updateSettings(userDetails.getId(), settingsDto);
            redirectAttributes.addFlashAttribute("successMessage", "Settings saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save settings: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }
    
    @PostMapping("/change-password")
    public String changeAdminPassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {
        try {
            // Check if new passwords match before sending to service
            if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "New password and confirmation do not match.");
                return "redirect:/admin/profile";
            }

            userProfileService.changePassword(userDetails.getId(), changePasswordRequest);
            
            // Invalidate the current session to force logout
            request.getSession().invalidate();

            // Redirect to the login page with a success message
            redirectAttributes.addFlashAttribute("password_change_success", "Password updated successfully! Please log in with your new password.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // This will catch errors from the service, like "Incorrect old password"
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "redirect:/admin/profile";
        }
    }
}