package np.edu.nast.ebs.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        logger.info("User '{}' authenticated successfully. Determining role for redirection.", username);
        
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // Save the full User object to the session for easy access in controllers
        HttpSession session = request.getSession();
        if (session != null) {
            Optional<User> userOptional = userRepository.findByEmail(username);
            userOptional.ifPresent(user -> session.setAttribute("loggedInUser", user));
        }

        handleRedirect(request, response, role);
    }

    protected void handleRedirect(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String authority) throws IOException {

        String targetUrl = determineTargetUrl(authority);
        logger.info("Redirecting user to target URL: {}", targetUrl);

        if (response.isCommitted()) {
            logger.warn("Response has already been committed. Unable to redirect to {}", targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(String authority) {
        // The authority from Spring Security includes the "ROLE_" prefix.
        if ("ROLE_ADMIN".equals(authority)) {
            return "/admin/dashboard";
        } else if ("ROLE_ORGANIZER".equals(authority)) {
            return "/organizer/dashboard";
        } else {
            logger.warn("User with role '{}' has no specific dashboard. Logging out.", authority);
            return "/login?unsupportedRole=true";
        }
    }
}