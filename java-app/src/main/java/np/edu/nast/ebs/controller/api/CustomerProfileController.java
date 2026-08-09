package np.edu.nast.ebs.controller.api;

import np.edu.nast.ebs.dto.request.ChangePasswordRequest;
import np.edu.nast.ebs.dto.request.UserProfileRequestDTO;
import np.edu.nast.ebs.dto.response.MessageResponse;
import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import np.edu.nast.ebs.dto.response.UserResponseDTO;
import np.edu.nast.ebs.mapper.UserMapper;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/profile")
public class CustomerProfileController {

    private final UserProfileService userProfileService;
    private final UserMapper userMapper; 

    public CustomerProfileController(UserProfileService userProfileService, UserMapper userMapper) {
        this.userProfileService = userProfileService;
        this.userMapper = userMapper;
    }

    /**
     * Gets the full user and profile information for the logged-in customer.
     */
    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Fetch profile, or create a new one if it doesn't exist, to ensure a consistent response
        UserProfileResponseDTO profile = userProfileService.findByUserId(userDetails.getId())
                .orElse(new UserProfileResponseDTO());
        
        // Also include basic user info in the response
        UserResponseDTO userDto = userMapper.toDto(userDetails.getUser());
        profile.setUserId(userDto.getUserId()); // Ensure userId is set

        return ResponseEntity.ok(profile);
    }
    
    /**
     * Updates the text-based settings for the logged-in customer's profile.
     */
    @PutMapping("/settings")
    public ResponseEntity<UserProfileResponseDTO> updateProfileSettings(@RequestBody UserProfileRequestDTO settingsDto,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponseDTO updatedProfile = userProfileService.updateSettings(userDetails.getId(), settingsDto);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Handles the upload of a new profile picture.
     */
    @PostMapping("/picture")
    public ResponseEntity<MessageResponse> uploadProfilePicture(@RequestParam("photo") MultipartFile photo,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userProfileService.updateProfilePicture(userDetails.getId(), photo);
            return ResponseEntity.ok(new MessageResponse("Profile picture updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error uploading photo: " + e.getMessage()));
        }
    }
    //endpoint for change password
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userProfileService.changePassword(userDetails.getId(), request);
            return ResponseEntity.ok(new MessageResponse("Password changed successfully. Please log in again."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}