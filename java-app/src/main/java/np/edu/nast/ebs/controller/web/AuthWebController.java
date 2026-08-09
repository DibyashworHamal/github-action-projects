package np.edu.nast.ebs.controller.web;

import jakarta.validation.Valid;
import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    private final UserService userService;

    @Autowired
    public AuthWebController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new UserRequestDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") UserRequestDTO userDto,
                                BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "signup"; 
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            model.addAttribute("passwordError", "Passwords do not match.");
            return "signup";
        }

        try {
            userService.createUser(userDto);
            // On successful user creation, redirect to the OTP verification page, passing the email.
            redirectAttributes.addAttribute("email", userDto.getEmail());
            return "redirect:/verify-otp"; 
            
        } catch (Exception e) {
            model.addAttribute("signupError", e.getMessage());
            return "signup";
        }
    }
}