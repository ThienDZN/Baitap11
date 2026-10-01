<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">Giỏ hàng <small>Thêm, cập nhật số lượng hoặc xóa sản phẩm trước khi thanh toán.</small></div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/product'/>">Cửa hàng</a>
            <a class="btn btn-secondary" href="<c:url value='/orders'/>">Lịch sử đơn</a>
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Trang chủ</a>
            <a class="btn btn-primary" href="<c:url value='/logout'/>">Đăng xuất</a>
        </div>
    </div>

    <c:if test="${not empty param.message}"><div class="message-box"><c:out value="${param.message}"/></div></c:if>
    <c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

    <section class="panel section-panel">
        <div class="section-head">
            <div>
                <h1>Giỏ hàng của bạn</h1>
                <p><c:out value="${cart.itemCount}"/> sản phẩm &middot; Tổng tạm tính: <strong><c:out value="${cart.totalAmount}"/> ₫</strong></p>
            </div>
            <c:if test="${not empty cart.items}">
                <a class="btn btn-primary" href="<c:url value='/checkout'/>">Tiến hành thanh toán</a>
            </c:if>
        </div>

        <c:choose>
            <c:when test="${empty cart.items}">
                <div class="empty-box">Giỏ hàng đang trống. Hãy chọn sản phẩm từ cửa hàng.</div>
            </c:when>
            <c:otherwise>
                <div style="overflow-x:auto;">
                    <table class="data-table">
                        <thead>
                        <tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th><th>Thao tác</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${cart.items}" var="item">
                            <tr>
                                <td><c:out value="${item.productName}"/></td>
                                <td><c:out value="${item.unitPrice}"/> ₫</td>
                                <td>
                                    <form method="post" action="<c:url value='/cart/update'/>">
                                        <input type="hidden" name="productId" value="<c:out value='${item.productId}'/>">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                        <input class="form-input" style="width:90px; display:inline-block;" type="number" name="quantity" min="1" value="<c:out value='${item.quantity}'/>" required>
                                        <button class="btn btn-secondary" type="submit">Cập nhật</button>
                                    </form>
                                </td>
                                <td><c:out value="${item.lineTotal}"/> ₫</td>
                                <td>
                                    <form method="post" action="<c:url value='/cart/remove'/>">
                                        <input type="hidden" name="productId" value="<c:out value='${item.productId}'/>">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                        <button class="btn btn-danger" type="submit">Xóa</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
