package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.dto.response.UserResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.UserMapper;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.EmailService;
import np.edu.nast.ebs.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        // 1. Check password and confirm password match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        // 2. Check if email already exists
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 3. Check if phone number already exists
        if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // 3.1 Validate phone number format (must be 10 digits and start with 98)
        if (!dto.getPhoneNumber().matches("^9[78]\\d{8}$")) {
            throw new IllegalArgumentException("Phone number must be a valid 10-digit Nepali number starting with 98");
        }

        // 4. Convert DTO to entity
        User user = userMapper.toEntity(dto);

        // 5. Set fields
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User.Role role = User.Role.valueOf(dto.getRole().toUpperCase().trim());
        user.setRole(role);
        
        // If the user is a customer||Organizer, generate OTP and keep them disabled.
        if (role == User.Role.ORGANIZER || role == User.Role.CUSTOMER) {
            String otp = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setOtp(otp);
            user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
            user.setEnabled(false); // User is not enabled until OTP verification
            
            emailService.sendOtpEmail(user.getEmail(), otp);
        } else {
            // Admins are enabled by default
            user.setEnabled(true);
        }

        // 6. Save and return response DTO
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream().map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User not found with id: " + id);
        userRepository.deleteById(id);
    }

    @Override
    public Integer getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public UserMapper getUserMapper() {
        return userMapper;
    }
}
