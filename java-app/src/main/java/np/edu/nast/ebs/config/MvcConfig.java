package np.edu.nast.ebs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("ebs_uploads/organizer_docs", "/uploads/organizer-docs", registry);
        exposeDirectory("ebs_uploads/event_images", "/event-images", registry);
    }

    private void exposeDirectory(String dirName, String pathPattern, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(System.getProperty("user.home"), dirName);

        String resourceLocation = uploadDir.toUri().toString();
        
        System.out.println("Mapping URL pattern '" + pathPattern + "/**' to resource location '" + resourceLocation + "'");

        registry.addResourceHandler(pathPattern + "/**")
                .addResourceLocations(resourceLocation);
    }
}