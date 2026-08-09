package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.UserRequestDTO;
import np.edu.nast.ebs.dto.response.UserResponseDTO;
import np.edu.nast.ebs.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(dto.getFirstName() + \" \" + dto.getLastName())")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mappings({
        // This expression safely splits the full name into a first name.
        @Mapping(target = "firstName", expression = "java(user.getFullName() != null ? user.getFullName().split(\" \")[0] : \"\")"),
        // This expression safely splits the full name into a last name, handling cases where there is no space.
        @Mapping(target = "lastName", expression = "java(user.getFullName() != null && user.getFullName().contains(\" \") ? user.getFullName().substring(user.getFullName().indexOf(\" \") + 1) : \"\")"),
        // This ignores the 'name' field, as it is redundant with firstName and lastName.
        @Mapping(target = "name", ignore = true)
    })
    UserResponseDTO toDto(User user);
}