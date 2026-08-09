package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.UserProfileMapper;
import np.edu.nast.ebs.model.UserProfile;
import np.edu.nast.ebs.repository.UserProfileRepository;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserProfileServiceImplTest {

    private UserProfileRepository profileRepository;
    private UserRepository userRepository;
    private UserProfileMapper userProfileMapper;
    private FileStorageService fileStorageService;
    private UserProfileServiceImpl service;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        // Mock all dependencies
        profileRepository = mock(UserProfileRepository.class);
        userRepository = mock(UserRepository.class);
        userProfileMapper = mock(UserProfileMapper.class);
        fileStorageService = mock(FileStorageService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserProfileServiceImpl(profileRepository, userRepository, userProfileMapper, fileStorageService, passwordEncoder);
    }

    @Test
    public void testGetById_WhenExists() {
        UserProfile profile = new UserProfile();
        profile.setSettingId(1);
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setSettingId(1);

        when(profileRepository.findById(1)).thenReturn(Optional.of(profile));
        when(userProfileMapper.toDto(profile)).thenReturn(dto);

        UserProfileResponseDTO result = service.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getSettingId());
        verify(profileRepository).findById(1);
    }

    @Test
    public void testGetById_WhenNotFound() {
        when(profileRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(99));
    }

    @Test
    public void testGetAll() {
        UserProfile profile1 = new UserProfile();
        profile1.setSettingId(1);
        UserProfile profile2 = new UserProfile();
        profile2.setSettingId(2);

        UserProfileResponseDTO dto1 = new UserProfileResponseDTO();
        dto1.setSettingId(1);
        UserProfileResponseDTO dto2 = new UserProfileResponseDTO();
        dto2.setSettingId(2);

        when(profileRepository.findAll()).thenReturn(Arrays.asList(profile1, profile2));
        when(userProfileMapper.toDto(profile1)).thenReturn(dto1);
        when(userProfileMapper.toDto(profile2)).thenReturn(dto2);

        List<UserProfileResponseDTO> result = service.getAll();

        assertEquals(2, result.size());
        verify(profileRepository).findAll();
    }

    @Test
    public void testDelete_WhenExists() {
        when(profileRepository.existsById(1)).thenReturn(true);
        service.delete(1);
        verify(profileRepository).deleteById(1);
    }

    @Test
    public void testDelete_WhenNotFound() {
        when(profileRepository.existsById(99)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99));
    }
}