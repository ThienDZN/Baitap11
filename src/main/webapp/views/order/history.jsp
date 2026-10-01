<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử đơn hàng</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">Lịch sử đơn hàng <small>Lọc đơn theo trạng thái hiện tại được lưu trong cơ sở dữ liệu.</small></div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/product'/>">Cửa hàng</a>
            <a class="btn btn-secondary" href="<c:url value='/cart'/>">Giỏ hàng</a>
            <a class="btn btn-primary" href="<c:url value='/logout'/>">Đăng xuất</a>
        </div>
    </div>

    <c:if test="${not empty param.message}"><div class="message-box"><c:out value="${param.message}"/></div></c:if>
    <c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

    <section class="panel section-panel">
        <div class="section-head">
            <div>
                <h1>Đơn hàng của tôi</h1>
                <p>Chọn một trong tám trạng thái theo yêu cầu đề bài.</p>
            </div>
        </div>
        <form class="search-row" method="get" action="<c:url value='/orders'/>">
            <select class="form-select" name="status" style="max-width:300px;">
                <option value="">Tất cả trạng thái</option>
                <c:forEach items="${orderStatuses}" var="status">
                    <option value="<c:out value='${status.code}'/>" ${selectedStatus == status.code ? 'selected' : ''}><c:out value="${status.displayName}"/></option>
                </c:forEach>
            </select>
            <button class="btn btn-primary" type="submit">Lọc đơn hàng</button>
        </form>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-box">Không có đơn hàng phù hợp với bộ lọc.</div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${orders}" var="order">
                    <article class="soft-panel" style="padding:22px; margin-top:16px;">
                        <div class="section-head">
                            <div>
                                <h2>Đơn #<c:out value="${order.orderId}"/></h2>
                                <p>Tạo lúc: <c:out value="${order.createdAt}"/> &middot; Thanh toán: <c:out value="${order.paymentMethod}"/></p>
                            </div>
                            <div class="badge"><c:out value="${order.statusLabel}"/></div>
                        </div>
                        <p><strong>Người nhận:</strong> <c:out value="${order.recipientName}"/> &middot; <strong>SĐT:</strong> <c:out value="${order.phone}"/></p>
                        <p><strong>Địa chỉ:</strong> <c:out value="${order.shippingAddress}"/></p>
                        <div style="overflow-x:auto;">
                            <table class="data-table">
                                <thead><tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr></thead>
                                <tbody>
                                <c:forEach items="${order.items}" var="item">
                                    <tr>
                                        <td><c:out value="${item.productName}"/></td>
                                        <td><c:out value="${item.unitPrice}"/> ₫</td>
                                        <td><c:out value="${item.quantity}"/></td>
                                        <td><c:out value="${item.lineTotal}"/> ₫</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <p class="price-line" style="font-size:20px;">Tổng đơn: <c:out value="${order.totalAmount}"/> ₫</p>
                    </article>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
