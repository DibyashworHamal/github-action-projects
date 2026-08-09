package np.edu.nast.ebs.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
   
    String store(MultipartFile file);
}