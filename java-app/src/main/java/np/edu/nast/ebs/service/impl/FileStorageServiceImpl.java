package np.edu.nast.ebs.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import np.edu.nast.ebs.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final AmazonS3 amazonS3;

    @Value("${AWS_S3_BUCKET_NAME:dip-uploads}")
    private String bucketName;

    @Value("${AWS_S3_PUBLIC_URL:https://s3.dibyashworhamal.com.np}")
    private String publicUrl;

    @Autowired
    public FileStorageServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Failed to store empty file or file with no name.");
        }

        try {
            // Extract file extension (e.g. .pdf, .jpg)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Prepare S3 Object Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // Upload directly to MinIO S3 Bucket
            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(bucketName, uniqueFilename, inputStream, metadata);
            }

            System.out.println("Successfully uploaded file to MinIO: " + uniqueFilename);

            // Return the full public HTTPS URL to store in your MySQL database
            return publicUrl + "/" + bucketName + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file to MinIO object storage.", e);
        }
    }
}