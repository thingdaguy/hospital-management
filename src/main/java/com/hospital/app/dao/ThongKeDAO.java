package com.hospital.app.dao;

import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO cho các chức năng thống kê (JPQL).
 */
public class ThongKeDAO {

    public long getTotalPatients() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(b) FROM BenhNhan b", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public long getTotalDoctors() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(d) FROM BacSi d", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách bệnh nhân chi trả nhiều nhất.
     * Trả về List<Object[]>: [0] = tên bệnh nhân, [1] = tổng tiền
     */
    public List<Object[]> getTopExpensivePatients(int limit) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT b.tenBenhNhan, SUM(h.tongTien) " +
                          "FROM HoaDon h JOIN h.benhNhan b " +
                          "GROUP BY b.maBenhNhan, b.tenBenhNhan " +
                          "ORDER BY SUM(h.tongTien) DESC";
            return em.createQuery(jpql, Object[].class)
                     .setMaxResults(limit)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách bác sĩ có nhiều bệnh nhân nhất.
     * Trả về List<Object[]>: [0] = tên bác sĩ, [1] = số lượng bệnh nhân
     */
    public List<Object[]> getTopDoctorsByPatients(int limit) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT d.tenBacSi, COUNT(b) " +
                          "FROM BenhNhan b JOIN b.bacSiTiepNhan d " +
                          "GROUP BY d.maBacSi, d.tenBacSi " +
                          "ORDER BY COUNT(b) DESC";
            return em.createQuery(jpql, Object[].class)
                     .setMaxResults(limit)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
