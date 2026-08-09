package np.edu.nast.ebs.controller;

import jakarta.validation.Valid;
import np.edu.nast.ebs.dto.request.AuthRequest;
import np.edu.nast.ebs.dto.request.OtpVerificationRequest;
import np.edu.nast.ebs.dto.request.PasswordOnlyRequest;
import np.edu.nast.ebs.dto.request.PasswordResetRequest;
import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.dto.response.AuthResponse;
import np.edu.nast.ebs.dto.response.MessageResponse;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.AuthenticationService;
import np.edu.nast.ebs.service.UserService;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationService authService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService,
    		AuthenticationService authService,
    		ObjectMapper objectMapper,
    		UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            userRequestDTO.setRole("CUSTOMER");
            userService.createUser(userRequestDTO);
            
            // Return a success message wrapped in our new JSON object
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(new MessageResponse("Registration successful! Please log in."));
        } catch (IllegalArgumentException e) {
            // Return a specific error message wrapped in JSON
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            // Return a generic error message wrapped in JSON
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new MessageResponse("An unexpected error occurred during registration."));
        }
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        try {
            authService.verifyOtp(request);
            return ResponseEntity.ok(new MessageResponse("Account verified successfully. You can now log in."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@RequestParam String email) {
        try {
            authService.resendOtp(email);
            return ResponseEntity.ok(new MessageResponse("A new OTP has been sent to your email."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.signin(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Return an error message wrapped in JSON for consistency
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(new MessageResponse("Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new MessageResponse("An error occurred during login."));
        }
    }
    
    @PostMapping("/forgot-password/initiate")
    public ResponseEntity<MessageResponse> initiateReset(@RequestParam("email") String email) {
        try {
            authService.initiatePasswordReset(email);
            return ResponseEntity.ok(new MessageResponse("An OTP has been sent to your email to reset your password."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password/finalize")
    public ResponseEntity<MessageResponse> finalizeReset(@Valid @RequestBody OtpVerificationRequest otpRequest,
                                                         @RequestHeader("X-Password-Reset-Data") String resetDataJson) {
        try {
        	
            User user = userRepository.findByEmail(otpRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));

            if (user.getOtp() == null || !user.getOtp().equals(otpRequest.getOtp())) {
                throw new IllegalArgumentException("Invalid OTP.");
            }
            if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("OTP has expired.");
            }
            PasswordOnlyRequest passwordOnlyRequest = objectMapper.readValue(resetDataJson, PasswordOnlyRequest.class);

            PasswordResetRequest finalPasswordRequest = new PasswordResetRequest();
            finalPasswordRequest.setEmail(otpRequest.getEmail()); 
            finalPasswordRequest.setNewPassword(passwordOnlyRequest.getNewPassword());
            finalPasswordRequest.setConfirmPassword(passwordOnlyRequest.getConfirmPassword());
            
            authService.finalizePasswordReset(finalPasswordRequest);
            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully. Please log in."));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing password reset data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to process request data."));
        }
    }
}