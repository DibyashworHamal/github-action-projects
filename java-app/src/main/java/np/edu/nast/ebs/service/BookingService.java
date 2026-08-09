package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.BookingRequestDTO;
import np.edu.nast.ebs.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO create(BookingRequestDTO dto);
    BookingResponseDTO getById(Integer id);
    List<BookingResponseDTO> getAll();
    List<BookingResponseDTO> getBookingsForOrganizer(Integer organizerId);
    List<BookingResponseDTO> getBookingsForCustomer(Integer customerId);
}
