package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.response.UserResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.UserMapper;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;
    private EmailService emailService; 

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, emailService);
        emailService = mock(EmailService.class);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setUserId(1);
        user1.setFullName("Alice Smith");

        User user2 = new User();
        user2.setUserId(2);
        user2.setFullName("Bob Smith");

        UserResponseDTO dto1 = new UserResponseDTO();
        dto1.setUserId(1);
        dto1.setFirstName("Alice");
        dto1.setLastName("Smith");
        dto1.setName("Alice Smith"); 

        UserResponseDTO dto2 = new UserResponseDTO();
        dto2.setUserId(2);
        dto2.setFirstName("Bob");
        dto2.setLastName("Smith");
        dto2.setName("Bob Smith"); 

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Alice Smith", result.get(0).getName());
        assertEquals("Bob Smith", result.get(1).getName());  
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserByIdFound() {
        User user = new User();
        user.setUserId(1);
        user.setFullName("Test User");

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(1);
        dto.setName("Test User");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDTO result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userRepository).findById(1);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99));
    }
}
