package com.hospital.app.dao;

import com.hospital.app.entity.Khoa;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO cho Khoa — truy cập bảng khoa trong hệ thống.
 */
public class KhoaDAO {

    /**
     * Lấy danh sách toàn bộ khoa phục vụ cho ComboBox.
     */
    public List<Khoa> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT k FROM Khoa k ORDER BY k.tenKhoa";
            TypedQuery<Khoa> q = em.createQuery(jpql, Khoa.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}
