package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.entity.Video_24162120;

public class JpaManualTest_24162120 {
    public static void main(String[] args) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        Category_24162120 category = new Category_24162120();
        category.setCategoryname("Iphone");
        category.setImages("iphone.jpg");
        category.setStatus(1);

        Video_24162120 video = new Video_24162120();
        video.setVideoId("v01");
        video.setTitle("JPA Test");
        video.setActive(1);
        video.setViews(100);
        video.setCategory(category);

        try {
            transaction.begin();
            entityManager.persist(category);
            entityManager.persist(video);
            transaction.commit();
            System.out.println("Insert test data success.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
