package vn.iotstar.dao.impl;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dao.IVideoDao_24162120;
import vn.iotstar.entity.Video_24162120;

public class VideoDao_24162120 implements IVideoDao_24162120 {
    private Video_24162120 fetchById(EntityManager em, String id) {
        try {
            TypedQuery<Video_24162120> q = em.createQuery(
                    "SELECT v FROM Video_24162120 v LEFT JOIN FETCH v.category WHERE v.videoId = :id", Video_24162120.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void insert(Video_24162120 video) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (video.getCategory() != null) {
                int cid = video.getCategory().getCategoryid();
                vn.iotstar.entity.Category_24162120 managed = em.find(vn.iotstar.entity.Category_24162120.class, cid);
                if (managed == null) {
                    throw new IllegalArgumentException("Selected category does not exist.");
                }
                video.setCategory(managed);
            }
            em.persist(video);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Video_24162120 video) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(video);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String videoId) throws Exception {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Video_24162120 v = em.find(Video_24162120.class, videoId);
            if (v == null) throw new Exception("No video was found for id = " + videoId);
            em.remove(v);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Video_24162120 findById(String videoId) {
        if (videoId == null || videoId.isBlank()) return null;
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            return fetchById(em, videoId.trim());
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video_24162120> findAll() {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            return em.createQuery("SELECT v FROM Video_24162120 v LEFT JOIN FETCH v.category ORDER BY v.videoId DESC", Video_24162120.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video_24162120> findAllPaginated(int page, int pageSize) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Video_24162120> q = em.createQuery("SELECT v FROM Video_24162120 v LEFT JOIN FETCH v.category ORDER BY v.videoId DESC", Video_24162120.class);
            q.setFirstResult(Math.max(0, (page - 1) * pageSize));
            q.setMaxResults(pageSize);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int countAll() {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            Query q = em.createQuery("SELECT COUNT(v) FROM Video_24162120 v");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video_24162120> findActive(int page, int pageSize) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Video_24162120> q = em.createQuery("SELECT v FROM Video_24162120 v LEFT JOIN FETCH v.category WHERE v.active = 1 ORDER BY v.videoId DESC", Video_24162120.class);
            q.setFirstResult(Math.max(0, (page - 1) * pageSize));
            q.setMaxResults(pageSize);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int countActive() {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            Query q = em.createQuery("SELECT COUNT(v) FROM Video_24162120 v WHERE v.active = 1");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video_24162120> findLatestActive(int limit) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Video_24162120> q = em.createQuery("SELECT v FROM Video_24162120 v LEFT JOIN FETCH v.category WHERE v.active = 1 ORDER BY v.videoId DESC", Video_24162120.class);
            q.setMaxResults(limit);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video_24162120> findByCategory(int categoryId, int page, int pageSize) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Video_24162120> q = em.createQuery("SELECT v FROM Video_24162120 v LEFT JOIN FETCH v.category c WHERE v.active = 1 AND c.categoryid = :cid ORDER BY v.videoId DESC", Video_24162120.class);
            q.setParameter("cid", categoryId);
            q.setFirstResult(Math.max(0, (page - 1) * pageSize));
            q.setMaxResults(pageSize);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int countByCategory(int categoryId) {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            Query q = em.createQuery("SELECT COUNT(v) FROM Video_24162120 v WHERE v.active = 1 AND v.category.categoryid = :cid");
            q.setParameter("cid", categoryId);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> countGroupByCategory() {
        EntityManager em = JpaConfig_24162120.getEntityManager();
        try {
            Query q = em.createQuery("SELECT v.category.categoryid, v.category.categoryname, COUNT(v) FROM Video_24162120 v GROUP BY v.category.categoryid, v.category.categoryname ORDER BY v.category.categoryid");
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}
