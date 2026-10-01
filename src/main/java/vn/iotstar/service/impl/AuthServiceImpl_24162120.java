package vn.iotstar.service.impl;

import java.time.LocalDateTime;

import vn.iotstar.config.OtpPurpose_24162120;
import vn.iotstar.config.PasswordUtils_24162120;
import vn.iotstar.dao.IOtpVerificationDao_24162120;
import vn.iotstar.dao.IUserAccountDao_24162120;
import vn.iotstar.dao.impl.OtpVerificationDao_24162120;
import vn.iotstar.dao.impl.UserAccountDao_24162120;
import vn.iotstar.entity.OtpVerification_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IAuthService_24162120;
import vn.iotstar.util.OtpGenerator_24162120;

public class AuthServiceImpl_24162120 implements IAuthService_24162120 {
    private final IUserAccountDao_24162120 userAccountDao = new UserAccountDao_24162120();
    private final IOtpVerificationDao_24162120 otpVerificationDao = new OtpVerificationDao_24162120();
    private final MailService_24162120 mailService = new MailService_24162120();

    @Override
    public String register(String fullName, String username, String email, String password, String confirmPassword) {
        validateRegisterInput(fullName, username, email, password, confirmPassword);
        ensureUniqueUser(username, email);

        UserAccount_24162120 user = new UserAccount_24162120();
        user.setFullName(fullName.trim());
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(PasswordUtils_24162120.encode(password));
        user.setRoleName("USER");
        user.setEnabled(false);
        user.setStatus(1);
        userAccountDao.insert(user);

        return createAndSendOtp(user, OtpPurpose_24162120.REGISTER);
    }

    @Override
    public UserAccount_24162120 login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Please enter both username/email and password.");
        }
        UserAccount_24162120 user = userAccountDao.findByUsernameOrEmail(usernameOrEmail.trim().toLowerCase());
        if (user == null || !PasswordUtils_24162120.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("The account or password is incorrect.");
        }
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("This account has not been activated by OTP yet.");
        }
        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("This account is currently disabled.");
        }
        return user;
    }

    @Override
    public void verifyRegistrationOtp(String email, String otp) {
        OtpVerification_24162120 otpVerification = validateOtp(email, otp, OtpPurpose_24162120.REGISTER);
        UserAccount_24162120 user = otpVerification.getUser();
        if (user == null) {
            user = userAccountDao.findByEmail(email.trim().toLowerCase());
        }
        if (user == null) {
            throw new IllegalArgumentException("Unable to find the account waiting for activation.");
        }
        user.setEnabled(true);
        userAccountDao.update(user);
        otpVerification.setUsed(true);
        otpVerificationDao.update(otpVerification);
    }

    @Override
    public String resendRegistrationOtp(String email) {
        UserAccount_24162120 user = userAccountDao.findByEmail(normalizeEmail(email));
        if (user == null) {
            throw new IllegalArgumentException("This email has not been registered.");
        }
        if (user.isEnabled()) {
            throw new IllegalArgumentException("This account is already activated.");
        }
        return createAndSendOtp(user, OtpPurpose_24162120.REGISTER);
    }

    @Override
    public String sendResetPasswordOtp(String email) {
        UserAccount_24162120 user = userAccountDao.findByEmail(normalizeEmail(email));
        if (user == null) {
            throw new IllegalArgumentException("No account was found with this email address.");
        }
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("This account has not been activated yet.");
        }
        return createAndSendOtp(user, OtpPurpose_24162120.RESET_PASSWORD);
    }

    @Override
    public void verifyResetPasswordOtp(String email, String otp) {
        OtpVerification_24162120 otpVerification = validateOtp(email, otp, OtpPurpose_24162120.RESET_PASSWORD);
        otpVerification.setUsed(true);
        otpVerificationDao.update(otpVerification);
    }

    @Override
    public void resetPassword(String email, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("The new password must contain at least 6 characters.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }
        UserAccount_24162120 user = userAccountDao.findByEmail(normalizeEmail(email));
        if (user == null) {
            throw new IllegalArgumentException("Unable to find the requested account.");
        }
        user.setPasswordHash(PasswordUtils_24162120.encode(newPassword));
        userAccountDao.update(user);
    }

    private void validateRegisterInput(String fullName, String username, String email, String password, String confirmPassword) {
        if (fullName == null || fullName.isBlank() || username == null || username.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Please fill in full name, username, and email.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }
    }

    private void ensureUniqueUser(String username, String email) {
        if (userAccountDao.findByUsername(username.trim()) != null) {
            throw new IllegalArgumentException("This username already exists.");
        }
        if (userAccountDao.findByEmail(email.trim().toLowerCase()) != null) {
            throw new IllegalArgumentException("This email address is already in use.");
        }
    }

    private String createAndSendOtp(UserAccount_24162120 user, String purpose) {
        otpVerificationDao.markAllUnusedAsUsed(user.getEmail(), purpose);

        String otp = OtpGenerator_24162120.generate6Digits();
        OtpVerification_24162120 otpVerification = new OtpVerification_24162120();
        otpVerification.setUser(user);
        otpVerification.setEmail(user.getEmail());
        otpVerification.setOtpCode(otp);
        otpVerification.setPurpose(purpose);
        otpVerification.setExpiryAt(LocalDateTime.now().plusMinutes(5));
        otpVerification.setUsed(false);
        otpVerificationDao.insert(otpVerification);

        return mailService.sendOtp(user.getEmail(), user.getFullName(), otp, purpose);
    }

    private OtpVerification_24162120 validateOtp(String email, String otp, String purpose) {
        String normalizedEmail = normalizeEmail(email);
        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException("Please enter the OTP code.");
        }
        OtpVerification_24162120 otpVerification = otpVerificationDao.findLatest(normalizedEmail, purpose);
        if (otpVerification == null) {
            throw new IllegalArgumentException("No matching OTP could be found.");
        }
        if (otpVerification.isUsed()) {
            throw new IllegalArgumentException("This OTP has already been used.");
        }
        if (otpVerification.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This OTP has expired.");
        }
        if (!otpVerification.getOtpCode().equals(otp.trim())) {
            throw new IllegalArgumentException("The OTP code is incorrect.");
        }
        return otpVerification;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty.");
        }
        return email.trim().toLowerCase();
    }
}
