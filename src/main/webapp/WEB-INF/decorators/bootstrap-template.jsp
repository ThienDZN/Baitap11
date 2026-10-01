<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title">baitap11</sitemesh:write></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f6f7fb; }
        .site-header { background: #1f2544; }
        .site-header .navbar-brand { color: #fff; font-weight: 700; letter-spacing: .04em; }
        .site-header .nav-link { color: rgba(255,255,255,.82); }
        .site-header .nav-link:hover, .site-header .nav-link.active { color: #fff; }
        .site-footer { color: #5b6178; border-top: 1px solid #e3e6f0; margin-top: 2rem; }
        .video-poster { width: 100%; height: 170px; object-fit: cover; background: #dfe3ee; }
        .pagination-wrap { display: flex; gap: .25rem; justify-content: center; margin-top: 1rem; }
        .category-heading { border-left: 4px solid #1f2544; padding-left: .6rem; }
    </style>
    <sitemesh:write property="head"/>
</head>
<body>
<header class="site-header">
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="<c:url value='/home'/>">baitap11</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="mainNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/home'/>">Trang Chủ</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/product'/>">Sản phẩm</a>
                    </li>
                    <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'USER'}">
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/cart'/>">Giỏ hàng</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/orders'/>">Đơn hàng</a>
                        </li>
                    </c:if>
                    <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/admin/videos'/>">Trang quản trị</a>
                        </li>
                    </c:if>
                </ul>
                <ul class="navbar-nav">
                    <c:choose>
                        <c:when test="${sessionScope.currentUser != null}">
                            <li class="nav-item">
                                <span class="nav-link">Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/></span>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/logout'/>">Đăng xuất</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/login'/>">Đăng nhập</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/register'/>">Đăng ký</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
</header>

<main class="container py-4">
    <c:if test="${not empty param.message}">
        <div class="alert alert-info"><c:out value="${param.message}"/></div>
    </c:if>
    <c:if test="${not empty message}">
        <div class="alert alert-info"><c:out value="${message}"/></div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>
    <sitemesh:write property="body"/>
</main>

<footer class="site-footer text-center py-3 small">
    baitap11 &middot; Giỏ hàng, thanh toán và lịch sử đơn hàng
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
