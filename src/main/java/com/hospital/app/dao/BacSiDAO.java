package com.hospital.app.dao;

import com.hospital.app.entity.BacSi;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO cho Bác Sĩ — cung cấp phương thức truy vấn và CRUD cơ bản.
 */
public class BacSiDAO {

    /**
     * Lấy danh sách toàn bộ bác sĩ kèm theo Khoa.
     */
    public List<BacSi> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT b FROM BacSi b LEFT JOIN FETCH b.khoa ORDER BY b.maBacSi";
            TypedQuery<BacSi> q = em.createQuery(jpql, BacSi.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm kiếm bác sĩ theo tên.
     */
    public List<BacSi> searchByTenContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT b FROM BacSi b LEFT JOIN FETCH b.khoa WHERE LOWER(b.tenBacSi) LIKE LOWER(:kw) ORDER BY b.maBacSi";
            TypedQuery<BacSi> q = em.createQuery(jpql, BacSi.class);
            q.setParameter("kw", "%" + keyword + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm bác sĩ theo ID.
     */
    public BacSi findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(BacSi.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Thêm mới bác sĩ.
     */
    public void save(BacSi bacSi) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(bacSi);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin bác sĩ.
     */
    public void update(BacSi bacSi) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(bacSi);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa bác sĩ theo ID.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BacSi b = em.find(BacSi.class, id);
            if (b != null) {
                em.remove(b);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
