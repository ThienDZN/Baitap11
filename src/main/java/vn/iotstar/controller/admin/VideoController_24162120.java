package vn.iotstar.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.entity.Video_24162120;
import vn.iotstar.service.ICategoryService_24162120;
import vn.iotstar.service.IVideoService_24162120;
import vn.iotstar.service.impl.CategoryServiceImpl_24162120;
import vn.iotstar.service.impl.VideoServiceImpl_24162120;
import vn.iotstar.util.LocalImageStorage_24162120;
import vn.iotstar.validation.ValidationErrors_24162120;
import vn.iotstar.validation.ValidationUtils_24162120;

/**
 * Trang quan tri: CRUD bang Videos voi phan trang 6 video/trang.
 */
@MultipartConfig
public class VideoController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IVideoService_24162120 videoService = new VideoServiceImpl_24162120();
    private final ICategoryService_24162120 categoryService = new CategoryServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getServletPath();
        if ("/admin/videos".equals(p)) { showList(req, resp); return; }
        if ("/admin/video/add".equals(p)) { showForm(req, resp, new Video_24162120(), true); return; }
        if ("/admin/video/edit".equals(p)) { showEdit(req, resp); return; }
        if ("/admin/video/delete".equals(p)) { deleteVideo(req, resp); return; }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getServletPath();
        if ("/admin/video/insert".equals(p)) { insertVideo(req, resp); return; }
        if ("/admin/video/update".equals(p)) { updateVideo(req, resp); return; }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = parsePage(req.getParameter("page"));
        int pageSize = 6;
        int total = videoService.countAll();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
        if (page > totalPages) page = totalPages;
        req.setAttribute("videos", videoService.findAllPaginated(page, pageSize));
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalItems", total);
        req.getRequestDispatcher("/views/admin/video-list.jsp").include(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Video_24162120 video = videoService.findById(req.getParameter("id"));
        if (video == null) {
            redirect(resp, req.getContextPath() + "/admin/videos", "Video_24162120 not found.");
            return;
        }
        showForm(req, resp, video, false);
    }

    private void bind(HttpServletRequest req, Video_24162120 video, Map<String, String> formData, ValidationErrors_24162120 errors) {
        req.setAttribute("video", video);
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
        req.setAttribute("categories", categoryService.findAll());
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Video_24162120 video, boolean isInsert) throws ServletException, IOException {
        req.setAttribute("video", video);
        req.setAttribute("categories", categoryService.findAll());
        req.setAttribute("isInsert", isInsert);
        req.getRequestDispatcher("/views/admin/video-form.jsp").include(req, resp);
    }

    private void logDebug(String msg, Throwable e) {
        try {
            java.nio.file.Path log = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"), "video-insert-debug.log");
            StringBuilder sb = new StringBuilder(new java.util.Date() + " " + msg + System.lineSeparator());
            if (e != null) {
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                sb.append(sw.toString()).append(System.lineSeparator());
            }
            java.nio.file.Files.writeString(log, sb.toString(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
    }

    private void insertVideo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            logDebug("INSERT params videoId=" + req.getParameter("videoId") + " title=" + req.getParameter("title") + " categoryId=" + req.getParameter("categoryId") + " views=" + req.getParameter("views") + " active=" + req.getParameter("active") + " contentType=" + req.getContentType(), null);
            doInsertVideo(req, resp);
        } catch (Throwable e) {
            logDebug("INSERT FATAL", e);
            e.printStackTrace();
            try {
                req.setAttribute("error", "Insert failed: " + e + rootCauses(e));
                showForm(req, resp, (Video_24162120) req.getAttribute("video") != null ? (Video_24162120) req.getAttribute("video") : new Video_24162120(), true);
            } catch (Throwable t2) {
                throw new ServletException(t2);
            }
        }
    }

    private String rootCauses(Throwable e) {
        StringBuilder sb = new StringBuilder();
        Throwable c = e.getCause();
        int n = 0;
        while (c != null && n++ < 8) {
            sb.append(" | caused by ").append(c.getClass().getSimpleName()).append(": ").append(c.getMessage());
            c = c.getCause();
        }
        return sb.toString();
    }

    private void doInsertVideo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Video_24162120 video = new Video_24162120();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors;
        try {
            errors = validate(req, video, formData, true);
        } catch (Exception e) {
            errors = new ValidationErrors_24162120();
            errors.add("videoId", "Validate failed: " + e.getMessage());
            e.printStackTrace();
        }
        bind(req, video, formData, errors);
        if (errors.hasErrors()) { showForm(req, resp, video, true); return; }
        try {
            video.setPoster(resolvePoster(req, null));
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("posterFile", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            showForm(req, resp, video, true);
            return;
        }
        try {
            videoService.insert(video);
            redirect(resp, req.getContextPath() + "/admin/videos", "Video_24162120 created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            Throwable c = e.getCause();
            while (c != null) {
                msg += " | caused by: " + c.getClass().getSimpleName() + ": " + c.getMessage();
                c = c.getCause();
            }
            req.setAttribute("error", "Insert failed: " + msg);
            showForm(req, resp, video, true);
        }
    }

    private void updateVideo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = ValidationUtils_24162120.trimToNull(req.getParameter("videoId"));
        Video_24162120 existing = videoService.findById(id);
        if (existing == null) {
            redirect(resp, req.getContextPath() + "/admin/videos", "Video_24162120 not found.");
            return;
        }
        String previousPoster = existing.getPoster();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors_24162120 errors = validate(req, existing, formData, false);
        bind(req, existing, formData, errors);
        if (errors.hasErrors()) { showForm(req, resp, existing, false); return; }
        try {
            existing.setPoster(resolvePoster(req, previousPoster));
        } catch (IllegalArgumentException e) {
            errors.add("posterFile", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            showForm(req, resp, existing, false);
            return;
        }
        try {
            videoService.update(existing);
            redirect(resp, req.getContextPath() + "/admin/videos", "Video_24162120 updated successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, existing, false);
        }
    }

    private void deleteVideo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = ValidationUtils_24162120.trimToNull(req.getParameter("id"));
        Video_24162120 video = videoService.findById(id);
        try {
            if (video != null && LocalImageStorage_24162120.isLocalFile(video.getPoster())) {
                LocalImageStorage_24162120.deleteIfExists(video.getPoster());
            }
            videoService.delete(id);
            redirect(resp, req.getContextPath() + "/admin/videos", "Video_24162120 deleted successfully.");
        } catch (Exception e) {
            redirect(resp, req.getContextPath() + "/admin/videos", e.getMessage());
        }
    }

    private ValidationErrors_24162120 validate(HttpServletRequest req, Video_24162120 video, Map<String, String> formData, boolean isInsert) {
        ValidationErrors_24162120 errors = new ValidationErrors_24162120();

        String videoId = ValidationUtils_24162120.trimToNull(req.getParameter("videoId"));
        formData.put("videoId", ValidationUtils_24162120.emptyIfNull(videoId));
        if (videoId == null) errors.add("videoId", "Please enter VideoId (e.g. VD001).");
        else if (videoId.length() > 50) errors.add("videoId", "VideoId must not exceed 50 characters.");
        if (isInsert) video.setVideoId(videoId);

        String title = ValidationUtils_24162120.trimToNull(req.getParameter("title"));
        formData.put("title", ValidationUtils_24162120.emptyIfNull(title));
        if (title == null) errors.add("title", "Please enter Title.");
        else if (title.length() > 500) errors.add("title", "Title must not exceed 500 characters.");
        video.setTitle(title);

        String categoryId = ValidationUtils_24162120.trimToNull(req.getParameter("categoryId"));
        formData.put("categoryId", ValidationUtils_24162120.emptyIfNull(categoryId));
        if (!ValidationUtils_24162120.isPositiveInteger(categoryId)) {
            errors.add("categoryId", "Please choose a valid category.");
        } else {
            Category_24162120 c = categoryService.findById(Integer.parseInt(categoryId));
            if (c == null) errors.add("categoryId", "Selected category does not exist.");
            else video.setCategory(c);
        }

        String views = ValidationUtils_24162120.trimToNull(req.getParameter("views"));
        formData.put("views", ValidationUtils_24162120.emptyIfNull(views));
        if (views == null) video.setViews(0);
        else if (!ValidationUtils_24162120.isNonNegativeInteger(views)) errors.add("views", "Views must be an integer >= 0.");
        else video.setViews(Integer.parseInt(views));

        String active = ValidationUtils_24162120.trimToNull(req.getParameter("active"));
        formData.put("active", ValidationUtils_24162120.emptyIfNull(active));
        if (!ValidationUtils_24162120.isStatusValue(active)) errors.add("active", "Please select Active status.");
        else video.setActive("1".equals(active) ? 1 : 0);

        String description = ValidationUtils_24162120.trimToNull(req.getParameter("description"));
        formData.put("description", ValidationUtils_24162120.emptyIfNull(description));
        if (ValidationUtils_24162120.exceedsLength(description, 500)) errors.add("description", "Description must not exceed 500 characters.");
        video.setDescription(description);

        String poster = ValidationUtils_24162120.trimToNull(req.getParameter("poster"));
        formData.put("poster", ValidationUtils_24162120.emptyIfNull(poster));
        if (ValidationUtils_24162120.exceedsLength(poster, 500)) errors.add("poster", "Poster URL must not exceed 500 characters.");
        else if (poster != null && !ValidationUtils_24162120.isValidHttpUrl(poster)) errors.add("poster", "Poster URL must start with http:// or https:// (or leave empty and upload file).");
        if (poster != null) video.setPoster(poster);

        return errors;
    }

    private String resolvePoster(HttpServletRequest req, String oldPoster) throws IOException, ServletException {
        String link = ValidationUtils_24162120.trimToNull(req.getParameter("poster"));
        Part part = null;
        try { part = req.getPart("posterFile"); } catch (Exception ignored) {}
        if (LocalImageStorage_24162120.hasUpload(part)) {
            String saved = LocalImageStorage_24162120.storeImage(part, "video");
            if (LocalImageStorage_24162120.isLocalFile(oldPoster)) LocalImageStorage_24162120.deleteIfExists(oldPoster);
            return saved;
        }
        if (link != null) return link;
        return ValidationUtils_24162120.trimToNull(oldPoster);
    }

    private void redirect(HttpServletResponse resp, String url, String msg) throws IOException {
        resp.sendRedirect(url + "?message=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }

    private int parsePage(String raw) {
        try { return Math.max(1, Integer.parseInt(raw)); } catch (Exception e) { return 1; }
    }
}
