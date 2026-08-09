package np.edu.nast.ebs.mapper;

import np.edu.nast.ebs.dto.request.OrganizerRequestDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.model.OrganizerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrganizerRequestMapper {

    @Mappings({
        @Mapping(source = "userId", target = "user.userId"),
        
        @Mapping(target = "requestId", ignore = true),
        @Mapping(target = "documentPath", ignore = true),
        @Mapping(target = "paymentStatus", ignore = true),
        @Mapping(target = "approvalStatus", ignore = true),
        @Mapping(target = "requestedAt", ignore = true),
        @Mapping(target = "approvedAt", ignore = true),
        @Mapping(target = "paidAt", ignore = true),
        @Mapping(target = "adminComments", ignore = true),
        @Mapping(target = "paymentAmount", ignore = true)
    })
    OrganizerRequest toEntity(OrganizerRequestDTO dto);

    
    @Mappings({
        @Mapping(source = "user.userId", target = "userId"),
        @Mapping(source = "user.fullName", target = "userFullName"), 
        @Mapping(source = "user.email", target = "userEmail"),
        @Mapping(source = "processedBy.fullName", target = "processedByAdminName")
    })
    OrganizerRequestResponseDTO toDto(OrganizerRequest request);
}