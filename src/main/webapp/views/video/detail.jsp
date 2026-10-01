<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>${video.title}</title></head>
<body>
<c:choose>
    <c:when test="${empty video}">
        <div class="alert alert-warning">Không tìm thấy video.</div>
    </c:when>
    <c:otherwise>
        <div class="row g-4 bg-white p-4 border rounded">
            <div class="col-md-5">
                <c:choose>
                    <c:when test="${not empty video.poster}">
                        <img src="${video.poster}" class="img-fluid rounded" alt="${video.title}">
                    </c:when>
                    <c:otherwise>
                        <div class="video-poster rounded d-flex align-items-center justify-content-center text-muted">No poster</div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-md-7">
                <h2 class="h4">Tiêu đề: ${video.title}</h2>
                <p class="mb-1">Mã video: ${video.videoId}</p>
                <p class="mb-1">Category name: ${video.category.categoryname}</p>
                <p class="mb-1">View: ${video.views}</p>
                <p class="mb-1">Share(10)</p>
                <p class="mb-3">Like(10)</p>
                <p>${video.description}</p>
                <a class="btn btn-secondary" href="<c:url value='/video'/>">Quay lại</a>
            </div>
        </div>
    </c:otherwise>
</c:choose>
</body>
</html>
