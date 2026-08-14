package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.service.EventImageStorageService;
import np.edu.nast.ebs.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EventImageStorageServiceImpl implements EventImageStorageService {

    private final FileStorageService fileStorageService;

    @Autowired
    public EventImageStorageServiceImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public String storeEventImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Uploads the event image (PNG, JPG, WEBP, etc.) directly to MinIO S3 Storage
        return fileStorageService.store(file);
    }
}