package vn.iotstar.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.service.ICategoryService_24162120;
import vn.iotstar.service.impl.CategoryServiceImpl_24162120;
import vn.iotstar.util.LocalImageStorage_24162120;
import vn.iotstar.validation.ValidationErrors_24162120;
import vn.iotstar.validation.ValidationUtils_24162120;

@MultipartConfig
public class CategoryController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ICategoryService_24162120 categoryService = new CategoryServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getServletPath();

        if (url.contains("/admin/categories")) {
            showList(req, resp);
            return;
        }
        if (url.contains("/admin/category/add")) {
            req.setAttribute("cate", new Category_24162120());
            req.getRequestDispatcher("/views/admin/category-add.jsp").include(req, resp);
            return;
        }
        if (url.contains("/admin/category/edit")) {
            showEditForm(req, resp);
            return;
        }
        if (url.contains("/admin/category/delete")) {
            deleteCategory(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getServletPath();
        if (url.contains("/admin/category/insert")) {
            insertCategory(req, resp);
            return;
        }
        if (url.contains("/admin/category/update")) {
            updateCategory(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();
        String keyword = ValidationUtils_24162120.trimToNull(req.getParameter("keyword"));
        if (ValidationUtils_24162120.exceedsLength(keyword, 50)) {
            errors.add("keyword", "Search keyword must not exceed 50 characters.");
        }

        List<Category_24162120> list = errors.hasErrors()
                ? categoryService.findAll()
                : (keyword == null ? categoryService.findAll() : categoryService.searchByName(keyword));
        req.setAttribute("errors", errors.asMap());
        req.setAttribute("listcate", list);
        req.setAttribute("keyword", keyword == null ? "" : keyword);
        req.getRequestDispatcher("/views/admin/category-list.jsp").include(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseId(req.getParameter("id"));
        Category_24162120 category = categoryService.findById(id);
        if (category == null) {
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category_24162120 entry not found.");
            return;
        }
        req.setAttribute("cate", category);
        req.getRequestDispatcher("/views/admin/category-edit.jsp").include(req, resp);
    }

    private void insertCategory(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Category_24162120 category = new Category_24162120();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validateCategoryRequest(req, category, formData, null, null);
        bindFormState(req, category, formData, errors);
        if (errors.hasErrors()) {
            req.getRequestDispatcher("/views/admin/category-add.jsp").include(req, resp);
            return;
        }

        try {
            category.setImages(resolveImage(req, null, "category"));
        } catch (IllegalArgumentException e) {
            errors.add("images1", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            req.getRequestDispatcher("/views/admin/category-add.jsp").include(req, resp);
            return;
        }

        try {
            categoryService.insert(category);
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category_24162120 created successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/views/admin/category-add.jsp").include(req, resp);
        }
    }

    private void updateCategory(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int categoryid = parseId(req.getParameter("categoryid"));
        Category_24162120 category = categoryService.findById(categoryid);
        if (category == null) {
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category_24162120 does not exist.");
            return;
        }

        String previousImage = category.getImages();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validateCategoryRequest(req, category, formData, categoryid, previousImage);
        bindFormState(req, category, formData, errors);
        if (errors.hasErrors()) {
            req.getRequestDispatcher("/views/admin/category-edit.jsp").include(req, resp);
            return;
        }

        try {
            category.setImages(resolveImage(req, previousImage, "category"));
        } catch (IllegalArgumentException e) {
            errors.add("images1", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            req.getRequestDispatcher("/views/admin/category-edit.jsp").include(req, resp);
            return;
        }

        try {
            categoryService.update(category);
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category_24162120 updated successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/views/admin/category-edit.jsp").include(req, resp);
        }
    }

    private void deleteCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = parseId(req.getParameter("id"));
        try {
            Category_24162120 category = categoryService.findById(id);
            if (category != null && isLocalImage(category.getImages())) {
                LocalImageStorage_24162120.deleteIfExists(category.getImages());
            }
            categoryService.delete(id);
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category_24162120 deleted successfully.");
        } catch (Exception e) {
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories", e.getMessage());
        }
    }

    private ValidationErrors_24162120 validateCategoryRequest(HttpServletRequest req, Category_24162120 category,
                                                     Map<String, String> formData, Integer currentId,
                                                     String previousImage) {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();

        String categoryName = ValidationUtils_24162120.trimToNull(req.getParameter("categoryname"));
        formData.put("categoryname", ValidationUtils_24162120.emptyIfNull(categoryName));
        if (categoryName == null) {
            errors.add("categoryname", "Please enter the category name.");
        } else if (ValidationUtils_24162120.exceedsLength(categoryName, 50)) {
            errors.add("categoryname", "Category_24162120 name must not exceed 50 characters.");
        } else {
            category.setCategoryname(categoryName);
            Category_24162120 duplicate = categoryService.findByCategoryname(categoryName);
            if (duplicate != null && (currentId == null || duplicate.getCategoryid() != currentId)) {
                errors.add("categoryname", "The category name already exists.");
            }
        }

        String imageValue = ValidationUtils_24162120.trimToNull(req.getParameter("images"));
        formData.put("images", ValidationUtils_24162120.emptyIfNull(imageValue));
        if (ValidationUtils_24162120.exceedsLength(imageValue, 500)) {
            errors.add("images", "Image URL must not exceed 500 characters.");
        } else if (imageValue != null && !isAcceptedImageReference(imageValue, previousImage)) {
            errors.add("images", "Image URL must start with http:// or https://.");
        }
        category.setImages(imageValue == null ? ValidationUtils_24162120.trimToNull(previousImage) : imageValue);

        String statusValue = ValidationUtils_24162120.trimToNull(req.getParameter("status"));
        formData.put("status", ValidationUtils_24162120.emptyIfNull(statusValue));
        if (!ValidationUtils_24162120.isStatusValue(statusValue)) {
            errors.add("status", "Please select a valid status.");
        } else {
            category.setStatus("1".equals(statusValue) ? 1 : 0);
        }
        return errors;
    }

    private void bindFormState(HttpServletRequest req, Category_24162120 category,
                               Map<String, String> formData, ValidationErrors_24162120 errors) {
        req.setAttribute("cate", category);
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
    }

    private String resolveImage(HttpServletRequest req, String oldImage, String prefix)
            throws IOException, ServletException {
        String imageLink = ValidationUtils_24162120.trimToNull(req.getParameter("images"));
        Part part = req.getPart("images1");
        if (LocalImageStorage_24162120.hasUpload(part)) {
            String savedName = LocalImageStorage_24162120.storeImage(part, prefix);
            if (isLocalImage(oldImage)) {
                LocalImageStorage_24162120.deleteIfExists(oldImage);
            }
            return savedName;
        }
        if (imageLink != null) {
            return imageLink;
        }
        return ValidationUtils_24162120.trimToNull(oldImage);
    }

    private void redirectWithMessage(HttpServletResponse resp, String baseUrl, String message)
            throws IOException {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        resp.sendRedirect(baseUrl + "?message=" + encoded);
    }

    private int parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The provided id is not valid.");
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
}
