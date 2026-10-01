package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.config.CartSession_24162120;
import vn.iotstar.config.CsrfToken_24162120;
import vn.iotstar.config.UserSessionAccess_24162120;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IOrderService_24162120;
import vn.iotstar.service.impl.OrderServiceImpl_24162120;

/** Checkout and history endpoints for an authenticated User. */
public class OrderController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IOrderService_24162120 orderService = new OrderServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount_24162120 user = requireUser(request, response);
        if (user == null) {
            return;
        }

        if ("/checkout".equals(request.getServletPath())) {
            showCheckout(request, response, user, null);
            return;
        }
        if ("/orders".equals(request.getServletPath())) {
            showHistory(request, response, user);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount_24162120 user = requireUser(request, response);
        if (user == null) {
            return;
        }
        if (!"/checkout".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!CsrfToken_24162120.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Yêu cầu không hợp lệ.");
            return;
        }

        ShoppingCart_24162120 cart = CartSession_24162120.getOrCreate(request);
        if (cart.isEmpty()) {
            redirect(response, request.getContextPath() + "/cart", "Giỏ hàng đang trống.");
            return;
        }

        CheckoutRequest_24162120 checkoutRequest = new CheckoutRequest_24162120(
                request.getParameter("recipientName"),
                request.getParameter("phone"),
                request.getParameter("shippingAddress"),
                request.getParameter("paymentMethod"));
        try {
            CustomerOrder_24162120 order = orderService.checkout(user, cart, checkoutRequest);
            cart.clear();
            redirect(response, request.getContextPath() + "/orders",
                    "Đặt hàng thành công. Mã đơn: " + order.getOrderId());
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            showCheckout(request, response, user, checkoutRequest);
        } catch (RuntimeException e) {
            request.setAttribute("error", "Không thể thanh toán lúc này. Vui lòng thử lại.");
            showCheckout(request, response, user, checkoutRequest);
        }
    }

    private void showCheckout(HttpServletRequest request, HttpServletResponse response, UserAccount_24162120 user,
                              CheckoutRequest_24162120 formData) throws ServletException, IOException {
        ShoppingCart_24162120 cart = CartSession_24162120.getOrCreate(request);
        if (cart.isEmpty()) {
            redirect(response, request.getContextPath() + "/cart", "Giỏ hàng đang trống.");
            return;
        }

        CheckoutRequest_24162120 checkoutForm = formData == null
                ? new CheckoutRequest_24162120(user.getFullName(), user.getPhone(), null, CheckoutRequest_24162120.COD)
                : formData;
        request.setAttribute("cart", cart);
        request.setAttribute("checkoutForm", checkoutForm);
        request.setAttribute("csrfToken", CsrfToken_24162120.ensureToken(request));
        request.getRequestDispatcher("/views/order/checkout.jsp").include(request, response);
    }

    private void showHistory(HttpServletRequest request, HttpServletResponse response, UserAccount_24162120 user)
            throws ServletException, IOException {
        String rawStatus = request.getParameter("status");
        Optional<OrderStatus_24162120> parsedStatus = OrderStatus_24162120.fromCode(rawStatus);
        OrderStatus_24162120 selectedStatus = parsedStatus.orElse(null);
        if (rawStatus != null && !rawStatus.isBlank() && selectedStatus == null) {
            request.setAttribute("error", "Trạng thái lọc không hợp lệ.");
        }

        request.setAttribute("orders", orderService.findForUser(user.getUserId(), selectedStatus));
        request.setAttribute("orderStatuses", OrderStatus_24162120.values());
        request.setAttribute("selectedStatus", selectedStatus == null ? "" : selectedStatus.name());
        request.getRequestDispatcher("/views/order/history.jsp").include(request, response);
    }

    private UserAccount_24162120 requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserAccount_24162120 user = UserSessionAccess_24162120.currentActiveUser(request);
        if (user != null) {
            return user;
        }
        redirect(response, request.getContextPath() + "/login", "Vui lòng đăng nhập bằng tài khoản User.");
        return null;
    }

    private void redirect(HttpServletResponse response, String url, String message) throws IOException {
        response.sendRedirect(url + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
