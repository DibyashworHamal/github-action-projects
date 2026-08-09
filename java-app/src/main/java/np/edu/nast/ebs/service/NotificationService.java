package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.response.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {
    NotificationResponseDTO sendNotification(NotificationRequestDTO dto);
    List<NotificationResponseDTO> getAll();
    List<NotificationResponseDTO> findByUserId(Integer userId);
    List<NotificationResponseDTO> findAllByUserId(Integer userId);
    void clearById(Integer notificationId);
    void clearAllByUserId(Integer userId);
    NotificationResponseDTO findById(Integer notificationId);
}
