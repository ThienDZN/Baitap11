package vn.iotstar.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.config.SessionConstants_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IUserAdminService_24162120;
import vn.iotstar.service.impl.UserAdminServiceImpl_24162120;
import vn.iotstar.validation.ValidationErrors_24162120;
import vn.iotstar.validation.ValidationUtils_24162120;

public class UserController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserAdminService_24162120 userAdminService = new UserAdminServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/users".equals(servletPath)) {
            showList(req, resp);
            return;
        }
        if ("/admin/user/add".equals(servletPath)) {
            UserAccount_24162120 user = new UserAccount_24162120();
            user.setRoleName("USER");
            user.setEnabled(true);
            user.setStatus(1);
            showForm(req, resp, user, "Create User", req.getContextPath() + "/admin/user/insert");
            return;
        }
        if ("/admin/user/edit".equals(servletPath)) {
            showEdit(req, resp);
            return;
        }
        if ("/admin/user/delete".equals(servletPath)) {
            deleteUser(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/user/insert".equals(servletPath)) {
            insertUser(req, resp);
            return;
        }
        if ("/admin/user/update".equals(servletPath)) {
            updateUser(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();
        String keyword = ValidationUtils_24162120.trimToNull(req.getParameter("keyword"));
        if (ValidationUtils_24162120.exceedsLength(keyword, 100)) {
            errors.add("keyword", "Search keyword must not exceed 100 characters.");
        }

        List<UserAccount_24162120> users = errors.hasErrors()
                ? userAdminService.findAll()
                : userAdminService.search(keyword);
        req.setAttribute("errors", errors.asMap());
        req.setAttribute("users", users);
        req.setAttribute("keyword", keyword == null ? "" : keyword);
        req.getRequestDispatcher("/views/admin/user-list.jsp").include(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = parseId(req.getParameter("id"));
        UserAccount_24162120 user = userAdminService.findById(userId);
        if (user == null) {
            redirect(resp, req.getContextPath() + "/admin/users", "User not found.");
            return;
        }
        showForm(req, resp, user, "Update User", req.getContextPath() + "/admin/user/update");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, UserAccount_24162120 user,
                          String formTitle, String formAction) throws ServletException, IOException {
        req.setAttribute("userAccount", user);
        req.setAttribute("formTitle", formTitle);
        req.setAttribute("formAction", formAction);
        req.getRequestDispatcher("/views/admin/user-form.jsp").include(req, resp);
    }

    private void insertUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount_24162120 user = new UserAccount_24162120();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validateUserRequest(req, user, formData, true, null);
        bindFormState(req, user, formData, errors);
        if (errors.hasErrors()) {
            showForm(req, resp, user, "Create User", req.getContextPath() + "/admin/user/insert");
            return;
        }

        try {
            userAdminService.insert(user, req.getParameter("password"));
            redirect(resp, req.getContextPath() + "/admin/users", "User created successfully.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, user, "Create User", req.getContextPath() + "/admin/user/insert");
        }
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = parseId(req.getParameter("userId"));
        UserAccount_24162120 existing = userAdminService.findById(userId);
        if (existing == null) {
            redirect(resp, req.getContextPath() + "/admin/users", "User not found.");
            return;
        }

        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validateUserRequest(req, existing, formData, false, existing.getImages());
        bindFormState(req, existing, formData, errors);
        if (errors.hasErrors()) {
            showForm(req, resp, existing, "Update User", req.getContextPath() + "/admin/user/update");
            return;
        }

        try {
            UserAccount_24162120 updatedUser = userAdminService.update(existing, req.getParameter("password"));
            refreshCurrentUserSession(req.getSession(false), updatedUser);
            redirect(resp, req.getContextPath() + "/admin/users", "User updated successfully.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, existing, "Update User", req.getContextPath() + "/admin/user/update");
        }
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = parseId(req.getParameter("id"));
        HttpSession session = req.getSession(false);
        UserAccount_24162120 currentUser = session == null ? null : (UserAccount_24162120) session.getAttribute(SessionConstants_24162120.CURRENT_USER);
        try {
            userAdminService.delete(userId);
            if (currentUser != null && currentUser.getUserId().equals(userId)) {
                session.invalidate();
                redirect(resp, req.getContextPath() + "/login", "Your account was deleted.");
                return;
            }
            redirect(resp, req.getContextPath() + "/admin/users", "User deleted successfully.");
        } catch (Exception e) {
            redirect(resp, req.getContextPath() + "/admin/users", e.getMessage());
        }
    }

    private ValidationErrors_24162120 validateUserRequest(HttpServletRequest req, UserAccount_24162120 user,
                                                 Map<String, String> formData,
                                                 boolean passwordRequired, String previousImage) {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();

        String fullName = ValidationUtils_24162120.trimToNull(req.getParameter("fullName"));
        formData.put("fullName", ValidationUtils_24162120.emptyIfNull(fullName));
        if (fullName == null) {
            errors.add("fullName", "Please enter the full name.");
        } else if (ValidationUtils_24162120.exceedsLength(fullName, 120)) {
            errors.add("fullName", "Full name must not exceed 120 characters.");
        } else {
            user.setFullName(fullName);
        }

        String username = ValidationUtils_24162120.trimToNull(req.getParameter("username"));
        formData.put("username", ValidationUtils_24162120.emptyIfNull(username));
        if (!ValidationUtils_24162120.isValidUsername(username)) {
            errors.add("username", "Username must be 3-50 characters and use letters, digits, dot, underscore, or dash.");
        } else {
            user.setUsername(username);
        }

        String email = ValidationUtils_24162120.normalizeEmail(req.getParameter("email"));
        formData.put("email", ValidationUtils_24162120.emptyIfNull(email));
        if (!ValidationUtils_24162120.isValidEmail(email) || ValidationUtils_24162120.exceedsLength(email, 120)) {
            errors.add("email", "Please enter a valid email address.");
        } else {
            user.setEmail(email);
        }

        String phone = ValidationUtils_24162120.trimToNull(req.getParameter("phone"));
        formData.put("phone", ValidationUtils_24162120.emptyIfNull(phone));
        if (ValidationUtils_24162120.exceedsLength(phone, 20)) {
            errors.add("phone", "Phone number must not exceed 20 characters.");
        } else if (!ValidationUtils_24162120.isValidPhone(phone)) {
            errors.add("phone", "Phone number may only contain digits, spaces, plus, dash, or parentheses.");
        } else {
            user.setPhone(phone);
        }

        String imageValue = ValidationUtils_24162120.trimToNull(req.getParameter("images"));
        formData.put("images", ValidationUtils_24162120.emptyIfNull(imageValue));
        if (ValidationUtils_24162120.exceedsLength(imageValue, 500)) {
            errors.add("images", "Image URL must not exceed 500 characters.");
        } else if (imageValue != null && !isAcceptedImageReference(imageValue, previousImage)) {
            errors.add("images", "Image URL must start with http:// or https://.");
        } else {
            user.setImages(imageValue == null ? ValidationUtils_24162120.trimToNull(previousImage) : imageValue);
        }

        String roleName = ValidationUtils_24162120.normalizeRoleName(req.getParameter("roleName"));
        formData.put("roleName", ValidationUtils_24162120.emptyIfNull(roleName));
        if (!ValidationUtils_24162120.isValidRoleName(roleName)) {
            errors.add("roleName", "Please choose a valid role.");
        } else {
            user.setRoleName(roleName);
        }

        String enabledValue = ValidationUtils_24162120.trimToNull(req.getParameter("enabled"));
        formData.put("enabled", ValidationUtils_24162120.emptyIfNull(enabledValue));
        if (!ValidationUtils_24162120.isStatusValue(enabledValue)) {
            errors.add("enabled", "Please choose a valid enabled state.");
        } else {
            user.setEnabled("1".equals(enabledValue));
        }

        String statusValue = ValidationUtils_24162120.trimToNull(req.getParameter("status"));
        formData.put("status", ValidationUtils_24162120.emptyIfNull(statusValue));
        if (!ValidationUtils_24162120.isStatusValue(statusValue)) {
            errors.add("status", "Please choose a valid status.");
        } else {
            user.setStatus("1".equals(statusValue) ? 1 : 0);
        }

        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        if (passwordRequired && ValidationUtils_24162120.trimToNull(password) == null) {
            errors.add("password", "Please enter a password with at least 6 characters.");
        } else if (ValidationUtils_24162120.trimToNull(password) != null && password.trim().length() < 6) {
            errors.add("password", "Password must contain at least 6 characters.");
        }
        if (ValidationUtils_24162120.trimToNull(password) != null
                && !password.equals(confirmPassword == null ? "" : confirmPassword)) {
            errors.add("confirmPassword", "Password confirmation does not match.");
        }

        return errors;
    }

    private void bindFormState(HttpServletRequest req, UserAccount_24162120 user,
                               Map<String, String> formData, ValidationErrors_24162120 errors) {
        req.setAttribute("userAccount", user);
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
    }

    private void refreshCurrentUserSession(HttpSession session, UserAccount_24162120 updatedUser) {
        if (session == null || updatedUser == null) {
            return;
        }
        UserAccount_24162120 currentUser = (UserAccount_24162120) session.getAttribute(SessionConstants_24162120.CURRENT_USER);
        if (currentUser != null && currentUser.getUserId().equals(updatedUser.getUserId())) {
            session.setAttribute(SessionConstants_24162120.CURRENT_USER, updatedUser);
        }
    }

    private Long parseId(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (Exception e) {
            return -1L;
        }
    }

    private boolean isAcceptedImageReference(String value, String previousImage) {
        return ValidationUtils_24162120.isValidHttpUrl(value)
                || (isLocalImage(previousImage) && value.equals(ValidationUtils_24162120.trimToNull(previousImage)));
    }

    private boolean isLocalImage(String value) {
        return value != null
                && !value.isBlank()
                && !value.startsWith("http://")
                && !value.startsWith("https://");
    }

    private void redirect(HttpServletResponse resp, String baseUrl, String message) throws IOException {
        resp.sendRedirect(baseUrl + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
