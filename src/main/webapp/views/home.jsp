<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Trang chủ</title></head>
<body>
<h2 class="h4 mb-1">Danh sách video theo Category</h2>
<p class="text-muted">Tổng số video: ${grandTotal} &middot; Trang ${currentPage}/${totalPages}</p>

<%-- Cau 5: so luong video theo tung category --%>
<div class="mb-4">
    <c:forEach items="${categoryBlocks}" var="b">
        <span class="badge bg-secondary me-2 mb-2">${b.category.categoryname} (${b.videoCount})</span>
    </c:forEach>
</div>

<c:choose>
    <c:when test="${empty groupedVideos}">
        <div class="alert alert-warning">Chưa có video nào.</div>
    </c:when>
    <c:otherwise>
        <c:forEach items="${groupedVideos}" var="entry">
            <section class="mb-4">
                <h3 class="h5 category-heading mb-3">${entry.key.categoryname} (${entry.value.size()})</h3>
                <div class="row g-3">
                    <c:forEach items="${entry.value}" var="v">
                        <div class="col-md-4">
                            <div class="card h-100">
                                <c:if test="${not empty v.poster}">
                                    <img src="${v.poster}" class="card-img-top video-poster" alt="${v.title}">
                                </c:if>
                                <div class="card-body">
                                    <p class="mb-1 small"><strong>Tiêu đề:</strong> ${v.title}</p>
                                    <p class="mb-1 small text-muted">Mã video: ${v.videoId}</p>
                                    <p class="mb-1 small text-muted">Category name: ${v.category.categoryname}</p>
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
            </section>
        </c:forEach>

        <%-- Phan trang chung: << 1 2 3 4 5 >> --%>
        <nav class="pagination-wrap">
            <ul class="pagination mb-0">
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="<c:url value='/home?page=${currentPage - 1}'/>">&laquo;</a>
                </li>
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <a class="page-link" href="<c:url value='/home?page=${i}'/>">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="<c:url value='/home?page=${currentPage + 1}'/>">&raquo;</a>
                </li>
            </ul>
        </nav>
    </c:otherwise>
</c:choose>
</body>
</html>
