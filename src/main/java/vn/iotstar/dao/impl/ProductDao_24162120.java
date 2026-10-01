package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dao.IProductDao_24162120;
import vn.iotstar.entity.Product_24162120;

public class ProductDao_24162120 implements IProductDao_24162120 {
    @Override
    public void insert(Product_24162120 product) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(product);
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

    @Override
    public void update(Product_24162120 product) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(product);
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

    @Override
    public void delete(Long productId) throws Exception {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Product_24162120 product = entityManager.find(Product_24162120.class, productId);
            if (product == null) {
                throw new Exception("No catalog entry was found for id = " + productId);
            }
            entityManager.remove(product);
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

    @Override
    public Product_24162120 findById(Long productId) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Product_24162120> query = entityManager.createQuery(
                    "SELECT p FROM Product_24162120 p JOIN FETCH p.category WHERE p.productId = :id", Product_24162120.class);
            query.setParameter("id", productId);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Product_24162120> findAll() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            return entityManager.createNamedQuery("Product_24162120.findAll", Product_24162120.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Product_24162120> findLatestActive(int limit) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Product_24162120> query = entityManager.createQuery(
                    "SELECT p FROM Product_24162120 p JOIN FETCH p.category WHERE p.status = 1 ORDER BY p.productId DESC", Product_24162120.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Product_24162120> findActive(int page, int pageSize) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Product_24162120> query = entityManager.createQuery(
                    "SELECT p FROM Product_24162120 p JOIN FETCH p.category WHERE p.status = 1 ORDER BY p.productId DESC", Product_24162120.class);
            query.setFirstResult(Math.max(0, (page - 1) * pageSize));
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public int countActive() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT COUNT(p) FROM Product_24162120 p WHERE p.status = 1");
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            entityManager.close();
        }
    }
}
