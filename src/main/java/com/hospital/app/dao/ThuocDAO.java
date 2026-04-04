package com.hospital.app.dao;

import com.hospital.app.entity.Thuoc;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO Thuốc — Các thao tác CRUD và tìm kiếm JPQL cho thực thể Thuốc.
 */
public class ThuocDAO {

    /**
     * Lấy toàn bộ danh sách thuốc.
     */
    public List<Thuoc> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Thuoc t ORDER BY t.maThuoc";
            TypedQuery<Thuoc> q = em.createQuery(jpql, Thuoc.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm kiếm thuốc theo tên.
     */
    public List<Thuoc> searchByTenContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Thuoc t WHERE LOWER(t.tenThuoc) LIKE LOWER(:kw) ORDER BY t.maThuoc";
            TypedQuery<Thuoc> q = em.createQuery(jpql, Thuoc.class);
            q.setParameter("kw", "%" + keyword + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm thuốc theo ID.
     */
    public Thuoc findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Thuoc.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Lưu mới một loại thuốc.
     */
    public void save(Thuoc thuoc) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(thuoc);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin thuốc.
     */
    public void update(Thuoc thuoc) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(thuoc);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa thuốc theo ID.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Thuoc t = em.find(Thuoc.class, id);
            if (t != null) {
                em.remove(t);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
