package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.NotificationRequestDTO;
import np.edu.nast.ebs.dto.response.NotificationResponseDTO;
import np.edu.nast.ebs.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
    @Mapping(source = "userId", target = "user.userId")
    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Notification toEntity(NotificationRequestDTO dto);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    NotificationResponseDTO toDto(Notification notification);
}