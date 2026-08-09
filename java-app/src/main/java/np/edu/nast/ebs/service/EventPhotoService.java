package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.EventPhotoRequestDTO;
import np.edu.nast.ebs.dto.response.EventPhotoResponseDTO;

import java.util.List;

public interface EventPhotoService {
    EventPhotoResponseDTO add(EventPhotoRequestDTO dto);
    List<EventPhotoResponseDTO> getByEventId(Integer eventId);
}
