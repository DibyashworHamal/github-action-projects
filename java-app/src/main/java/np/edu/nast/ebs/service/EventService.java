package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.EventFormDTO;
import np.edu.nast.ebs.dto.request.EventRequestDTO;
import np.edu.nast.ebs.dto.response.EventResponseDTO;
import np.edu.nast.ebs.model.User;

import java.util.List;

public interface EventService {
    EventResponseDTO create(EventRequestDTO dto);
    EventResponseDTO getById(Integer id);
    List<EventResponseDTO> getAll();
    void delete(Integer id);
    EventResponseDTO createEventFromForm(EventFormDTO dto, User organizer);
    List<EventResponseDTO> getEventsByOrganizer(User organizer);
    EventResponseDTO updateEventFromForm(Integer eventId, EventFormDTO dto);
    List<EventResponseDTO> searchEvents(String query);
}
