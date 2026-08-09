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
import np.edu.nast.ebs.service.OrganizerRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import np.edu.nast.ebs.model.OrganizerRequest.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizerRequestServiceImpl implements OrganizerRequestService {

    private final OrganizerRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final OrganizerRequestMapper mapper;
    private final FileStorageService fileStorageService;

    public OrganizerRequestServiceImpl(OrganizerRequestRepository requestRepository,
                                       UserRepository userRepository,
                                       OrganizerRequestMapper mapper,
                                       FileStorageService fileStorageService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public OrganizerRequestResponseDTO createRequest(OrganizerRequestDTO dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("Cannot create an organizer request with a null User ID.");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
        if (requestRepository.existsByUserAndApprovalStatus(user, OrganizerRequest.ApprovalStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending organizer request.");
        }
        String documentPath = fileStorageService.store(dto.getDocument());
        OrganizerRequest request = mapper.toEntity(dto);
        request.setUser(user);
        request.setDocumentPath(documentPath);
        OrganizerRequest savedRequest = requestRepository.save(request);
        return mapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public OrganizerRequestResponseDTO approveRequest(Integer requestId, User adminUser) {
        OrganizerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer request not found with id: " + requestId));
        if (request.getApprovalStatus() != OrganizerRequest.ApprovalStatus.PENDING) {
            throw new IllegalStateException("This request has already been processed.");
        }
        request.setApprovalStatus(OrganizerRequest.ApprovalStatus.APPROVED);
        request.setPaymentStatus(OrganizerRequest.PaymentStatus.PAID);
        request.setProcessedBy(adminUser);
        request.setApprovedAt(LocalDateTime.now());
        User user = request.getUser();
        user.setRole(User.Role.ORGANIZER);
        userRepository.save(user);
        OrganizerRequest updatedRequest = requestRepository.save(request);
        return mapper.toDto(updatedRequest);
    }

    @Override
    @Transactional
    public OrganizerRequestResponseDTO rejectRequest(Integer requestId, String comments, User adminUser) {
        OrganizerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer request not found with id: " + requestId));

        if (request.getApprovalStatus() != OrganizerRequest.ApprovalStatus.PENDING) {
            throw new IllegalStateException("This request has already been processed.");
        }

        request.setApprovalStatus(OrganizerRequest.ApprovalStatus.REJECTED);
        request.setApprovedAt(LocalDateTime.now()); // Sets the processing date
        request.setProcessedBy(adminUser);
        
        // Save the admin's comments to the entity
        request.setAdminComments(comments);

        OrganizerRequest updatedRequest = requestRepository.save(request);
        return mapper.toDto(updatedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizerRequestResponseDTO> getAll() {
        return requestRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingOrApprovedRequest(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return requestRepository.existsByUserAndApprovalStatusIn(user,
                List.of(OrganizerRequest.ApprovalStatus.PENDING, OrganizerRequest.ApprovalStatus.APPROVED));
    }

    @Override
    @Transactional
    public void processPaymentForRequest(Integer requestId, Integer userId, BigDecimal amount) {
        OrganizerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer request not found with id: " + requestId));
        if (!request.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You are not authorized to process payment for this request.");
        }
        request.setPaymentStatus(OrganizerRequest.PaymentStatus.PAID);
        request.setPaidAt(LocalDateTime.now());
        request.setPaymentAmount(amount);
        requestRepository.save(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizerRequestResponseDTO> findLatestRequestByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return requestRepository.findTopByUserOrderByRequestedAtDesc(user)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizerRequestResponseDTO> findPendingRequests() {
        List<OrganizerRequest> pendingRequests = 
            requestRepository.findByApprovalStatus(OrganizerRequest.ApprovalStatus.PENDING);
        
        return pendingRequests.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizerRequestResponseDTO> findRequestById(Integer requestId) {
        return requestRepository.findById(requestId)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPaidRequest(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if any of the user's previous requests have a PAID status
        return requestRepository.existsByUserAndPaymentStatus(user, PaymentStatus.PAID);
    }

}