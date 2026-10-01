<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Danh sách video</title></head>
<body>
<h2 class="h4 mb-3">Danh sách video</h2>

<c:choose>
    <c:when test="${empty videos}">
        <div class="alert alert-warning">Chưa có video nào.</div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach items="${videos}" var="v">
                <div class="col-md-4">
                    <div class="card h-100">
                        <c:if test="${not empty v.poster}">
                            <img src="${v.poster}" class="card-img-top video-poster" alt="${v.title}">
                        </c:if>
                        <div class="card-body">
                            <h5 class="card-title h6">${v.title}</h5>
                            <p class="mb-1 small text-muted">Mã video: ${v.videoId}</p>
                            <p class="mb-1 small text-muted">Category: ${v.category.categoryname}</p>
                            <p class="mb-1 small">View: ${v.views}</p>
                            <p class="mb-1 small">Share(10)</p>
                            <p class="mb-1 small">Like(10)</p>
                        </div>
                        <div class="card-footer bg-white">
                            <a class="btn btn-sm btn-primary" href="<c:url value='/video/detail?id=${v.videoId}'/>">Xem chi tiết</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${totalPages > 1}">
            <nav class="pagination-wrap">
                <ul class="pagination mb-0">
                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                        <a class="page-link" href="<c:url value='/video?page=${currentPage - 1}'/>">&laquo;</a>
                    </li>
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                            <a class="page-link" href="<c:url value='/video?page=${i}'/>">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="<c:url value='/video?page=${currentPage + 1}'/>">&raquo;</a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </c:otherwise>
</c:choose>
</body>
</html>
