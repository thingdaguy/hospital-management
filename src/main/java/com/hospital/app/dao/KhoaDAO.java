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
     * Truy xuất toàn bộ danh sách các Khoa hiện có trong hệ thống.
     * Dữ liệu được sắp xếp theo tên khoa để hiển thị thuận tiện trên UI.
     */
    public List<Khoa> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lấy thực thể Khoa và sắp xếp theo tên (ASC mặc định)
            String jpql = "SELECT k FROM Khoa k ORDER BY k.tenKhoa";
            TypedQuery<Khoa> q = em.createQuery(jpql, Khoa.class);
            return q.getResultList();
        } finally {
            // Đảm bảo đóng kết nối để tối ưu bộ nhớ
            em.close();
        }
    }
}
