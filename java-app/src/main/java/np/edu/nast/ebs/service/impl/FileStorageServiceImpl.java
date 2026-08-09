package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;

    public FileStorageServiceImpl() {
        // Create a path based on the user's home directory. This is stable.
        // It will resolve to something like: C:\Users\YourUser\ebs_uploads\organizer_docs
        String userHome = System.getProperty("user.home");
        this.rootLocation = Paths.get(userHome, "ebs_uploads", "organizer_docs");
                
        System.out.println("File storage location: " + this.rootLocation.toAbsolutePath().toString());

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location: " + rootLocation.toString(), e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Failed to store empty file or file with no name.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(uniqueFilename)
                                                     .normalize().toAbsolutePath();
            
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new SecurityException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}