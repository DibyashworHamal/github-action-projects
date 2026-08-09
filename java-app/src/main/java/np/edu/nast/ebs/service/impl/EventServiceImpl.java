package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.EventFormDTO;
import np.edu.nast.ebs.dto.request.EventRequestDTO;
import np.edu.nast.ebs.dto.response.EventResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.EventMapper;
import np.edu.nast.ebs.model.Category;
import np.edu.nast.ebs.model.Event;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.CategoryRepository;
import np.edu.nast.ebs.repository.EventRepository;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.EventImageStorageService;
import np.edu.nast.ebs.service.EventService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper mapper;
    private final EventImageStorageService eventImageStorageService;

    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            EventMapper mapper,
                            EventImageStorageService eventImageStorageService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.eventImageStorageService = eventImageStorageService;
    }

    @Override
    public EventResponseDTO create(EventRequestDTO dto) {
        User organizer = userRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Event event = mapper.toEntity(dto);
        event.setOrganizer(organizer);
        event.setCategory(category);
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getStartDateTime().plusHours(2));
        event.setPrice(dto.getPrice());
        event.setCreatedAt(LocalDateTime.now());
        event.setTotalTickets(100);

        return mapper.toDto(eventRepository.save(event));
    }
    
    @Override
    public EventResponseDTO getById(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return mapper.toDto(event);
    }

    @Override
    public List<EventResponseDTO> getAll() {
        return eventRepository.findAll()
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (!eventRepository.existsById(id))
            throw new ResourceNotFoundException("Event not found with id: " + id);
        eventRepository.deleteById(id);
    }

	 @Override
	 public EventResponseDTO createEventFromForm(EventFormDTO dto, User organizer) {
	    Event event = new Event();
	    
	    if (dto.getCategoryId() != null && dto.getCategoryId() == -1 && 
	        dto.getOtherCategoryName() != null && !dto.getOtherCategoryName().isBlank()) {
	        event.setCustomCategory(dto.getOtherCategoryName().trim());
	        event.setCategory(null);
	    } else {
	        Category category = categoryRepository.findById(dto.getCategoryId())
	            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
	        event.setCategory(category);
	        event.setCustomCategory(null);
	    }

	    String imagePath = eventImageStorageService.storeEventImage(dto.getEventImage());

	    event.setOrganizer(organizer);
	    event.setTitle(dto.getEventName());
	    event.setDescription(dto.getEventDescription());
	    event.setStartDateTime(dto.getStartDateTime());
	    event.setEndDateTime(dto.getEndDateTime());
	    event.setLocation(dto.getLocation());
	    event.setTotalTickets(dto.getTotalTickets());
	    event.setPrice(dto.getTicketPrice());
	    event.setCoverImagePath(imagePath);
	    event.setOrganizerName(dto.getOrganizerName());
	    event.setOrganizerContact(dto.getOrganizerContact());
	    event.setEventWebsite(dto.getEventWebsite());
	    event.setFeatured(dto.isFeatured());
	    event.setRegistrationRequired(dto.isRegistrationRequired());
	    event.setCreatedAt(LocalDateTime.now());

	    Event savedEvent = eventRepository.save(event);
	    return mapper.toDto(savedEvent);
	}

	 @Override
	 public List<EventResponseDTO> getEventsByOrganizer(User organizer) {
	    List<Event> events = eventRepository.findByOrganizerUserIdOrderByStartDateTimeDesc(organizer.getUserId());
	    return events.stream()
	            .map(mapper::toDto)
	            .collect(Collectors.toList());
	}
	 
	@Override
    public EventResponseDTO updateEventFromForm(Integer eventId, EventFormDTO dto) {
        // Find the existing event from the database
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (dto.getCategoryId() != null && dto.getCategoryId() == -1 && dto.getOtherCategoryName() != null && !dto.getOtherCategoryName().isBlank()) {
            event.setCustomCategory(dto.getOtherCategoryName().trim());
            event.setCategory(null);
        } else {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            event.setCategory(category);
            event.setCustomCategory(null);
        }
        
        if (dto.getEventImage() != null && !dto.getEventImage().isEmpty()) {
            String imagePath = eventImageStorageService.storeEventImage(dto.getEventImage());
            event.setCoverImagePath(imagePath);
        }
        
        event.setTitle(dto.getEventName());
        event.setDescription(dto.getEventDescription());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());
        event.setLocation(dto.getLocation());
        event.setTotalTickets(dto.getTotalTickets());
        event.setPrice(dto.getTicketPrice());
        event.setOrganizerName(dto.getOrganizerName());
        event.setOrganizerContact(dto.getOrganizerContact());
        event.setEventWebsite(dto.getEventWebsite());
        event.setFeatured(dto.isFeatured());
        event.setRegistrationRequired(dto.isRegistrationRequired());

        Event updatedEvent = eventRepository.save(event);
        return mapper.toDto(updatedEvent);
    }
	    @Override
	    public List<EventResponseDTO> searchEvents(String query) {
	    	
	        if (query == null || query.trim().isEmpty()) {
	            return getAll();
	        }
	        
	        return eventRepository.findByTitleContainingIgnoreCase(query)
	                .stream()
	                .map(mapper::toDto)
	                .collect(Collectors.toList());
	    }
}