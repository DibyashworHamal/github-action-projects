package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.OrganizerRequestDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrganizerRequestService {

    OrganizerRequestResponseDTO createRequest(OrganizerRequestDTO dto);

    OrganizerRequestResponseDTO approveRequest(Integer requestId, User adminUser);
    
    OrganizerRequestResponseDTO rejectRequest(Integer requestId, String comments, User adminUser);

    List<OrganizerRequestResponseDTO> getAll();

    boolean hasPendingOrApprovedRequest(Integer userId);
    
    boolean hasPaidRequest(Integer userId);

    void processPaymentForRequest(Integer requestId, Integer userId, BigDecimal amount);
    
    Optional<OrganizerRequestResponseDTO> findLatestRequestByUserId(Integer userId);
    
    List<OrganizerRequestResponseDTO> findPendingRequests();
    
    Optional<OrganizerRequestResponseDTO> findRequestById(Integer requestId);
}