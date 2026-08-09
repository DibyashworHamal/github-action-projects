package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.ChangePasswordRequest;
import np.edu.nast.ebs.dto.request.UserProfileRequestDTO;
import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    UserProfileResponseDTO create(UserProfileRequestDTO dto);
    UserProfileResponseDTO getById(Integer id);
    List<UserProfileResponseDTO> getAll();
    void delete(Integer id);
    Optional<UserProfileResponseDTO> findByUserId(Integer userId);
    UserProfileResponseDTO updateSettings(Integer userId, UserProfileRequestDTO dto);
    void updateProfilePicture(Integer userId, MultipartFile photo);
    void changePassword(Integer userId, ChangePasswordRequest request);
}