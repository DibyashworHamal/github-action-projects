package np.edu.nast.ebs.controller.web;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/files")
public class FileController {

    private final Path uploadRoot;

    public FileController() {
        String userHome = System.getProperty("user.home");
        this.uploadRoot = Paths.get(userHome, "ebs_uploads", "organizer_docs");
    }

    @GetMapping("/organizer_docs/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = uploadRoot.resolve(filename).normalize();
            
            if (!Files.exists(file) || !Files.isReadable(file)) {
                 System.err.println("File not found or not readable: " + file.toString());
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toUri());

            // Determine the content type of the file
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                // Set a default if the type cannot be determined
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    // Add the Content-Type header
                    .contentType(MediaType.parseMediaType(contentType)) 
                    // The inline disposition tells the browser to try and display it, not download it
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL exception for file: " + filename);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) { 
             System.err.println("Could not determine file type for: " + filename);
            return ResponseEntity.internalServerError().build();
        }
    }
}