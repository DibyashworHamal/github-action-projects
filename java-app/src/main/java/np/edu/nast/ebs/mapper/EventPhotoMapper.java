package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.EventPhotoRequestDTO;
import np.edu.nast.ebs.dto.response.EventPhotoResponseDTO;
import np.edu.nast.ebs.model.EventPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventPhotoMapper {
    @Mapping(source = "eventId", target = "event.eventId")
    @Mapping(target = "photoId", ignore = true)
    EventPhoto toEntity(EventPhotoRequestDTO dto);

    @Mapping(source = "event.eventId", target = "eventId")
    EventPhotoResponseDTO toDto(EventPhoto eventPhoto);
}