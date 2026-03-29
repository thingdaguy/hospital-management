package com.hospital.app.dao;

import com.hospital.app.entity.PhongBenh;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO cho Phòng Bệnh — truy xuất và CRUD cơ bản.
 */
public class PhongBenhDAO {

    /**
     * Lấy danh sách toàn bộ phòng bệnh.
     */
    public List<PhongBenh> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM PhongBenh p ORDER BY p.maPhong";
            TypedQuery<PhongBenh> q = em.createQuery(jpql, PhongBenh.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm phòng bệnh theo loại phòng hoặc mã phòng.
     */
    public List<PhongBenh> searchByLoaiPhongContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM PhongBenh p WHERE LOWER(p.loaiPhong) LIKE LOWER(:kw) OR LOWER(p.maPhong) LIKE LOWER(:kw) ORDER BY p.maPhong";
            TypedQuery<PhongBenh> q = em.createQuery(jpql, PhongBenh.class);
            q.setParameter("kw", "%" + keyword + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm phòng.
     */
    public PhongBenh findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(PhongBenh.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Thêm phòng.
     */
    public void save(PhongBenh phongBenh) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(phongBenh);
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
     * Cập nhật thông tin phòng.
     */
    public void update(PhongBenh phongBenh) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(phongBenh);
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
     * Xóa phòng theo ID.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PhongBenh p = em.find(PhongBenh.class, id);
            if (p != null) {
                em.remove(p);
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
