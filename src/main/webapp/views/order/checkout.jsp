<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">Thanh toán <small>Xác nhận thông tin giao nhận để tạo đơn hàng mới.</small></div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/cart'/>">Quay lại giỏ hàng</a>
            <a class="btn btn-secondary" href="<c:url value='/orders'/>">Lịch sử đơn</a>
        </div>
    </div>

    <c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

    <section class="panel form-shell">
        <h1>Thông tin thanh toán</h1>
        <p class="hero-lead" style="font-size:16px; max-width:none;">Tổng thanh toán: <strong><c:out value="${cart.totalAmount}"/> ₫</strong>. Giá và tồn kho sẽ được kiểm tra lại khi tạo đơn.</p>
        <form method="post" action="<c:url value='/checkout'/>">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
            <div class="form-group">
                <label for="recipientName">Họ tên người nhận</label>
                <input id="recipientName" class="form-input" type="text" name="recipientName" maxlength="120" required value="<c:out value='${checkoutForm.recipientName}'/>">
            </div>
            <div class="form-group">
                <label for="phone">Số điện thoại</label>
                <input id="phone" class="form-input" type="tel" name="phone" maxlength="20" required value="<c:out value='${checkoutForm.phone}'/>">
            </div>
            <div class="form-group">
                <label for="shippingAddress">Địa chỉ giao hàng</label>
                <textarea id="shippingAddress" class="form-textarea" name="shippingAddress" maxlength="500" required><c:out value="${checkoutForm.shippingAddress}"/></textarea>
            </div>
            <div class="form-group">
                <label for="paymentMethod">Phương thức thanh toán</label>
                <select id="paymentMethod" class="form-select" name="paymentMethod" required>
                    <option value="COD" ${checkoutForm.paymentMethod == 'COD' ? 'selected' : ''}>Thanh toán khi nhận hàng</option>
                    <option value="BANK_TRANSFER" ${checkoutForm.paymentMethod == 'BANK_TRANSFER' ? 'selected' : ''}>Chuyển khoản ngân hàng</option>
                </select>
                <div class="field-hint">Bài tập ghi nhận phương thức thanh toán; không tích hợp cổng thanh toán bên ngoài.</div>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Xác nhận đặt hàng</button>
                <a class="btn btn-secondary" href="<c:url value='/cart'/>">Hủy và quay lại giỏ</a>
            </div>
        </form>
    </section>
</div>
</body>
</html>
