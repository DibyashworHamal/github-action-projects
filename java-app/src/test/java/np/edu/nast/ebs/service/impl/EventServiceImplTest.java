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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventServiceImplTest {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private EventMapper eventMapper;
    private EventServiceImpl eventService;
    private EventImageStorageService eventImageStorageService; 

    @BeforeEach
    void setup() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        eventMapper = mock(EventMapper.class);
        eventImageStorageService = mock(EventImageStorageService.class);
        eventService = new EventServiceImpl(eventRepository, userRepository, categoryRepository, eventMapper, eventImageStorageService);
    }

    @Test
    void testGetAllEvents() {
        Event event1 = new Event();
        event1.setEventId(1);
        event1.setTitle("Expo");

        Event event2 = new Event();
        event2.setEventId(2);
        event2.setTitle("Wedding");

        EventResponseDTO dto1 = new EventResponseDTO();
        dto1.setEventId(1);
        dto1.setTitle("Expo");

        EventResponseDTO dto2 = new EventResponseDTO();
        dto2.setEventId(2);
        dto2.setTitle("Wedding");

        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));
        when(eventMapper.toDto(event1)).thenReturn(dto1);
        when(eventMapper.toDto(event2)).thenReturn(dto2);

        List<EventResponseDTO> result = eventService.getAll();

        assertEquals(2, result.size());
        assertEquals("Expo", result.get(0).getTitle());
        verify(eventRepository).findAll();
    }

    @Test
    void testGetEventByIdFound() {
        Event event = new Event();
        event.setEventId(1);
        event.setTitle("Birthday");

        EventResponseDTO dto = new EventResponseDTO();
        dto.setEventId(1);
        dto.setTitle("Birthday");

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(dto);

        EventResponseDTO result = eventService.getById(1);

        assertNotNull(result);
        assertEquals("Birthday", result.getTitle());
        verify(eventRepository).findById(1);
    }

    @Test
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getById(999);
        });

        verify(eventRepository).findById(999);
    }

    @Test
    void testCreateEvent() {
        EventRequestDTO requestDTO = new EventRequestDTO();
        requestDTO.setTitle("Tech Meetup");
        requestDTO.setOrganizerId(1);
        requestDTO.setCategoryId(2);
        requestDTO.setStartDateTime(LocalDateTime.now().plusDays(1));

        User organizer = new User();
        organizer.setUserId(1);

        Category category = new Category();
        category.setCategoryId(2);

        Event event = new Event();
        event.setTitle("Tech Meetup");

        Event savedEvent = new Event();
        savedEvent.setEventId(10);
        savedEvent.setTitle("Tech Meetup");

        EventResponseDTO responseDTO = new EventResponseDTO();
        responseDTO.setEventId(10);
        responseDTO.setTitle("Tech Meetup");

        when(userRepository.findById(1)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(2)).thenReturn(Optional.of(category));
        when(eventMapper.toEntity(requestDTO)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(responseDTO);

        EventResponseDTO result = eventService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Tech Meetup", result.getTitle());
    }
    
    @Test
    void testCreateEventFromForm_WithStandardCategory() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
            "eventImage",         
            "hello.png",      
            "image/png",          
            "some-image-bytes".getBytes()
        );
        EventFormDTO formDTO = new EventFormDTO();
        formDTO.setEventName("Summer Music Festival");
        formDTO.setCategoryId(1);
        formDTO.setEventImage(mockFile);
        formDTO.setTicketPrice(new BigDecimal("500.00"));
        formDTO.setStartDateTime(LocalDateTime.now().plusDays(10));
        formDTO.setEndDateTime(LocalDateTime.now().plusDays(11));
     
        User organizer = new User();
        organizer.setUserId(42);
        
        Category mockCategory = new Category();
        mockCategory.setCategoryId(1);
        mockCategory.setName("Music");
        
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(eventImageStorageService.storeEventImage(any(MultipartFile.class))).thenReturn("/event-images/unique-file-name.png");
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0)); 
        when(eventMapper.toDto(any(Event.class))).thenReturn(new EventResponseDTO()); 
        
        // Act
        EventResponseDTO result = eventService.createEventFromForm(formDTO, organizer);

        // Assert
        assertNotNull(result); 
        verify(categoryRepository, times(1)).findById(1); 
        verify(eventImageStorageService, times(1)).storeEventImage(mockFile); 
        verify(eventRepository, times(1)).save(any(Event.class)); 
    }
    
    @Test
    void testCreateEventFromForm_WithOtherCategory() {
        // Arrange
        EventFormDTO formDTO = new EventFormDTO();
        formDTO.setEventName("Charity Gala");
        formDTO.setCategoryId(-1);
        formDTO.setOtherCategoryName("Gala Event"); 
        
        User organizer = new User();
        organizer.setUserId(42);

        when(eventImageStorageService.storeEventImage(any())).thenReturn("/event-images/gala.jpg");
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventMapper.toDto(any(Event.class))).thenReturn(new EventResponseDTO());

        // Act
        eventService.createEventFromForm(formDTO, organizer);

        // Assert
        verify(categoryRepository, never()).findById(anyInt());
        verify(eventRepository).save(argThat(event -> 
            event.getCustomCategory().equals("Gala Event") && event.getCategory() == null
        ));
    }
    
	public EventImageStorageService getEventImageStorageService() {
		return eventImageStorageService;
	}

	public void setEventImageStorageService(EventImageStorageService eventImageStorageService) {
		this.eventImageStorageService = eventImageStorageService;
	}
}