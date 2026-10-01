package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dao.IUserAccountDao_24162120;
import vn.iotstar.entity.UserAccount_24162120;

public class UserAccountDao_24162120 implements IUserAccountDao_24162120 {
    @Override
    public void insert(UserAccount_24162120 user) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
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
    public void update(UserAccount_24162120 user) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(user);
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
    public void delete(Long id) throws Exception {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            UserAccount_24162120 user = entityManager.find(UserAccount_24162120.class, id);
            if (user == null) {
                throw new Exception("No user was found for id = " + id);
            }
            entityManager.createQuery("DELETE FROM OtpVerification_24162120 o WHERE o.user.userId = :userId")
                    .setParameter("userId", id)
                    .executeUpdate();
            entityManager.remove(user);
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
    public UserAccount_24162120 findById(Long id) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            return entityManager.find(UserAccount_24162120.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public UserAccount_24162120 findByEmail(String email) {
        return findSingle("SELECT u FROM UserAccount_24162120 u WHERE LOWER(u.email) = LOWER(:value)", email);
    }

    @Override
    public UserAccount_24162120 findByUsername(String username) {
        return findSingle("SELECT u FROM UserAccount_24162120 u WHERE LOWER(u.username) = LOWER(:value)", username);
    }

    @Override
    public UserAccount_24162120 findByUsernameOrEmail(String value) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<UserAccount_24162120> query = entityManager.createQuery(
                    "SELECT u FROM UserAccount_24162120 u WHERE LOWER(u.username) = LOWER(:value) OR LOWER(u.email) = LOWER(:value)",
                    UserAccount_24162120.class);
            query.setParameter("value", value);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<UserAccount_24162120> findAll() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            return entityManager.createNamedQuery("UserAccount_24162120.findAll", UserAccount_24162120.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<UserAccount_24162120> search(String keyword) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<UserAccount_24162120> query = entityManager.createQuery(
                    "SELECT u FROM UserAccount_24162120 u " +
                            "WHERE LOWER(u.fullName) LIKE LOWER(:keyword) " +
                            "OR LOWER(u.username) LIKE LOWER(:keyword) " +
                            "OR LOWER(u.email) LIKE LOWER(:keyword) " +
                            "OR LOWER(u.roleName) LIKE LOWER(:keyword) " +
                            "ORDER BY u.userId DESC",
                    UserAccount_24162120.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    private UserAccount_24162120 findSingle(String jpql, String value) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<UserAccount_24162120> query = entityManager.createQuery(jpql, UserAccount_24162120.class);
            query.setParameter("value", value);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }
}
