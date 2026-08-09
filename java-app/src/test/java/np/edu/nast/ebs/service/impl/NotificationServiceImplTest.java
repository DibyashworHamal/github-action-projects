package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.response.NotificationResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.NotificationMapper;
import np.edu.nast.ebs.model.Notification;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.NotificationRepository;
import np.edu.nast.ebs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private NotificationMapper mapper;
    private NotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = mock(NotificationMapper.class);
        service = new NotificationServiceImpl(notificationRepository, userRepository, mapper);
    }

    @Test
    void testSendNotification_Success() {
        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(1);
        dto.setTitle("Reminder");

        User user = new User();
        user.setUserId(1);

        Notification notification = new Notification();
        notification.setTitle("Reminder");

        Notification savedNotification = new Notification();
        savedNotification.setTitle("Reminder");
        savedNotification.setUser(user);

        NotificationResponseDTO responseDTO = new NotificationResponseDTO();
        responseDTO.setTitle("Reminder");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(mapper.toEntity(dto)).thenReturn(notification);
        when(notificationRepository.save(notification)).thenReturn(savedNotification);
        when(mapper.toDto(savedNotification)).thenReturn(responseDTO);

        NotificationResponseDTO result = service.sendNotification(dto);

        assertNotNull(result);
        assertEquals("Reminder", result.getTitle());
    }

    @Test
    void testSendNotification_UserNotFound() {
        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(99);
        dto.setTitle("Title");
        dto.setMessage("Message");

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.sendNotification(dto));
        verify(userRepository).findById(99);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetAll_ReturnsList() {
        Notification n1 = new Notification();
        n1.setTitle("A");

        Notification n2 = new Notification();
        n2.setTitle("B");

        NotificationResponseDTO dto1 = new NotificationResponseDTO();
        dto1.setTitle("A");

        NotificationResponseDTO dto2 = new NotificationResponseDTO();
        dto2.setTitle("B");

        when(notificationRepository.findAll()).thenReturn(Arrays.asList(n1, n2));
        when(mapper.toDto(n1)).thenReturn(dto1);
        when(mapper.toDto(n2)).thenReturn(dto2);

        List<NotificationResponseDTO> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getTitle());
        assertEquals("B", result.get(1).getTitle());

        verify(notificationRepository).findAll();
        verify(mapper).toDto(n1);
        verify(mapper).toDto(n2);
    }
}
