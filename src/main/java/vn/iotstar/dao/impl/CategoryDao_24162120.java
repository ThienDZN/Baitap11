package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dao.ICategoryDao_24162120;
import vn.iotstar.entity.Category_24162120;

public class CategoryDao_24162120 implements ICategoryDao_24162120 {

    @Override
    public void insert(Category_24162120 category) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(category);
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
    public void update(Category_24162120 category) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(category);
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
    public void delete(int cateid) throws Exception {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Category_24162120 category = entityManager.find(Category_24162120.class, cateid);
            if (category == null) {
                throw new Exception("No category was found for id = " + cateid);
            }
            entityManager.remove(category);
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
    public Category_24162120 findById(int cateid) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            return entityManager.find(Category_24162120.class, cateid);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Category_24162120 findByCategoryname(String name) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        String jpql = "SELECT c FROM Category_24162120 c WHERE LOWER(c.categoryname) = LOWER(:catename)";
        try {
            TypedQuery<Category_24162120> query = entityManager.createQuery(jpql, Category_24162120.class);
            query.setParameter("catename", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category_24162120> findAll() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Category_24162120> query =
                    entityManager.createNamedQuery("Category_24162120.findAll", Category_24162120.class);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category_24162120> searchByName(String catname) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        String jpql = "SELECT c FROM Category_24162120 c WHERE LOWER(c.categoryname) LIKE LOWER(:catename) ORDER BY c.categoryid DESC";
        try {
            TypedQuery<Category_24162120> query = entityManager.createQuery(jpql, Category_24162120.class);
            query.setParameter("catename", "%" + catname + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category_24162120> findAll(int page, int pagesize) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<Category_24162120> query =
                    entityManager.createNamedQuery("Category_24162120.findAll", Category_24162120.class);
            query.setFirstResult(page * pagesize);
            query.setMaxResults(pagesize);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public int count() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        String jpql = "SELECT COUNT(c) FROM Category_24162120 c";
        try {
            Query query = entityManager.createQuery(jpql);
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            entityManager.close();
        }
    }
}
