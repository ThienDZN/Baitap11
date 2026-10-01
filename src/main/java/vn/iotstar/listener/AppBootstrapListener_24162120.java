package vn.iotstar.listener;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.config.PasswordUtils_24162120;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.entity.Video_24162120;

public class AppBootstrapListener_24162120 implements ServletContextListener {
    private static final String PROFILE_DEMO_USERNAME = "thien";
    private static final String PROFILE_DEMO_EMAIL = "thien@example.com";
    private static final String PROFILE_DEMO_PASSWORD = "User123@Aa1";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        seedAdminAndDemoData();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JpaConfig_24162120.close();
    }

    private void seedAdminAndDemoData() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Long userCount = entityManager.createQuery("SELECT COUNT(u) FROM UserAccount_24162120 u", Long.class).getSingleResult();
            if (userCount == 0) {
                UserAccount_24162120 admin = new UserAccount_24162120();
                admin.setFullName("System Admin");
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(PasswordUtils_24162120.encode("Admin@123"));
                admin.setRoleName("ADMIN");
                admin.setEnabled(true);
                admin.setStatus(1);
                entityManager.persist(admin);
            }

            ensureProfileDemoUser(entityManager);
            Map<String, Category_24162120> categories = ensureCategories(entityManager);
            syncDemoCatalog(entityManager, categories);
            syncDemoVideos(entityManager, categories);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    private void ensureProfileDemoUser(EntityManager entityManager) {
        List<UserAccount_24162120> matches = entityManager.createQuery(
                        "SELECT u FROM UserAccount_24162120 u WHERE LOWER(u.username) = :username",
                        UserAccount_24162120.class)
                .setParameter("username", PROFILE_DEMO_USERNAME.toLowerCase())
                .setMaxResults(1)
                .getResultList();

        UserAccount_24162120 profileUser = matches.isEmpty() ? null : matches.get(0);
        if (profileUser == null) {
            profileUser = new UserAccount_24162120();
            profileUser.setFullName("Thien");
            profileUser.setUsername(PROFILE_DEMO_USERNAME);
            profileUser.setEmail(PROFILE_DEMO_EMAIL);
            profileUser.setRoleName("USER");
            profileUser.setEnabled(true);
            profileUser.setStatus(1);
            profileUser.setPasswordHash(PasswordUtils_24162120.encode(PROFILE_DEMO_PASSWORD));
            entityManager.persist(profileUser);
            return;
        }

        if (profileUser.getFullName() == null || profileUser.getFullName().isBlank()) {
            profileUser.setFullName("Thien");
        }
        if (profileUser.getEmail() == null || profileUser.getEmail().isBlank()) {
            profileUser.setEmail(PROFILE_DEMO_EMAIL);
        }
        profileUser.setRoleName("USER");
        profileUser.setEnabled(true);
        profileUser.setStatus(1);
        profileUser.setPasswordHash(PasswordUtils_24162120.encode(PROFILE_DEMO_PASSWORD));
    }

    private Map<String, Category_24162120> ensureCategories(EntityManager entityManager) {
        List<Category_24162120> categories = entityManager.createQuery("SELECT c FROM Category_24162120 c", Category_24162120.class).getResultList();
        Map<String, Category_24162120> byName = categories.stream()
                .collect(Collectors.toMap(Category_24162120::getCategoryname, category -> category, (left, right) -> left, LinkedHashMap::new));

        createCategoryIfMissing(entityManager, byName, "Playlist Opener", "https://i.ytimg.com/vi/poGyHfrJ_uo/maxresdefault.jpg");
        createCategoryIfMissing(entityManager, byName, "Mid Playlist", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f");
        createCategoryIfMissing(entityManager, byName, "Closing Run", "https://images.unsplash.com/photo-1511379938547-c1f69419868d");

        entityManager.flush();
        return byName;
    }

    private void createCategoryIfMissing(EntityManager entityManager, Map<String, Category_24162120> byName, String name, String image) {
        if (byName.containsKey(name)) {
            return;
        }
        Category_24162120 category = new Category_24162120();
        category.setCategoryname(name);
        category.setImages(image);
        category.setStatus(1);
        entityManager.persist(category);
        byName.put(name, category);
    }

    private void syncDemoCatalog(EntityManager entityManager, Map<String, Category_24162120> categoryMap) {
        List<Product_24162120> currentProducts = entityManager.createQuery("SELECT p FROM Product_24162120 p", Product_24162120.class).getResultList();
        if (!currentProducts.isEmpty()) {
            return;
        }

        Category_24162120 opener = categoryMap.get("Playlist Opener");
        Category_24162120 mid = categoryMap.get("Mid Playlist");
        Category_24162120 closing = categoryMap.get("Closing Run");

        persistProduct(entityManager, "Khóc Đấy (Album Version)", "Track 1 of 12 from the YouTube playlist. Album Version. Runtime: 3:52.", new BigDecimal("360897"), 1, "https://i.ytimg.com/vi/poGyHfrJ_uo/maxresdefault.jpg", opener);
        persistProduct(entityManager, "Bút Chì Bạc (Album Version)", "Track 2 of 12 from the YouTube playlist. Album Version. Runtime: 2:57.", new BigDecimal("121795"), 2, "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f", opener);
        persistProduct(entityManager, "Hoá Ra Là (feat. Wala)", "Track 3 of 12. Featuring Wala. Runtime: 3:22.", new BigDecimal("532449"), 3, "https://images.unsplash.com/photo-1485579149621-3123dd979885", opener);
        persistProduct(entityManager, "Gội Đầu (feat. Hà Lê)", "Track 4 of 12. Featuring Hà Lê. Runtime: 4:25.", new BigDecimal("873446"), 4, "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3", opener);
        persistProduct(entityManager, "100%", "Track 5 of 12 from the playlist. Runtime: 2:57.", new BigDecimal("419868"), 5, "https://images.unsplash.com/photo-1501612780327-45045538702b", mid);
        persistProduct(entityManager, "Căn Gác Lặng", "Track 6 of 12 from the playlist. Runtime: 2:32.", new BigDecimal("252183"), 6, "https://images.unsplash.com/photo-1507838153414-b4b713384a76", mid);
        persistProduct(entityManager, "Đồng Ý (kết hợp với Thơ Tơ Mơ)", "Track 7 of 12. Collaboration with Thơ Tơ Mơ. Runtime: 2:41.", new BigDecimal("840501"), 7, "https://images.unsplash.com/photo-1496293455970-f8581aae0e3b", mid);
        persistProduct(entityManager, "60m Vuông", "Track 8 of 12 from the playlist. Runtime: 2:28.", new BigDecimal("311093"), 8, "https://images.unsplash.com/photo-1459749411175-04bf5292ceea", mid);
        persistProduct(entityManager, "Nấu Con Beat (feat. Wala)", "Track 9 of 12. Featuring Wala. Runtime: 3:21.", new BigDecimal("150139"), 9, "https://images.unsplash.com/photo-1516280440614-37939bbacd81", closing);
        persistProduct(entityManager, "Rất (feat. SUNI, Pixel Neko)", "Track 10 of 12. Featuring SUNI and Pixel Neko. Runtime: 3:21.", new BigDecimal("120034"), 10, "https://images.unsplash.com/photo-1510915361894-db8b60106cb1", closing);
        persistProduct(entityManager, "Sáng Ra Chỉ Cần", "Track 11 of 12 from the playlist. Runtime: 3:30.", new BigDecimal("127532"), 11, "https://images.unsplash.com/photo-1506157786151-b8491531f063", closing);
        persistProduct(entityManager, "Tình Nhân Muôn Kiếp", "Track 12 of 12 from the playlist. Runtime: 3:59.", new BigDecimal("580643"), 12, "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee", closing);
    }


    private void syncDemoVideos(EntityManager entityManager, Map<String, Category_24162120> categoryMap) {
        Long videoCount = entityManager.createQuery("SELECT COUNT(v) FROM Video_24162120 v", Long.class).getSingleResult();
        if (videoCount != null && videoCount > 0) {
            return;
        }
        Category_24162120 opener = categoryMap.get("Playlist Opener");
        Category_24162120 mid = categoryMap.get("Mid Playlist");
        Category_24162120 closing = categoryMap.get("Closing Run");
        persistVideo(entityManager, "VD001", "Khoa Hoc Lap Trinh Web 1", "Video_24162120 demo 1 cho de thi qua trinh 03.", 120, 1, "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f", opener);
        persistVideo(entityManager, "VD002", "Huong Dan Servlet JSP 2", "Video_24162120 demo 2 cho de thi qua trinh 03.", 230, 1, "https://images.unsplash.com/photo-1485579149621-3123dd979885", opener);
        persistVideo(entityManager, "VD003", "JPA Hibernate Co Ban 3", "Video_24162120 demo 3 cho de thi qua trinh 03.", 310, 1, "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3", opener);
        persistVideo(entityManager, "VD004", "Xay Dung CRUD Video_24162120 4", "Video_24162120 demo 4 cho de thi qua trinh 03.", 410, 1, "https://images.unsplash.com/photo-1501612780327-45045538702b", opener);
        persistVideo(entityManager, "VD005", "Phan Trang 6 Video_24162120 5", "Video_24162120 demo 5 cho de thi qua trinh 03.", 520, 1, "https://images.unsplash.com/photo-1507838153414-b4b713384a76", mid);
        persistVideo(entityManager, "VD006", "SiteMesh Decorator 6", "Video_24162120 demo 6 cho de thi qua trinh 03.", 160, 1, "https://images.unsplash.com/photo-1496293455970-f8581aae0e3b", mid);
        persistVideo(entityManager, "VD007", "OTP Session Login 7", "Video_24162120 demo 7 cho de thi qua trinh 03.", 270, 1, "https://images.unsplash.com/photo-1459749411175-04bf5292ceea", mid);
        persistVideo(entityManager, "VD008", "Quan Tri Video_24162120 Admin 8", "Video_24162120 demo 8 cho de thi qua trinh 03.", 380, 1, "https://images.unsplash.com/photo-1516280440614-37939bbacd81", mid);
        persistVideo(entityManager, "VD009", "Chi Tiet Video_24162120 9", "Video_24162120 demo 9 cho de thi qua trinh 03.", 190, 1, "https://images.unsplash.com/photo-1510915361894-db8b60106cb1", closing);
        persistVideo(entityManager, "VD010", "Home Theo Category_24162120 10", "Video_24162120 demo 10 cho de thi qua trinh 03.", 290, 1, "https://images.unsplash.com/photo-1506157786151-b8491531f063", closing);
        persistVideo(entityManager, "VD011", "Dem Video_24162120 Cau 5 11", "Video_24162120 demo 11 cho de thi qua trinh 03.", 330, 1, "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee", closing);
        persistVideo(entityManager, "VD012", "Bao Cao Nghiem Thu 12", "Video_24162120 demo 12 cho de thi qua trinh 03.", 440, 1, "https://i.ytimg.com/vi/poGyHfrJ_uo/maxresdefault.jpg", closing);
    }

    private void persistVideo(EntityManager entityManager, String videoId, String title, String description, int views,
                              int active, String poster, Category_24162120 category) {
        Video_24162120 video = new Video_24162120();
        video.setVideoId(videoId);
        video.setTitle(title);
        video.setDescription(description);
        video.setViews(views);
        video.setActive(active);
        video.setPoster(poster);
        video.setCategory(category);
        entityManager.persist(video);
    }

    private void persistProduct(EntityManager entityManager, String name, String description, BigDecimal price,
                                int quantity, String image, Category_24162120 category) {
        Product_24162120 product = new Product_24162120();
        product.setProductName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setImage(image);
        product.setStatus(1);
        product.setCategory(category);
        entityManager.persist(product);
    }
}
