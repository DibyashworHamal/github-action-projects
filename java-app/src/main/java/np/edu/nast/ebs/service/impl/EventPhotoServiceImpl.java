package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.EventPhotoRequestDTO;
import np.edu.nast.ebs.dto.response.EventPhotoResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.EventPhotoMapper;
import np.edu.nast.ebs.model.Event;
import np.edu.nast.ebs.model.EventPhoto;
import np.edu.nast.ebs.repository.EventPhotoRepository;
import np.edu.nast.ebs.repository.EventRepository;
import np.edu.nast.ebs.service.EventPhotoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventPhotoServiceImpl implements EventPhotoService {

    private final EventRepository eventRepository;
    private final EventPhotoRepository photoRepository;
    private final EventPhotoMapper mapper;

    public EventPhotoServiceImpl(EventRepository eventRepository,
                                 EventPhotoRepository photoRepository,
                                 EventPhotoMapper mapper) {
        this.eventRepository = eventRepository;
        this.photoRepository = photoRepository;
        this.mapper = mapper;
    }

    @Override
    public EventPhotoResponseDTO add(EventPhotoRequestDTO dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        EventPhoto photo = mapper.toEntity(dto);
        photo.setEvent(event);
        photo.setPhotoUrl(dto.getPhotoUrl());
        return mapper.toDto(photoRepository.save(photo));
    }

    @Override
    public List<EventPhotoResponseDTO> getByEventId(Integer eventId) {
        return photoRepository.findAll().stream()
                .filter(p -> p.getEvent().getEventId().equals(eventId))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
