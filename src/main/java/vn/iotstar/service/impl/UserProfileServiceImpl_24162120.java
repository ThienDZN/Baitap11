package vn.iotstar.service.impl;

import vn.iotstar.dao.IUserAccountDao_24162120;
import vn.iotstar.dao.impl.UserAccountDao_24162120;
import vn.iotstar.dto.UserProfileUpdateRequest_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IUserProfileService_24162120;

public class UserProfileServiceImpl_24162120 implements IUserProfileService_24162120 {
    private final IUserAccountDao_24162120 userAccountDao = new UserAccountDao_24162120();

    @Override
    public UserAccount_24162120 findById(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Please log in again to access your profile.");
        }
        UserAccount_24162120 user = userAccountDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Unable to find your account.");
        }
        return user;
    }

    @Override
    public UserAccount_24162120 updateProfile(Long userId, UserProfileUpdateRequest_24162120 request) {
        UserAccount_24162120 user = findById(userId);
        UserProfileUpdateRequest_24162120 normalized = normalize(request);

        user.setFullName(normalized.getFullName());
        user.setPhone(normalized.getPhone());
        user.setImages(normalized.getImage());
        userAccountDao.update(user);

        return findById(userId);
    }

    private UserProfileUpdateRequest_24162120 normalize(UserProfileUpdateRequest_24162120 request) {
        if (request == null) {
            throw new IllegalArgumentException("Profile data must not be empty.");
        }

        UserProfileUpdateRequest_24162120 normalized = new UserProfileUpdateRequest_24162120();
        normalized.setFullName(normalizeRequired(request.getFullName(), "Full name must not be empty.", 120));
        normalized.setPhone(normalizeOptional(request.getPhone(), 20, "Phone number must not exceed 20 characters."));
        normalized.setImage(normalizeOptional(request.getImage(), 500, "Image path must not exceed 500 characters."));
        return normalized;
    }

    private String normalizeRequired(String value, String emptyMessage, int maxLength) {
        String normalized = normalizeOptional(value, maxLength, "This field is too long.");
        if (normalized == null) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return normalized;
    }

    private String normalizeOptional(String value, int maxLength, String tooLongMessage) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(tooLongMessage);
        }
        return trimmed;
    }
}
