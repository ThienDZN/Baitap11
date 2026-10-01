<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Quản trị Video</title></head>
<body>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="h4 mb-0">Quản trị Video</h2>
    <a class="btn btn-primary" href="<c:url value='/admin/video/add'/>">Thêm video</a>
</div>

<div class="table-responsive">
    <table class="table table-bordered table-hover align-middle bg-white">
        <thead class="table-light">
        <tr>
            <th>Mã video</th>
            <th>Poster</th>
            <th>Tiêu đề</th>
            <th>Category</th>
            <th>View</th>
            <th>Active</th>
            <th style="width:160px;">Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty videos}">
                <tr><td colspan="7" class="text-center text-muted">Chưa có video nào.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${videos}" var="v">
                    <tr>
                        <td>${v.videoId}</td>
                        <td>
                            <c:if test="${not empty v.poster}">
                                <img src="${v.poster}" alt="" style="width:80px;height:50px;object-fit:cover;">
                            </c:if>
                        </td>
                        <td>${v.title}</td>
                        <td>${v.category.categoryname}</td>
                        <td>${v.views}</td>
                        <td>
                            <c:choose>
                                <c:when test="${v.active == 1}"><span class="badge bg-success">Hiện</span></c:when>
                                <c:otherwise><span class="badge bg-secondary">Ẩn</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a class="btn btn-sm btn-outline-primary" href="<c:url value='/admin/video/edit?id=${v.videoId}'/>">Sửa</a>
                            <a class="btn btn-sm btn-outline-danger" href="<c:url value='/admin/video/delete?id=${v.videoId}'/>"
                               onclick="return confirm('Xóa video này?');">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

<c:if test="${totalPages > 1}">
    <nav class="pagination-wrap">
        <ul class="pagination mb-0">
            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                <a class="page-link" href="<c:url value='/admin/videos?page=${currentPage - 1}'/>">&laquo;</a>
            </li>
            <c:forEach var="i" begin="1" end="${totalPages}">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link" href="<c:url value='/admin/videos?page=${i}'/>">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                <a class="page-link" href="<c:url value='/admin/videos?page=${currentPage + 1}'/>">&raquo;</a>
            </li>
        </ul>
    </nav>
</c:if>
<p class="text-center text-muted small mt-2">Tổng: ${totalItems} video</p>
</body>
</html>
