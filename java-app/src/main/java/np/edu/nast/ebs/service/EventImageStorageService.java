package np.edu.nast.ebs.service;

import org.springframework.web.multipart.MultipartFile;

public interface EventImageStorageService {
  
    String storeEventImage(MultipartFile file);
}