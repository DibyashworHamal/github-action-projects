package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.response.NotificationResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.NotificationMapper;
import np.edu.nast.ebs.model.Notification;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.NotificationRepository;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper mapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper mapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public NotificationResponseDTO sendNotification(NotificationRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = mapper.toEntity(dto);
        notification.setUser(user);
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setStatus(Notification.Status.UNREAD);
        notification.setSentAt(LocalDateTime.now());

        return mapper.toDto(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponseDTO> getAll() {
        return notificationRepository.findAll()
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponseDTO> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Cannot find notifications for non-existent user with ID: " + userId);
        }
        // Find all notifications and filter by user ID, then sort by newest first
        return notificationRepository.findAll().stream()
        		.filter(n -> n.getUser().getUserId().equals(userId) && n.getStatus() != Notification.Status.CLEARED)
                .sorted(Comparator.comparing(Notification::getSentAt).reversed())
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificationResponseDTO> findAllByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        // This version does NOT filter by status.
        return notificationRepository.findAll().stream()
                .filter(n -> n.getUser().getUserId().equals(userId))
                .sorted(Comparator.comparing(Notification::getSentAt).reversed())
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void clearById(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));
 
        notification.setStatus(Notification.Status.CLEARED);
        notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void clearAllByUserId(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            
        List<Notification> notificationsToClear = user.getNotifications().stream()
                .filter(n -> n.getStatus() != Notification.Status.CLEARED)
                .toList();
        
        if (!notificationsToClear.isEmpty()) {
            for (Notification notification : notificationsToClear) {
                notification.setStatus(Notification.Status.CLEARED);
            }
            notificationRepository.saveAll(notificationsToClear);
        }
    }
    @Override
    public NotificationResponseDTO findById(Integer notificationId) {
        return notificationRepository.findById(notificationId)
                .map(mapper::toDto) 
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }
}

