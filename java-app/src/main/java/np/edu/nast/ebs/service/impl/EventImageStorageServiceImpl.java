package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.exception.FileStorageException;
import np.edu.nast.ebs.service.EventImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class EventImageStorageServiceImpl implements EventImageStorageService {

    private final Path storageLocation;

    public EventImageStorageServiceImpl() {
        String userHome = System.getProperty("user.home");
        this.storageLocation = Paths.get(userHome, "ebs_uploads", "event_images");
        System.out.println("Event Image storage location: " + this.storageLocation.toAbsolutePath());
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create event image storage directory.", e);
        }
    }

    @Override
    public String storeEventImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence: " + originalFilename);
            }
            
            String extension = "";
            int lastDot = originalFilename.lastIndexOf('.');
            if (lastDot >= 0) {
                extension = originalFilename.substring(lastDot);
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.storageLocation.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/event-images/" + uniqueFilename;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store event image.", e);
        }
    }
}