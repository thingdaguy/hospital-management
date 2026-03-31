package com.hospital.app.dao;

import com.hospital.app.entity.TaiKhoan;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO tai khoan dang nhap.
 */
public class TaiKhoanDAO {

    /**
     * Tim tai khoan theo ten dang nhap (khong phan biet hoa thuong).
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) return null;

        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = """
                    SELECT t FROM TaiKhoan t
                    WHERE LOWER(t.tenDangNhap) = LOWER(:u)
                    """;
            TypedQuery<TaiKhoan> q = em.createQuery(jpql, TaiKhoan.class);
            q.setParameter("u", tenDangNhap.trim());
            List<TaiKhoan> rows = q.getResultList();
            return rows.isEmpty() ? null : rows.get(0);
        } finally {
            em.close();
        }
    }
}

