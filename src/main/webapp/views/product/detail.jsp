<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Chi tiết sản phẩm
            <small>Kiểm tra tồn kho, chọn số lượng và thêm sản phẩm vào giỏ hàng.</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-primary" href="<c:url value='/product'/>">Danh sách sản phẩm</a>
            <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'USER'}">
                <a class="btn btn-secondary" href="<c:url value='/cart'/>">Giỏ hàng</a>
                <a class="btn btn-secondary" href="<c:url value='/orders'/>">Lịch sử đơn</a>
            </c:if>
            <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Admin</a>
            </c:if>
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <a class="btn btn-secondary" href="<c:url value='/profile'/>">Profile</a>
                    <a class="btn btn-secondary" href="<c:url value='/logout'/>">Logout</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary" href="<c:url value='/login'/>">Login</a>
                    <a class="btn btn-secondary" href="<c:url value='/register'/>">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <section class="detail-grid">
        <div class="panel section-panel" style="padding:22px;">
            <c:choose>
                <c:when test="${empty product.image}"><img class="detail-image" src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}"><img class="detail-image" src="<c:out value='${product.image}'/>" alt="<c:out value='${product.productName}'/>"></c:when>
                <c:otherwise><img class="detail-image" src="<c:url value='/image?fname=${product.image}'/>" alt="<c:out value='${product.productName}'/>"></c:otherwise>
            </c:choose>
        </div>
        <div class="panel detail-copy">
            <div class="eyebrow"><c:out value="${product.category.categoryname}"/></div>
            <h1 style="margin:12px 0 14px; font-size:42px;"><c:out value="${product.productName}"/></h1>
            <div class="price-line">Giá: <c:out value="${product.price}"/> ₫</div>
            <div class="meta-line">Tồn kho: <c:out value="${product.quantity}"/> sản phẩm</div>
            <p class="hero-lead" style="font-size:17px; max-width:none;"><c:out value="${product.description}"/></p>
            <div class="form-actions">
                <c:if test="${not empty youtubeUrl}">
                    <a class="btn btn-primary" href="${youtubeUrl}" target="_blank" rel="noopener noreferrer">Open on YouTube</a>
                </c:if>
                <a class="btn btn-secondary" href="<c:url value='/product'/>">Quay lại cửa hàng</a>
                <a class="btn btn-secondary" href="<c:url value='/home'/>">Quay lại trang chủ</a>
            </div>
            <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'USER'}">
                <c:choose>
                    <c:when test="${product.quantity > 0}">
                        <form class="form-actions" method="post" action="<c:url value='/cart/add'/>">
                            <input type="hidden" name="productId" value="<c:out value='${product.productId}'/>">
                            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                            <label for="quantity">Số lượng</label>
                            <input id="quantity" class="form-input" style="max-width:120px;" type="number" name="quantity" min="1" max="<c:out value='${product.quantity}'/>" value="1" required>
                            <button class="btn btn-primary" type="submit">Thêm vào giỏ</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="error-box">Sản phẩm hiện đã hết hàng.</div>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </section>
</div>
</body>
</html>
