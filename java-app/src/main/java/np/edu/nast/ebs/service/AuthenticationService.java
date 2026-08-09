package np.edu.nast.ebs.service;

import np.edu.nast.ebs.dto.request.AuthRequest;
import np.edu.nast.ebs.dto.request.OtpVerificationRequest;
import np.edu.nast.ebs.dto.request.PasswordResetRequest;
import np.edu.nast.ebs.dto.response.AuthResponse;
import np.edu.nast.ebs.dto.request.RefreshTokenRequest;

import np.edu.nast.ebs.model.User;

public interface AuthenticationService {

    User signup(AuthRequest request);

    AuthResponse signin(AuthRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
    
    void verifyOtp(OtpVerificationRequest request);
    
    void resendOtp(String email);
    
    void initiatePasswordReset(String email);
    
    void finalizePasswordReset(PasswordResetRequest request);
}