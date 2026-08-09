package np.edu.nast.ebs.service.impl;

import np.edu.nast.ebs.dto.request.ChangePasswordRequest;
import np.edu.nast.ebs.dto.request.UserProfileRequestDTO;
import np.edu.nast.ebs.dto.response.UserProfileResponseDTO;
import np.edu.nast.ebs.exception.ResourceNotFoundException;
import np.edu.nast.ebs.mapper.UserProfileMapper;
import np.edu.nast.ebs.model.User;
import np.edu.nast.ebs.model.UserProfile;
import np.edu.nast.ebs.model.UserProfile.Theme;
import np.edu.nast.ebs.repository.UserProfileRepository;
import np.edu.nast.ebs.repository.UserRepository;
import np.edu.nast.ebs.service.FileStorageService;
import np.edu.nast.ebs.service.UserProfileService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final UserProfileMapper mapper;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder; 

    public UserProfileServiceImpl(UserProfileRepository profileRepository,
                                  UserRepository userRepository,
                                  UserProfileMapper mapper,
                                  FileStorageService fileStorageService,
                                  PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.fileStorageService = fileStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserProfileResponseDTO create(UserProfileRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserProfile profile = mapper.toEntity(dto);
        profile.setUser(user);
        return mapper.toDto(profileRepository.save(profile));
    }

    @Override
    public UserProfileResponseDTO getById(Integer id) {
        return profileRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
    }

    @Override
    public List<UserProfileResponseDTO> getAll() {
        return profileRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (!profileRepository.existsById(id))
            throw new ResourceNotFoundException("Profile not found with id: " + id);
        profileRepository.deleteById(id);
    }
    
    @Override
    public Optional<UserProfileResponseDTO> findByUserId(Integer userId) {
        return profileRepository.findByUser_UserId(userId)
                .map(mapper::toDto);
    }

	public FileStorageService getFileStorageService() {
		return fileStorageService;
	}
	
	@Override
	@Transactional
	public UserProfileResponseDTO updateSettings(Integer userId, UserProfileRequestDTO dto) {
	    UserProfile profile = profileRepository.findByUser_UserId(userId)
	        .orElseGet(() -> {
	            User user = userRepository.findById(userId).orElseThrow();
	            UserProfile newProfile = new UserProfile();
	            newProfile.setUser(user);
	            return newProfile;
	        });

	    profile.setBio(dto.getBio());
	    profile.setTheme(dto.getTheme() != null ? Theme.valueOf(dto.getTheme().toUpperCase()) : Theme.LIGHT);
	    profile.setNotificationEmail(dto.getNotificationEmail() != null && dto.getNotificationEmail());
	    profile.setUpdatedAt(LocalDateTime.now());
	    
	    return mapper.toDto(profileRepository.save(profile));
	}

	@Override
	@Transactional
	public void updateProfilePicture(Integer userId, MultipartFile photo) {
	    if (photo.isEmpty()) {
	        throw new IllegalArgumentException("Cannot update profile with an empty photo.");
	    }
	    
	    String fileName = fileStorageService.store(photo);
	    String photoUrl = "/files/organizer_docs/" + fileName;

	    UserProfile profile = profileRepository.findByUser_UserId(userId)
	        .orElseGet(() -> {
	            User user = userRepository.findById(userId).orElseThrow();
	            UserProfile newProfile = new UserProfile();
	            newProfile.setUser(user);
	            return newProfile;
	        });
	    
	    profile.setProfilePicture(photoUrl);
	    profile.setUpdatedAt(LocalDateTime.now());
	    profileRepository.save(profile);
	}
	
	 @Override
	    @Transactional
	    public void changePassword(Integer userId, ChangePasswordRequest request) {
	        // 1. Find the user
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	        // 2. Check if the old password matches
	        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
	            throw new IllegalArgumentException("Incorrect old password.");
	        }

	        // 3. Check if the new password and confirm password match
	        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
	            throw new IllegalArgumentException("New password and confirmation do not match.");
	        }

	        // 4. Encode and save the new password
	        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
	        userRepository.save(user);
	    }
}
