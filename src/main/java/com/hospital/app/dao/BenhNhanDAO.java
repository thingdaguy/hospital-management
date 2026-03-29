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
     * JPQL tìm theo tên (chứa chuỗi, không phân biệt hoa thường) — dùng cho ô tìm kiếm.
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
}
