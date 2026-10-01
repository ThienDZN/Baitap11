package vn.iotstar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.entity.Video_24162120;
import vn.iotstar.service.ICategoryService_24162120;
import vn.iotstar.service.IVideoService_24162120;
import vn.iotstar.service.impl.CategoryServiceImpl_24162120;
import vn.iotstar.service.impl.VideoServiceImpl_24162120;

/**
 * Trang home vai tro user.
 * Hien thi video theo tung category, phan trang 3 video/trang (Cau 4).
 * Hien thi so luong video theo tung category (Cau 5).
 */
public class HomeController_24162120 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 3;

    private final IVideoService_24162120 videoService = new VideoServiceImpl_24162120();
    private final ICategoryService_24162120 categoryService = new CategoryServiceImpl_24162120();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = parsePage(req.getParameter("page"));

        List<Category_24162120> categories = categoryService.findAll();

        // Tong so video active tren toan bo category
        List<CategoryBlock> categoryBlocks = new ArrayList<>();
        int grandTotal = 0;
        for (Category_24162120 c : categories) {
            int count = videoService.countByCategory(c.getCategoryid());
            categoryBlocks.add(new CategoryBlock(c, count));
            grandTotal += count;
        }

        int totalPages = Math.max(1, (int) Math.ceil(grandTotal / (double) PAGE_SIZE));
        if (page > totalPages) page = totalPages;

        // Trang hien tai gom 3 video (tren toan bo danh sach active)
        List<Video_24162120> pageVideos = videoService.findActive(page, PAGE_SIZE);

        // Nhom 3 video cua trang hien tai theo category de hien thi dung kieu "Category_24162120 Name (...) + danh sach"
        Map<Category_24162120, List<Video_24162120>> grouped = new LinkedHashMap<>();
        for (Video_24162120 v : pageVideos) {
            grouped.computeIfAbsent(v.getCategory(), k -> new ArrayList<>()).add(v);
        }

        req.setAttribute("categoryBlocks", categoryBlocks);
        req.setAttribute("groupedVideos", grouped);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("grandTotal", grandTotal);
        req.getRequestDispatcher("/views/home.jsp").include(req, resp);
    }

    private int parsePage(String raw) {
        try { return Math.max(1, Integer.parseInt(raw)); } catch (Exception e) { return 1; }
    }

    /** Category_24162120 + so luong video cua category do (Cau 5). */
    public static class CategoryBlock {
        private final Category_24162120 category;
        private final int videoCount;

        public CategoryBlock(Category_24162120 category, int videoCount) {
            this.category = category;
            this.videoCount = videoCount;
        }

        public Category_24162120 getCategory() { return category; }
        public int getVideoCount() { return videoCount; }
    }
}
