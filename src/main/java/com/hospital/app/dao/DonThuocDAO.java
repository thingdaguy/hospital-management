package com.hospital.app.dao;

import com.hospital.app.entity.DonThuoc;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO Đơn thuốc — Quản lý việc lưu và truy vấn đơn thuốc.
 */
public class DonThuocDAO {

    /**
     * Lưu mới một đơn thuốc.
     */
    public void save(DonThuoc donThuoc) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(donThuoc);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm các đơn thuốc thuộc về một lượt điều trị nhất định.
     */
    public List<DonThuoc> findByLuotDieuTri(String maLuot) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM DonThuoc d WHERE d.luotDieuTri.maLuot = :maLuot ORDER BY d.ngayKe DESC";
            TypedQuery<DonThuoc> q = em.createQuery(jpql, DonThuoc.class);
            q.setParameter("maLuot", maLuot);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm đơn thuốc mới nhất của một lượt điều trị, kèm theo chi tiết.
     */
    public DonThuoc findLatestByLuotDieuTri(String maLuot) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM DonThuoc d " +
                          "LEFT JOIN FETCH d.chiTietDonThuocs ct " +
                          "LEFT JOIN FETCH ct.thuoc " +
                          "WHERE d.luotDieuTri.maLuot = :maLuot " +
                          "ORDER BY d.ngayKe DESC, d.maDon DESC";
            TypedQuery<DonThuoc> q = em.createQuery(jpql, DonThuoc.class);
            q.setParameter("maLuot", maLuot);
            q.setMaxResults(1);
            List<DonThuoc> results = q.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Tìm đơn thuốc kèm theo chi tiết thuốc (FETCH JOIN).
     */
    public DonThuoc findByIdWithDetails(String maDon) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM DonThuoc d LEFT JOIN FETCH d.chiTietDonThuocs ct LEFT JOIN FETCH ct.thuoc WHERE d.maDon = :maDon";
            TypedQuery<DonThuoc> q = em.createQuery(jpql, DonThuoc.class);
            q.setParameter("maDon", maDon);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
}
