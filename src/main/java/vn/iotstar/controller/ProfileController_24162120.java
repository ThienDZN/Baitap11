package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.config.SessionConstants_24162120;
import vn.iotstar.dto.UserProfileUpdateRequest_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IUserProfileService_24162120;
import vn.iotstar.service.impl.UserProfileServiceImpl_24162120;
import vn.iotstar.util.LocalImageStorage_24162120;
import vn.iotstar.validation.ValidationErrors_24162120;
import vn.iotstar.validation.ValidationUtils_24162120;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
public class ProfileController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserProfileService_24162120 userProfileService = new UserProfileServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount_24162120 currentUser = requireCurrentUser(req, resp);
        if (currentUser == null) {
            return;
        }

        UserAccount_24162120 freshUser = userProfileService.findById(currentUser.getUserId());
        req.getSession().setAttribute(SessionConstants_24162120.CURRENT_USER, freshUser);
        req.setAttribute("profileUser", freshUser);
        req.getRequestDispatcher("/views/user/profile.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount_24162120 currentUser = requireCurrentUser(req, resp);
        if (currentUser == null) {
            return;
        }

        UserAccount_24162120 persistedUser = userProfileService.findById(currentUser.getUserId());
        ValidationErrors_24162120 errors = validateProfileInput(req);
        req.setAttribute("errors", errors.asMap());
        if (errors.hasErrors()) {
            req.setAttribute("profileUser", persistedUser);
            bindSubmittedForm(req);
            req.getRequestDispatcher("/views/user/profile.jsp").include(req, resp);
            return;
        }

        String uploadedImage = null;
        try {
            Part imagePart = req.getPart("imageFile");
            String nextImage = persistedUser.getImages();
            if (LocalImageStorage_24162120.hasUpload(imagePart)) {
                uploadedImage = LocalImageStorage_24162120.storeImage(imagePart, "profile");
                nextImage = uploadedImage;
            }

            UserProfileUpdateRequest_24162120 updateRequest = new UserProfileUpdateRequest_24162120();
            updateRequest.setFullName(ValidationUtils_24162120.trimToNull(req.getParameter("fullName")));
            updateRequest.setPhone(ValidationUtils_24162120.trimToNull(req.getParameter("phone")));
            updateRequest.setImage(nextImage);

            UserAccount_24162120 updatedUser = userProfileService.updateProfile(persistedUser.getUserId(), updateRequest);
            cleanupOldImage(persistedUser.getImages(), uploadedImage);

            req.getSession().setAttribute(SessionConstants_24162120.CURRENT_USER, updatedUser);
            resp.sendRedirect(req.getContextPath() + "/profile?message=" + encode("Profile updated successfully."));
        } catch (IllegalArgumentException e) {
            rollbackUpload(uploadedImage);
            ValidationErrors_24162120 uploadErrors = new ValidationErrors_24162120();
            uploadErrors.add("imageFile", e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("errors", uploadErrors.asMap());
            req.setAttribute("profileUser", persistedUser);
            bindSubmittedForm(req);
            req.getRequestDispatcher("/views/user/profile.jsp").include(req, resp);
        } catch (Exception e) {
            rollbackUpload(uploadedImage);
            req.setAttribute("error", e.getMessage());
            req.setAttribute("profileUser", persistedUser);
            bindSubmittedForm(req);
            req.getRequestDispatcher("/views/user/profile.jsp").include(req, resp);
        }
    }

    private ValidationErrors_24162120 validateProfileInput(HttpServletRequest req) {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();
        String fullName = ValidationUtils_24162120.trimToNull(req.getParameter("fullName"));
        if (fullName == null) {
            errors.add("fullName", "Please enter your full name.");
        } else if (ValidationUtils_24162120.exceedsLength(fullName, 120)) {
            errors.add("fullName", "Full name must not exceed 120 characters.");
        }

        String phone = ValidationUtils_24162120.trimToNull(req.getParameter("phone"));
        if (ValidationUtils_24162120.exceedsLength(phone, 20)) {
            errors.add("phone", "Phone number must not exceed 20 characters.");
        } else if (!ValidationUtils_24162120.isValidPhone(phone)) {
            errors.add("phone", "Phone number may only contain digits, spaces, plus, dash, or parentheses.");
        }
        return errors;
    }

    private UserAccount_24162120 requireCurrentUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        UserAccount_24162120 currentUser = session == null ? null : (UserAccount_24162120) session.getAttribute(SessionConstants_24162120.CURRENT_USER);
        if (currentUser != null) {
            return currentUser;
        }
        resp.sendRedirect(req.getContextPath() + "/login?message="
                + encode("Please log in before updating your profile."));
        return null;
    }

    private void cleanupOldImage(String previousImage, String uploadedImage) {
        if (uploadedImage == null || !LocalImageStorage_24162120.isLocalFile(previousImage)) {
            return;
        }
        try {
            LocalImageStorage_24162120.deleteIfExists(previousImage);
        } catch (IOException ignored) {
        }
    }

    private void rollbackUpload(String uploadedImage) {
        if (uploadedImage == null) {
            return;
        }
        try {
            LocalImageStorage_24162120.deleteIfExists(uploadedImage);
        } catch (IOException ignored) {
        }
    }

    private void bindSubmittedForm(HttpServletRequest req) {
        req.setAttribute("formFullName", submittedValue(req, "fullName"));
        req.setAttribute("formPhone", submittedValue(req, "phone"));
    }

    private String submittedValue(HttpServletRequest req, String fieldName) {
        if (!req.getParameterMap().containsKey(fieldName)) {
            return null;
        }
        String value = ValidationUtils_24162120.trimToNull(req.getParameter(fieldName));
        return value == null ? "" : value;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
