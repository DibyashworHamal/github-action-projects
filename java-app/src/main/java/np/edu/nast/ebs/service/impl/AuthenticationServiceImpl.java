package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.AuthRequest;
import np.edu.nast.ebs.dto.request.OtpVerificationRequest;
import np.edu.nast.ebs.dto.request.PasswordResetRequest;
import np.edu.nast.ebs.dto.request.RefreshTokenRequest;
import np.edu.nast.ebs.dto.response.AuthResponse;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.AuthenticationService;
import np.edu.nast.ebs.service.EmailService;
import np.edu.nast.ebs.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service 
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     AuthenticationManager authenticationManager,
                                     JwtUtil jwtUtil,
                                     EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Override
    public User signup(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already registered!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.CUSTOMER);        
        return userRepository.save(user);
    }

    @Override
    public AuthResponse signin(AuthRequest request) {
        // Step 1: Authenticate username and password.
        // This will throw BadCredentialsException if the password is wrong or user is disabled.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Step 2: Fetch the user details from the database.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Step 3: Check if the user has the correct role for this login endpoint.
        if (user.getRole() != User.Role.CUSTOMER) {
            // Throw an exception to prevent non-customers from logging into the mobile API.
            throw new BadCredentialsException("Access Denied: This login is for customers only.");
        }

        // Step 4: If role is correct, generate and return the JWT.
        String jwt = jwtUtil.generateToken(user);
        return new AuthResponse(jwt);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        throw new UnsupportedOperationException("Refresh token functionality is not yet implemented.");
    }
    
    @Override
    @Transactional
    public void verifyOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with this email."));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP.");
        }
        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired.");
        }

        user.setEnabled(true);
        user.setOtp(null); // Clear OTP after successful verification
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with this email."));
        if (user.isEnabled()) {
            throw new IllegalStateException("This account is already verified.");
        }
        // Generate and send a new OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    // A helper method to generate a 6-digit OTP
    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
    
    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found with that email address."));
        
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void finalizePasswordReset(PasswordResetRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // This method's only job is to update the password and clear the OTP.
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }
}