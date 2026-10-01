<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>${isInsert ? 'Thêm' : 'Sửa'} Video</title></head>
<body>
<h2 class="h4 mb-3">${isInsert ? 'Thêm video' : 'Cập nhật video'}</h2>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<form method="post"
      action="<c:url value='${isInsert ? "/admin/video/insert" : "/admin/video/update"}'/>"
      enctype="multipart/form-data" class="bg-white p-4 border rounded">
    <c:if test="${not isInsert}">
        <input type="hidden" name="videoId" value="${video.videoId}">
    </c:if>

    <div class="mb-3">
        <label class="form-label">Mã video (VideoId)</label>
        <c:choose>
            <c:when test="${isInsert}">
                <input type="text" name="videoId" class="form-control ${not empty errors.videoId ? 'is-invalid' : ''}"
                       value="${not empty formData.videoId ? formData.videoId : ''}" maxlength="50" placeholder="VD001">
                <c:if test="${not empty errors.videoId}"><div class="invalid-feedback">${errors.videoId}</div></c:if>
            </c:when>
            <c:otherwise>
                <input type="text" class="form-control" value="${video.videoId}" disabled>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="mb-3">
        <label class="form-label">Tiêu đề</label>
        <input type="text" name="title" class="form-control ${not empty errors.title ? 'is-invalid' : ''}"
               value="${not empty formData.title ? formData.title : video.title}" maxlength="500">
        <c:if test="${not empty errors.title}"><div class="invalid-feedback">${errors.title}</div></c:if>
    </div>

    <div class="mb-3">
        <label class="form-label">Category</label>
        <select name="categoryId" class="form-select ${not empty errors.categoryId ? 'is-invalid' : ''}">
            <option value="">-- Chọn category --</option>
            <c:forEach items="${categories}" var="c">
                <option value="${c.categoryid}"
                    <c:if test="${(not empty formData.categoryId and formData.categoryId == c.categoryid)
                                  or (empty formData.categoryId and video.category != null and video.category.categoryid == c.categoryid)}">selected</c:if>>
                    ${c.categoryname}
                </option>
            </c:forEach>
        </select>
        <c:if test="${not empty errors.categoryId}"><div class="invalid-feedback">${errors.categoryId}</div></c:if>
    </div>

    <div class="mb-3">
        <label class="form-label">View</label>
        <input type="number" name="views" min="0" class="form-control ${not empty errors.views ? 'is-invalid' : ''}"
               value="${not empty formData.views ? formData.views : video.views}">
        <c:if test="${not empty errors.views}"><div class="invalid-feedback">${errors.views}</div></c:if>
    </div>

    <div class="mb-3">
        <label class="form-label">Poster (URL)</label>
        <input type="text" name="poster" class="form-control ${not empty errors.poster ? 'is-invalid' : ''}"
               value="${not empty formData.poster ? formData.poster : video.poster}" maxlength="500"
               placeholder="https://...">
        <c:if test="${not empty errors.poster}"><div class="invalid-feedback">${errors.poster}</div></c:if>
    </div>

    <div class="mb-3">
        <label class="form-label">Hoặc tải poster lên</label>
        <input type="file" name="posterFile" class="form-control" accept="image/*">
        <c:if test="${not empty errors.posterFile}"><div class="text-danger small">${errors.posterFile}</div></c:if>
    </div>

    <div class="mb-3">
        <label class="form-label">Description</label>
        <textarea name="description" rows="3" class="form-control ${not empty errors.description ? 'is-invalid' : ''}"
                  maxlength="500">${not empty formData.description ? formData.description : video.description}</textarea>
        <c:if test="${not empty errors.description}"><div class="invalid-feedback">${errors.description}</div></c:if>
    </div>

    <div class="mb-3">
        <label class="form-label">Active</label>
        <select name="active" class="form-select ${not empty errors.active ? 'is-invalid' : ''}">
            <option value="1" <c:if test="${(empty formData.active and video.active == 1) or formData.active == '1'}">selected</c:if>>Hiện</option>
            <option value="0" <c:if test="${formData.active == '0' or (empty formData.active and not isInsert and video.active == 0)}">selected</c:if>>Ẩn</option>
        </select>
        <c:if test="${not empty errors.active}"><div class="invalid-feedback">${errors.active}</div></c:if>
    </div>

    <button type="submit" class="btn btn-primary">${isInsert ? 'Thêm' : 'Cập nhật'}</button>
    <a class="btn btn-secondary" href="<c:url value='/admin/videos'/>">Quay lại</a>
</form>
</body>
</html>
