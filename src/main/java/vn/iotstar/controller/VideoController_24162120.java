package vn.iotstar.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Video_24162120;
import vn.iotstar.service.IVideoService_24162120;
import vn.iotstar.service.impl.VideoServiceImpl_24162120;

/**
 * Trang nguoi dung: danh sach video (phan trang 6/trang) va chi tiet 1 video.
 */
public class VideoController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IVideoService_24162120 videoService = new VideoServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getServletPath();
        if ("/video/detail".equals(p)) { showDetail(req, resp); return; }
        if ("/video".equals(p)) { showList(req, resp); return; }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = parsePage(req.getParameter("page"));
        int pageSize = 6;
        int total = videoService.countActive();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
        if (page > totalPages) page = totalPages;
        List<Video_24162120> videos = videoService.findActive(page, pageSize);
        req.setAttribute("videos", videos);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalItems", total);
        req.getRequestDispatcher("/views/video/list.jsp").include(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        Video_24162120 video = videoService.findById(id);
        if (video == null) {
            resp.sendRedirect(req.getContextPath() + "/video?message=Video_24162120+not+found");
            return;
        }
        req.setAttribute("video", video);
        req.getRequestDispatcher("/views/video/detail.jsp").include(req, resp);
    }

    private int parsePage(String raw) {
        try { return Math.max(1, Integer.parseInt(raw)); } catch (Exception e) { return 1; }
    }
}
