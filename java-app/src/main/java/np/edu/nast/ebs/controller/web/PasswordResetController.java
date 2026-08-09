package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.request.PasswordResetRequest;
import np.edu.nast.ebs.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    private final AuthenticationService authService;

    public PasswordResetController(AuthenticationService authService) {
        this.authService = authService;
    }

    // --- Step 1: Show the form to find the account by email ---
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password-find"; // -> forgot-password-find.html
    }

    // --- Step 2: Process the email submission ---
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            // This just checks if the user exists and is an Organizer/Customer. It doesn't send the OTP yet.
            authService.initiatePasswordReset(email);
            // On success, redirect to the page where they can set a new password.
            redirectAttributes.addAttribute("email", email);
            return "redirect:/reset-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    // --- Step 3: Show the form to set a new password ---
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        // We pass an empty request object for the form to bind to
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());
        return "reset-password"; // -> reset-password.html
    }

    // --- Step 4: Process the new password and send the OTP ---
    @PostMapping("/reset-password")
    public String processResetPassword(@ModelAttribute PasswordResetRequest request, RedirectAttributes redirectAttributes) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            redirectAttributes.addAttribute("email", request.getEmail());
            return "redirect:/reset-password";
        }
        
        redirectAttributes.addAttribute("email", request.getEmail());
        // We will pass the new password data to the OTP page via flash attributes.
        redirectAttributes.addFlashAttribute("passwordResetRequest", request);
        
        return "redirect:/verify-otp-reset";
    }
}