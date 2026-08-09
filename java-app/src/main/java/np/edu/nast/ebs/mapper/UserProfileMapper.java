package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.UserProfileRequestDTO;
import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import np.edu.nast.ebs.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(source = "userId", target = "user.userId")
    @Mapping(target = "settingId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toEntity(UserProfileRequestDTO dto);

    @Mapping(source = "user.userId", target = "userId")
    UserProfileResponseDTO toDto(UserProfile userProfile);
}