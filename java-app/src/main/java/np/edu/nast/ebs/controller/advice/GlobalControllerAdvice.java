package np.edu.nast.ebs.controller.advice;

import np.edu.nast.ebs.security.util.SecurityUtilService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final SecurityUtilService securityUtilService;

    // Inject the service using constructor injection
    public GlobalControllerAdvice(SecurityUtilService securityUtilService) {
        this.securityUtilService = securityUtilService;
    }

    /**
     * This method adds the 'security' utility bean to the model for all controllers.
     * This makes it available for use in any Thymeleaf template.
     */
    @ModelAttribute("security")
    public SecurityUtilService securityUtilService() {
        return this.securityUtilService;
    }
}