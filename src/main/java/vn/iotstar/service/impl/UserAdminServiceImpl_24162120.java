package vn.iotstar.service.impl;

import java.util.List;

import vn.iotstar.config.PasswordUtils_24162120;
import vn.iotstar.dao.IUserAccountDao_24162120;
import vn.iotstar.dao.impl.UserAccountDao_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IUserAdminService_24162120;
import vn.iotstar.validation.ValidationUtils_24162120;

public class UserAdminServiceImpl_24162120 implements IUserAdminService_24162120 {
    private final IUserAccountDao_24162120 userAccountDao = new UserAccountDao_24162120();

    @Override
    public List<UserAccount_24162120> findAll() {
        return userAccountDao.findAll();
    }

    @Override
    public List<UserAccount_24162120> search(String keyword) {
        String normalizedKeyword = ValidationUtils_24162120.trimToNull(keyword);
        return normalizedKeyword == null ? findAll() : userAccountDao.search(normalizedKeyword);
    }

    @Override
    public UserAccount_24162120 findById(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        return userAccountDao.findById(userId);
    }

    @Override
    public void insert(UserAccount_24162120 user, String rawPassword) {
        UserAccount_24162120 normalized = normalize(user);
        validatePassword(rawPassword, true);
        ensureUnique(normalized, null);
        normalized.setPasswordHash(PasswordUtils_24162120.encode(rawPassword.trim()));
        userAccountDao.insert(normalized);
    }

    @Override
    public UserAccount_24162120 update(UserAccount_24162120 user, String rawPassword) {
        if (user == null || user.getUserId() == null || user.getUserId() <= 0) {
            throw new IllegalArgumentException("The user data is not valid.");
        }

        UserAccount_24162120 existing = userAccountDao.findById(user.getUserId());
        if (existing == null) {
            throw new IllegalArgumentException("The user does not exist.");
        }

        UserAccount_24162120 normalized = normalize(user);
        normalized.setUserId(existing.getUserId());
        normalized.setCreatedAt(existing.getCreatedAt());
        ensureUnique(normalized, existing.getUserId());

        if (ValidationUtils_24162120.trimToNull(rawPassword) == null) {
            normalized.setPasswordHash(existing.getPasswordHash());
        } else {
            validatePassword(rawPassword, false);
            normalized.setPasswordHash(PasswordUtils_24162120.encode(rawPassword.trim()));
        }

        userAccountDao.update(normalized);
        return userAccountDao.findById(existing.getUserId());
    }

    @Override
    public void delete(Long userId) throws Exception {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("The user id is not valid.");
        }
        userAccountDao.delete(userId);
    }

    private void ensureUnique(UserAccount_24162120 user, Long currentUserId) {
        UserAccount_24162120 usernameMatch = userAccountDao.findByUsername(user.getUsername());
        if (usernameMatch != null && !usernameMatch.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("This username already exists.");
        }

        UserAccount_24162120 emailMatch = userAccountDao.findByEmail(user.getEmail());
        if (emailMatch != null && !emailMatch.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("This email address is already in use.");
        }
    }

    private void validatePassword(String rawPassword, boolean required) {
        String normalized = ValidationUtils_24162120.trimToNull(rawPassword);
        if (normalized == null) {
            if (required) {
                throw new IllegalArgumentException("Password must contain at least 6 characters.");
            }
            return;
        }
        if (normalized.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
    }

    private UserAccount_24162120 normalize(UserAccount_24162120 user) {
        if (user == null) {
            throw new IllegalArgumentException("The user data is not valid.");
        }

        String fullName = ValidationUtils_24162120.trimToNull(user.getFullName());
        if (fullName == null || ValidationUtils_24162120.exceedsLength(fullName, 120)) {
            throw new IllegalArgumentException("Please provide a valid full name.");
        }

        String username = ValidationUtils_24162120.trimToNull(user.getUsername());
        if (!ValidationUtils_24162120.isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be 3-50 characters and use letters, digits, dot, underscore, or dash.");
        }

        String email = ValidationUtils_24162120.normalizeEmail(user.getEmail());
        if (!ValidationUtils_24162120.isValidEmail(email)) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }

        String phone = ValidationUtils_24162120.trimToNull(user.getPhone());
        if (!ValidationUtils_24162120.isValidPhone(phone)) {
            throw new IllegalArgumentException("Phone number may only contain digits, spaces, plus, dash, or parentheses.");
        }
        if (ValidationUtils_24162120.exceedsLength(phone, 20)) {
            throw new IllegalArgumentException("Phone number must not exceed 20 characters.");
        }

        String images = ValidationUtils_24162120.trimToNull(user.getImages());
        if (ValidationUtils_24162120.exceedsLength(images, 500)) {
            throw new IllegalArgumentException("Image URL must not exceed 500 characters.");
        }

        String roleName = ValidationUtils_24162120.normalizeRoleName(user.getRoleName());
        if (!ValidationUtils_24162120.isValidRoleName(roleName)) {
            throw new IllegalArgumentException("Please choose a valid role.");
        }

        UserAccount_24162120 normalized = new UserAccount_24162120();
        normalized.setUserId(user.getUserId());
        normalized.setFullName(fullName);
        normalized.setUsername(username);
        normalized.setEmail(email);
        normalized.setPhone(phone);
        normalized.setImages(images);
        normalized.setRoleName(roleName);
        normalized.setEnabled(user.isEnabled());
        normalized.setStatus(user.getStatus() == 1 ? 1 : 0);
        normalized.setCreatedAt(user.getCreatedAt());
        return normalized;
    }
}
