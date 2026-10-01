package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.config.CartSession_24162120;
import vn.iotstar.config.CsrfToken_24162120;
import vn.iotstar.config.UserSessionAccess_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IProductService_24162120;
import vn.iotstar.service.impl.ProductServiceImpl_24162120;

/** User-only cart routes. All changes are POST requests protected by a session CSRF token. */
public class CartController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IProductService_24162120 productService = new ProductServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/cart".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (requireUser(request, response) == null) {
            return;
        }
        showCart(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (requireUser(request, response) == null) {
            return;
        }
        if (!CsrfToken_24162120.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Yêu cầu không hợp lệ.");
            return;
        }

        String servletPath = request.getServletPath();
        if ("/cart/add".equals(servletPath)) {
            addItem(request, response);
            return;
        }
        if ("/cart/update".equals(servletPath)) {
            updateItem(request, response);
            return;
        }
        if ("/cart/remove".equals(servletPath)) {
            removeItem(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ShoppingCart_24162120 cart = CartSession_24162120.getOrCreate(request);
        request.setAttribute("cart", cart);
        request.setAttribute("csrfToken", CsrfToken_24162120.ensureToken(request));
        request.getRequestDispatcher("/views/cart/index.jsp").include(request, response);
    }

    private void addItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = parseProductId(request.getParameter("productId"));
        int quantity = parsePositiveQuantity(request.getParameter("quantity"));
        if (productId == null || quantity < 1) {
            redirect(response, request.getContextPath() + "/product", "Sản phẩm hoặc số lượng không hợp lệ.");
            return;
        }

        Product_24162120 product = productService.findById(productId);
        if (!isPurchasable(product)) {
            redirect(response, request.getContextPath() + "/product", "Sản phẩm hiện không thể thêm vào giỏ hàng.");
            return;
        }

        try {
            CartSession_24162120.getOrCreate(request).add(product, quantity);
            redirect(response, request.getContextPath() + "/cart", "Đã thêm sản phẩm vào giỏ hàng.");
        } catch (IllegalArgumentException e) {
            redirect(response, request.getContextPath() + "/product/detail?id=" + productId, e.getMessage());
        }
    }

    private void updateItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = parseProductId(request.getParameter("productId"));
        int quantity = parsePositiveQuantity(request.getParameter("quantity"));
        if (productId == null || quantity < 1) {
            redirect(response, request.getContextPath() + "/cart", "Số lượng không hợp lệ.");
            return;
        }

        Product_24162120 product = productService.findById(productId);
        if (!isPurchasable(product)) {
            redirect(response, request.getContextPath() + "/cart", "Sản phẩm không còn được bán. Bạn có thể xóa khỏi giỏ hàng.");
            return;
        }

        try {
            CartSession_24162120.getOrCreate(request).updateQuantity(productId, quantity, product.getQuantity());
            redirect(response, request.getContextPath() + "/cart", "Đã cập nhật số lượng.");
        } catch (IllegalArgumentException e) {
            redirect(response, request.getContextPath() + "/cart", e.getMessage());
        }
    }

    private void removeItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = parseProductId(request.getParameter("productId"));
        if (productId == null) {
            redirect(response, request.getContextPath() + "/cart", "Sản phẩm không hợp lệ.");
            return;
        }
        CartSession_24162120.getOrCreate(request).remove(productId);
        redirect(response, request.getContextPath() + "/cart", "Đã xóa sản phẩm khỏi giỏ hàng.");
    }

    private UserAccount_24162120 requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserAccount_24162120 user = UserSessionAccess_24162120.currentActiveUser(request);
        if (user != null) {
            return user;
        }
        redirect(response, request.getContextPath() + "/login", "Vui lòng đăng nhập bằng tài khoản User để sử dụng giỏ hàng.");
        return null;
    }

    private boolean isPurchasable(Product_24162120 product) {
        return product != null && product.getStatus() == 1 && product.getQuantity() > 0
                && product.getPrice() != null && product.getPrice().signum() >= 0;
    }

    private Long parseProductId(String rawValue) {
        try {
            long productId = Long.parseLong(rawValue);
            return productId > 0 ? productId : null;
        } catch (Exception e) {
            return null;
        }
    }

    private int parsePositiveQuantity(String rawValue) {
        try {
            int quantity = Integer.parseInt(rawValue);
            return quantity > 0 && quantity <= 1_000_000 ? quantity : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private void redirect(HttpServletResponse response, String url, String message) throws IOException {
        String separator = url.contains("?") ? "&" : "?";
        response.sendRedirect(url + separator + "message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
