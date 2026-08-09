package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.FeedbackRequestDTO;
import np.edu.nast.ebs.dto.response.FeedbackResponseDTO;

import java.util.List;

public interface FeedbackService {
    FeedbackResponseDTO giveFeedback(FeedbackRequestDTO dto);
    List<FeedbackResponseDTO> getAll();
}
