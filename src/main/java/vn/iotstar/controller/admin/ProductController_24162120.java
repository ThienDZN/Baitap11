package vn.iotstar.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
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
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.service.ICategoryService_24162120;
import vn.iotstar.service.IProductService_24162120;
import vn.iotstar.service.impl.CategoryServiceImpl_24162120;
import vn.iotstar.service.impl.ProductServiceImpl_24162120;
import vn.iotstar.util.LocalImageStorage_24162120;
import vn.iotstar.validation.ValidationErrors_24162120;
import vn.iotstar.validation.ValidationUtils_24162120;

@MultipartConfig
public class ProductController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IProductService_24162120 productService = new ProductServiceImpl_24162120();
    private final ICategoryService_24162120 categoryService = new CategoryServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/products".equals(servletPath)) {
            showList(req, resp);
            return;
        }
        if ("/admin/product/add".equals(servletPath)) {
            showForm(req, resp, new Product_24162120(), "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
            return;
        }
        if ("/admin/product/edit".equals(servletPath)) {
            showEdit(req, resp);
            return;
        }
        if ("/admin/product/delete".equals(servletPath)) {
            deleteProduct(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/product/insert".equals(servletPath)) {
            insertProduct(req, resp);
            return;
        }
        if ("/admin/product/update".equals(servletPath)) {
            updateProduct(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("products", productService.findAll());
        req.getRequestDispatcher("/views/admin/product-list.jsp").include(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Product_24162120 product = productService.findById(parseId(req.getParameter("id")));
        if (product == null) {
            redirect(resp, req.getContextPath() + "/admin/products", "Track entry not found.");
            return;
        }
        showForm(req, resp, product, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product_24162120 product, String formTitle, String formAction)
            throws ServletException, IOException {
        List<Category_24162120> categories = categoryService.findAll();
        req.setAttribute("product", product);
        req.setAttribute("categories", categories);
        req.setAttribute("formTitle", formTitle);
        req.setAttribute("formAction", formAction);
        req.getRequestDispatcher("/views/admin/product-form.jsp").include(req, resp);
    }

    private void insertProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Product_24162120 product = new Product_24162120();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validateProductRequest(req, product, formData, null);
        bindFormState(req, product, formData, errors);
        if (errors.hasErrors()) {
            showForm(req, resp, product, "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
            return;
        }

        try {
            product.setImage(resolveImage(req, null, "product"));
        } catch (IllegalArgumentException e) {
            errors.add("imageFile", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            showForm(req, resp, product, "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
            return;
        }

        try {
            productService.insert(product);
            redirect(resp, req.getContextPath() + "/admin/products", "Catalog entry created successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, product, "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
        }
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseId(req.getParameter("productId"));
        Product_24162120 existing = productService.findById(id);
        if (existing == null) {
            redirect(resp, req.getContextPath() + "/admin/products", "Track entry not found.");
            return;
        }

        String previousImage = existing.getImage();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validateProductRequest(req, existing, formData, previousImage);
        bindFormState(req, existing, formData, errors);
        if (errors.hasErrors()) {
            showForm(req, resp, existing, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
            return;
        }

        try {
            existing.setImage(resolveImage(req, previousImage, "product"));
        } catch (IllegalArgumentException e) {
            errors.add("imageFile", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            showForm(req, resp, existing, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
            return;
        }

        try {
            productService.update(existing);
            redirect(resp, req.getContextPath() + "/admin/products", "Catalog entry updated successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, existing, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
        }
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        Product_24162120 product = productService.findById(id);
        try {
            if (product != null && isLocalImage(product.getImage())) {
                LocalImageStorage_24162120.deleteIfExists(product.getImage());
            }
            productService.delete(id);
            redirect(resp, req.getContextPath() + "/admin/products", "Catalog entry deleted successfully.");
        } catch (Exception e) {
            redirect(resp, req.getContextPath() + "/admin/products", e.getMessage());
        }
    }

    private ValidationErrors_24162120 validateProductRequest(HttpServletRequest req, Product_24162120 product,
                                                    Map<String, String> formData, String previousImage) {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();

        String productName = ValidationUtils_24162120.trimToNull(req.getParameter("productName"));
        formData.put("productName", ValidationUtils_24162120.emptyIfNull(productName));
        if (productName == null) {
            errors.add("productName", "Please enter the title.");
        } else if (ValidationUtils_24162120.exceedsLength(productName, 150)) {
            errors.add("productName", "Title must not exceed 150 characters.");
        }
        product.setProductName(productName);

        String categoryIdValue = ValidationUtils_24162120.trimToNull(req.getParameter("categoryId"));
        formData.put("categoryId", ValidationUtils_24162120.emptyIfNull(categoryIdValue));
        if (!ValidationUtils_24162120.isPositiveInteger(categoryIdValue)) {
            errors.add("categoryId", "Please choose a valid category.");
        } else {
            Category_24162120 category = categoryService.findById(Integer.parseInt(categoryIdValue));
            if (category == null) {
                errors.add("categoryId", "Selected category does not exist.");
            } else {
                product.setCategory(category);
            }
        }

        String priceValue = ValidationUtils_24162120.trimToNull(req.getParameter("price"));
        formData.put("price", ValidationUtils_24162120.emptyIfNull(priceValue));
        if (priceValue == null) {
            errors.add("price", "Please enter the price.");
        } else if (!ValidationUtils_24162120.isNonNegativeDecimal(priceValue)) {
            errors.add("price", "Price must be a number greater than or equal to 0.");
        } else {
            product.setPrice(new BigDecimal(priceValue));
        }

        String quantityValue = ValidationUtils_24162120.trimToNull(req.getParameter("quantity"));
        formData.put("quantity", ValidationUtils_24162120.emptyIfNull(quantityValue));
        if (quantityValue == null) {
            errors.add("quantity", "Please enter the quantity.");
        } else if (!ValidationUtils_24162120.isNonNegativeInteger(quantityValue)) {
            errors.add("quantity", "Quantity must be an integer greater than or equal to 0.");
        } else {
            product.setQuantity(Integer.parseInt(quantityValue));
        }

        String description = ValidationUtils_24162120.trimToNull(req.getParameter("description"));
        formData.put("description", ValidationUtils_24162120.emptyIfNull(description));
        if (ValidationUtils_24162120.exceedsLength(description, 2000)) {
            errors.add("description", "Description must not exceed 2000 characters.");
        }
        product.setDescription(description);

        String imageValue = ValidationUtils_24162120.trimToNull(req.getParameter("image"));
        formData.put("image", ValidationUtils_24162120.emptyIfNull(imageValue));
        if (ValidationUtils_24162120.exceedsLength(imageValue, 500)) {
            errors.add("image", "Image URL must not exceed 500 characters.");
        } else if (imageValue != null && !isAcceptedImageReference(imageValue, previousImage)) {
            errors.add("image", "Image URL must start with http:// or https://.");
        }
        product.setImage(imageValue == null ? ValidationUtils_24162120.trimToNull(previousImage) : imageValue);

        String statusValue = ValidationUtils_24162120.trimToNull(req.getParameter("status"));
        formData.put("status", ValidationUtils_24162120.emptyIfNull(statusValue));
        if (!ValidationUtils_24162120.isStatusValue(statusValue)) {
            errors.add("status", "Please select a valid status.");
        } else {
            product.setStatus("1".equals(statusValue) ? 1 : 0);
        }

        return errors;
    }

    private void bindFormState(HttpServletRequest req, Product_24162120 product,
                               Map<String, String> formData, ValidationErrors_24162120 errors) {
        req.setAttribute("product", product);
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
    }

    private String resolveImage(HttpServletRequest req, String oldImage, String prefix)
            throws IOException, ServletException {
        String imageLink = ValidationUtils_24162120.trimToNull(req.getParameter("image"));
        Part part = req.getPart("imageFile");
        if (LocalImageStorage_24162120.hasUpload(part)) {
            String savedName = LocalImageStorage_24162120.storeImage(part, prefix);
            if (isLocalImage(oldImage)) {
                LocalImageStorage_24162120.deleteIfExists(oldImage);
            }
            return savedName;
        }
        return imageLink != null ? imageLink : ValidationUtils_24162120.trimToNull(oldImage);
    }

    private void redirect(HttpServletResponse resp, String baseUrl, String message) throws IOException {
        resp.sendRedirect(baseUrl + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
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
        return value != null && !value.isBlank() && !value.startsWith("http://") && !value.startsWith("https://");
    }
}
