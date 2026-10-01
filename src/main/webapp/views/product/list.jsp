<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cửa hàng sản phẩm</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Cửa hàng sản phẩm
            <small>Chọn sản phẩm, thêm vào giỏ, sau đó thanh toán bằng tài khoản User.</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-primary" href="<c:url value='/product'/>">Refresh</a>
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

    <c:if test="${not empty param.message}"><div class="message-box"><c:out value="${param.message}"/></div></c:if>

    <section class="panel section-panel">
        <div class="section-head">
            <div>
                <h1>Danh sách sản phẩm</h1>
                <p>Chỉ sản phẩm đang hoạt động mới được hiển thị và có thể thêm vào giỏ.</p>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-box">Chưa có sản phẩm nào để hiển thị.</div>
            </c:when>
            <c:otherwise>
                <div class="card-grid">
                    <c:forEach items="${products}" var="product">
                        <div class="item-card">
                            <c:choose>
                                <c:when test="${empty product.image}"><img src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                                <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}"><img src="<c:out value='${product.image}'/>" alt="<c:out value='${product.productName}'/>"></c:when>
                                <c:otherwise><img src="<c:url value='/image?fname=${product.image}'/>" alt="<c:out value='${product.productName}'/>"></c:otherwise>
                            </c:choose>
                            <div class="meta-line">Danh mục: <c:out value="${product.category.categoryname}"/></div>
                            <h3><c:out value="${product.productName}"/></h3>
                            <p><c:out value="${product.description}"/></p>
                            <div class="price-line">Giá: <c:out value="${product.price}"/> ₫</div>
                            <div class="meta-line">Còn lại: <c:out value="${product.quantity}"/> sản phẩm</div>
                            <div class="action-row">
                                <a class="btn btn-primary" href="<c:url value='/product/detail?id=${product.productId}'/>">Xem chi tiết</a>
                                <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'USER' and product.quantity > 0}">
                                    <form method="post" action="<c:url value='/cart/add'/>">
                                        <input type="hidden" name="productId" value="<c:out value='${product.productId}'/>">
                                        <input type="hidden" name="quantity" value="1">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                        <button class="btn btn-secondary" type="submit">Thêm vào giỏ</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                        <c:choose>
                            <c:when test="${pageNumber == currentPage}">
                                <span class="page-pill active">${pageNumber}</span>
                            </c:when>
                            <c:otherwise>
                                <a class="page-pill" href="<c:url value='/product?page=${pageNumber}'/>">${pageNumber}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
