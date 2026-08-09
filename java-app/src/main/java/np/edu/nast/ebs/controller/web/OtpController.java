package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.request.OtpVerificationRequest;
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
public class OtpController {

    private final AuthenticationService authService;

    public OtpController(AuthenticationService authService) {
        this.authService = authService;
    }

    @GetMapping("/verify-otp")
    public String showOtpPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processOtpVerification(@RequestParam("email") String email,
                                         @RequestParam("otp") String otp,
                                         RedirectAttributes redirectAttributes) {
        try {
            OtpVerificationRequest request = new OtpVerificationRequest();
            request.setEmail(email);
            request.setOtp(otp);
            authService.verifyOtp(request);
            
            redirectAttributes.addFlashAttribute("signup_success", true);
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addAttribute("email", email); // Pass email back to the GET mapping
            return "redirect:/verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String processResendOtp(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            authService.resendOtp(email);
            redirectAttributes.addFlashAttribute("successMessage", "A new OTP has been sent to your email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addAttribute("email", email);
        return "redirect:/verify-otp";
    }
    
    @GetMapping("/verify-otp-reset")
    public String showOtpResetPage(@RequestParam("email") String email, Model model) {
        // We check if the password request object was passed from the previous step
        if (!model.containsAttribute("passwordResetRequest")) {
            // If not, redirect back to the start of the flow
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "verify-otp-reset";
    }
    
    @PostMapping("/verify-otp-reset")
    public String processOtpReset(@RequestParam("email") String email,
                                  @RequestParam("otp") String otp,
                                  // We retrieve the new password data from the session
                                  @ModelAttribute("passwordResetRequest") PasswordResetRequest passwordResetRequest,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 1. Verify the OTP
            OtpVerificationRequest otpRequest = new OtpVerificationRequest();
            otpRequest.setEmail(email);
            otpRequest.setOtp(otp);
            authService.verifyOtp(otpRequest);
            
            // 2. If OTP is correct, finalize the password change
            authService.finalizePasswordReset(passwordResetRequest);
            
            redirectAttributes.addFlashAttribute("password_change_success", "Password reset successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addAttribute("email", email);
            // We must pass the password data back again for the form to work
            redirectAttributes.addFlashAttribute("passwordResetRequest", passwordResetRequest);
            return "redirect:/verify-otp-reset";
        }
    }
}