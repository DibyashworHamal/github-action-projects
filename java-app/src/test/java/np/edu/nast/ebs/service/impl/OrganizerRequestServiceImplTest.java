package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.OrganizerRequestDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.OrganizerRequestMapper;
import np.edu.nast.ebs.model.OrganizerRequest;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.OrganizerRequestRepository;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrganizerRequestServiceImplTest {

    private OrganizerRequestRepository requestRepository;
    private UserRepository userRepository;
    private OrganizerRequestMapper mapper;
    private FileStorageService fileStorageService;
    private OrganizerRequestServiceImpl service;

    @BeforeEach
    void setUp() {
        requestRepository = mock(OrganizerRequestRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = mock(OrganizerRequestMapper.class);
        fileStorageService = mock(FileStorageService.class);
        service = new OrganizerRequestServiceImpl(requestRepository, userRepository, mapper, fileStorageService);
    }

    @Test
    void testCreateRequest_Success() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
        OrganizerRequestDTO dto = new OrganizerRequestDTO();
        dto.setUserId(1);
        dto.setDocument(mockFile); 

        User user = new User();
        user.setUserId(1);
        user.setRole(User.Role.CUSTOMER);

        OrganizerRequest request = new OrganizerRequest();
        OrganizerRequest savedRequest = new OrganizerRequest();
        savedRequest.setRequestId(1);
        savedRequest.setUser(user);

        OrganizerRequestResponseDTO responseDTO = new OrganizerRequestResponseDTO();
        responseDTO.setRequestId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.existsByUserAndApprovalStatus(user, OrganizerRequest.ApprovalStatus.PENDING)).thenReturn(false);
        when(fileStorageService.store(any())).thenReturn("uploads/organizer_docs/some-file.pdf");
        when(mapper.toEntity(dto)).thenReturn(request);
        when(requestRepository.save(any(OrganizerRequest.class))).thenReturn(savedRequest);
        when(mapper.toDto(savedRequest)).thenReturn(responseDTO);

        OrganizerRequestResponseDTO result = service.createRequest(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRequestId());
        verify(fileStorageService).store(mockFile);
        verify(requestRepository).save(any(OrganizerRequest.class));
    }

    @Test
    void testCreateRequest_UserNotFound() {
        OrganizerRequestDTO dto = new OrganizerRequestDTO();
        dto.setUserId(99);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createRequest(dto));
    }

  
    @Test
    void testApproveRequest_Success() {
        // Arrange
        Integer requestId = 1;
        
        // The user who made the request
        User requestingUser = new User();
        requestingUser.setUserId(1);
        requestingUser.setRole(User.Role.CUSTOMER);

        // The admin who is approving the request
        User adminUser = new User();
        adminUser.setUserId(99);
        adminUser.setFullName("Admin Approver");
        adminUser.setRole(User.Role.ADMIN);

        OrganizerRequest request = new OrganizerRequest();
        request.setRequestId(requestId);
        request.setApprovalStatus(OrganizerRequest.ApprovalStatus.PENDING);
        request.setUser(requestingUser);

        OrganizerRequestResponseDTO responseDTO = new OrganizerRequestResponseDTO();
        responseDTO.setRequestId(requestId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(OrganizerRequest.class))).thenReturn(request);
        when(userRepository.save(any(User.class))).thenReturn(requestingUser);
        when(mapper.toDto(request)).thenReturn(responseDTO);

        // Act
        // --- Call the method with the new signature ---
        OrganizerRequestResponseDTO result = service.approveRequest(requestId, adminUser);

        // Assert
        assertNotNull(result);
        assertEquals(requestId, result.getRequestId());
        assertEquals(OrganizerRequest.ApprovalStatus.APPROVED, request.getApprovalStatus());
        assertEquals(User.Role.ORGANIZER, requestingUser.getRole());
        assertEquals(adminUser, request.getProcessedBy()); // Verify the admin was set

        verify(requestRepository).save(request);
        verify(userRepository).save(requestingUser);
    }

    @Test
    void testApproveRequest_NotFound() {
        when(requestRepository.findById(99)).thenReturn(Optional.empty());
        // The adminUser can be null here because the service will throw an exception before using it
        assertThrows(ResourceNotFoundException.class, () -> service.approveRequest(99, null));
    }

    @Test
    void testRejectRequest_Success() {
        // Arrange
        Integer requestId = 2;
        String comments = "Documentation unclear.";

        User requestingUser = new User();
        requestingUser.setUserId(2);

        User adminUser = new User();
        adminUser.setUserId(99);
        adminUser.setFullName("Admin Reviewer");

        OrganizerRequest request = new OrganizerRequest();
        request.setRequestId(requestId);
        request.setApprovalStatus(OrganizerRequest.ApprovalStatus.PENDING);
        request.setUser(requestingUser);
        
        OrganizerRequestResponseDTO responseDTO = new OrganizerRequestResponseDTO();
        responseDTO.setRequestId(requestId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(OrganizerRequest.class))).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(responseDTO);

        // Act
        OrganizerRequestResponseDTO result = service.rejectRequest(requestId, comments, adminUser);

        // Assert
        assertNotNull(result);
        assertEquals(requestId, result.getRequestId());
        assertEquals(OrganizerRequest.ApprovalStatus.REJECTED, request.getApprovalStatus());
        assertEquals(comments, request.getAdminComments());
        assertEquals(adminUser, request.getProcessedBy());

        verify(requestRepository).save(request);
        // We verify that userRepository.save() is NOT called during a rejection
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllRequests() {
        // Arrange
        OrganizerRequest r1 = new OrganizerRequest();
        OrganizerRequest r2 = new OrganizerRequest();
        OrganizerRequestResponseDTO dto1 = new OrganizerRequestResponseDTO();
        OrganizerRequestResponseDTO dto2 = new OrganizerRequestResponseDTO();

        when(requestRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
        when(mapper.toDto(r1)).thenReturn(dto1);
        when(mapper.toDto(r2)).thenReturn(dto2);

        // Act
        List<OrganizerRequestResponseDTO> result = service.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(requestRepository).findAll();
    }
}