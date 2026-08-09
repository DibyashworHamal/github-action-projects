package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Integer id);
    void deleteUser(Integer id);
    Integer getUserIdByEmail(String email);
}
