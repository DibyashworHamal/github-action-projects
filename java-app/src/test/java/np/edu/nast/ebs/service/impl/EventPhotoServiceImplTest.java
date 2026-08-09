package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.EventPhotoRequestDTO;
import np.edu.nast.ebs.dto.response.EventPhotoResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.EventPhotoMapper;
import np.edu.nast.ebs.model.Event;
import np.edu.nast.ebs.model.EventPhoto;
import np.edu.nast.ebs.repository.EventPhotoRepository;
import np.edu.nast.ebs.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventPhotoServiceImplTest {

    private EventRepository eventRepository;
    private EventPhotoRepository photoRepository;
    private EventPhotoMapper mapper;
    private EventPhotoServiceImpl service;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        photoRepository = mock(EventPhotoRepository.class);
        mapper = mock(EventPhotoMapper.class);
        service = new EventPhotoServiceImpl(eventRepository, photoRepository, mapper);
    }

    @Test
    void testAddPhoto_Success() {
        EventPhotoRequestDTO dto = new EventPhotoRequestDTO();
        dto.setEventId(1);
        dto.setPhotoUrl("photo.jpg");

        Event event = new Event();
        event.setEventId(1);

        EventPhoto photo = new EventPhoto();
        photo.setPhotoUrl("photo.jpg");

        EventPhoto savedPhoto = new EventPhoto();
        savedPhoto.setPhotoUrl("photo.jpg");
        savedPhoto.setEvent(event);

        EventPhotoResponseDTO responseDTO = new EventPhotoResponseDTO();
        responseDTO.setPhotoUrl("photo.jpg");

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(mapper.toEntity(dto)).thenReturn(photo);
        when(photoRepository.save(photo)).thenReturn(savedPhoto);
        when(mapper.toDto(savedPhoto)).thenReturn(responseDTO);

        EventPhotoResponseDTO result = service.add(dto);

        assertNotNull(result);
        assertEquals("photo.jpg", result.getPhotoUrl());
        verify(photoRepository).save(photo);
    }

    @Test
    void testAddPhoto_EventNotFound() {
        EventPhotoRequestDTO dto = new EventPhotoRequestDTO();
        dto.setEventId(99);
        dto.setPhotoUrl("photo.jpg");

        when(eventRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.add(dto));
        verify(eventRepository).findById(99);
        verify(photoRepository, never()).save(any());
    }

    @Test
    void testGetByEventId_ReturnsList() {
        Event event = new Event();
        event.setEventId(1);

        EventPhoto photo1 = new EventPhoto();
        photo1.setPhotoUrl("photo1.jpg");
        photo1.setEvent(event);

        EventPhoto photo2 = new EventPhoto();
        photo2.setPhotoUrl("photo2.jpg");
        photo2.setEvent(event);

        EventPhotoResponseDTO dto1 = new EventPhotoResponseDTO();
        dto1.setPhotoUrl("photo1.jpg");

        EventPhotoResponseDTO dto2 = new EventPhotoResponseDTO();
        dto2.setPhotoUrl("photo2.jpg");

        when(photoRepository.findAll()).thenReturn(Arrays.asList(photo1, photo2));
        when(mapper.toDto(photo1)).thenReturn(dto1);
        when(mapper.toDto(photo2)).thenReturn(dto2);

        List<EventPhotoResponseDTO> result = service.getByEventId(1);

        assertEquals(2, result.size());
        assertEquals("photo1.jpg", result.get(0).getPhotoUrl());
        assertEquals("photo2.jpg", result.get(1).getPhotoUrl());

        verify(photoRepository).findAll();
        verify(mapper).toDto(photo1);
        verify(mapper).toDto(photo2);
    }
}
