package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.EventRequestDTO;
import np.edu.nast.ebs.dto.response.EventResponseDTO;
import np.edu.nast.ebs.model.Event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "organizerId", target = "organizer.userId") 
    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "endDateTime", ignore = true)
    @Mapping(target = "totalTickets", ignore = true)
    @Mapping(target = "coverImagePath", ignore = true)
    @Mapping(target = "customCategory", ignore = true)
    @Mapping(target = "organizerName", ignore = true)
    @Mapping(target = "organizerContact", ignore = true)
    @Mapping(target = "eventWebsite", ignore = true)
    @Mapping(target = "featured", ignore = true)
    @Mapping(target = "registrationRequired", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "photos", ignore = true)
    Event toEntity(EventRequestDTO dto);
 
    @Mapping(source = "organizer.userId", target = "organizerId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "organizer.fullName", target = "organizerName")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "startDateTime", target = "startDateTime")
    @Mapping(source = "endDateTime", target = "endDateTime") 
    @Mapping(source = "coverImagePath", target = "coverImagePath")
    @Mapping(source = "customCategory", target = "customCategory")
    @Mapping(source = "totalTickets", target = "totalTickets") 
    @Mapping(source = "organizerContact", target = "organizerContact")
    @Mapping(source = "eventWebsite", target = "eventWebsite")
    
    @Mapping(source = "featured", target = "featured")
    @Mapping(source = "registrationRequired", target = "registrationRequired")
    EventResponseDTO toDto(Event event);
}