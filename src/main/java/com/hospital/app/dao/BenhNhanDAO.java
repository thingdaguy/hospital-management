package com.hospital.app.dao;

import com.hospital.app.entity.BenhNhan;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO bệnh nhân — truy vấn JPQL cho danh sách và (sau này) CRUD/tìm kiếm.
 */
public class BenhNhanDAO {

    /**
     * Lấy toàn bộ bệnh nhân kèm bác sĩ tiếp nhận và phòng (JOIN FETCH tránh N+1).
     */
    public List<BenhNhan> findAllWithBacSiAndPhong() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = """
                    SELECT DISTINCT b FROM BenhNhan b
                    LEFT JOIN FETCH b.bacSiTiepNhan
                    LEFT JOIN FETCH b.phongBenh
                    ORDER BY b.maBenhNhan
                    """;
            TypedQuery<BenhNhan> q = em.createQuery(jpql, BenhNhan.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * JPQL tìm theo tên (chứa chuỗi, không phân biệt hoa thường) — dùng cho ô tìm
     * kiếm.
     */
    public List<BenhNhan> searchByTenContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = """
                    SELECT DISTINCT b FROM BenhNhan b
                    LEFT JOIN FETCH b.bacSiTiepNhan
                    LEFT JOIN FETCH b.phongBenh
                    WHERE LOWER(b.tenBenhNhan) LIKE LOWER(:kw)
                    ORDER BY b.maBenhNhan
                    """;
            TypedQuery<BenhNhan> q = em.createQuery(jpql, BenhNhan.class);
            q.setParameter("kw", "%" + keyword.trim() + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm bệnh nhân theo mã (ID).
     */
    public BenhNhan findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(BenhNhan.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Thêm mới một bệnh nhân.
     */
    public void save(BenhNhan benhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (benhNhan.getBacSiTiepNhan() != null) {
                benhNhan.setBacSiTiepNhan(
                        em.getReference(com.hospital.app.entity.BacSi.class, benhNhan.getBacSiTiepNhan().getMaBacSi()));
            }
            if (benhNhan.getPhongBenh() != null) {
                benhNhan.setPhongBenh(
                        em.getReference(com.hospital.app.entity.PhongBenh.class, benhNhan.getPhongBenh().getMaPhong()));
            }
            em.persist(benhNhan);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin bệnh nhân.
     */
    public void update(BenhNhan benhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(benhNhan);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa bệnh nhân theo mã.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BenhNhan b = em.find(BenhNhan.class, id);
            if (b != null) {
                em.remove(b);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số lượng bệnh nhân đang ở trong một phòng.
     */
    public long countByPhong(String maPhong) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(b) FROM BenhNhan b WHERE b.phongBenh.maPhong = :maPhong";
            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("maPhong", maPhong);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }
}
