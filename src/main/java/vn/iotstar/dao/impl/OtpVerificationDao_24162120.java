package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dao.IOtpVerificationDao_24162120;
import vn.iotstar.entity.OtpVerification_24162120;

public class OtpVerificationDao_24162120 implements IOtpVerificationDao_24162120 {
    @Override
    public void insert(OtpVerification_24162120 otpVerification) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(otpVerification);
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
    public void markAllUnusedAsUsed(String email, String purpose) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.createQuery(
                            "UPDATE OtpVerification_24162120 o SET o.used = true WHERE LOWER(o.email) = LOWER(:email) AND o.purpose = :purpose AND o.used = false")
                    .setParameter("email", email)
                    .setParameter("purpose", purpose)
                    .executeUpdate();
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
    public OtpVerification_24162120 findLatest(String email, String purpose) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        try {
            TypedQuery<OtpVerification_24162120> query = entityManager.createQuery(
                    "SELECT o FROM OtpVerification_24162120 o LEFT JOIN FETCH o.user WHERE LOWER(o.email) = LOWER(:email) AND o.purpose = :purpose ORDER BY o.createdAt DESC",
                    OtpVerification_24162120.class);
            query.setParameter("email", email);
            query.setParameter("purpose", purpose);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void update(OtpVerification_24162120 otpVerification) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(otpVerification);
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
}
