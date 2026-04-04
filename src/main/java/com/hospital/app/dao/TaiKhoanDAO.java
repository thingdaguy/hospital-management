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
     * Tìm kiếm tài khoản dựa trên tên đăng nhập.
     * Hỗ trợ tìm kiếm không phân biệt hoa thường (Case-insensitive) để tăng trải nghiệm người dùng.
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) return null;

        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Sử dụng hàm LOWER() để so sánh chuỗi không phân biệt hoa thường
            String jpql = """
                    SELECT t FROM TaiKhoan t
                    WHERE LOWER(t.tenDangNhap) = LOWER(:u)
                    """;
            TypedQuery<TaiKhoan> q = em.createQuery(jpql, TaiKhoan.class);
            // Thiết lập tham số sau khi đã loại bỏ khoảng trắng thừa (trim)
            q.setParameter("u", tenDangNhap.trim());
            List<TaiKhoan> rows = q.getResultList();
            // Trả về tài khoản đầu tiên tìm thấy hoặc null nếu không tồn tại
            return rows.isEmpty() ? null : rows.get(0);
        } finally {
            // Giải phóng tài nguyên EntityManager
            em.close();
        }
    }

    /**
     * Ghi mới một thực thể tài khoản vào Cơ sở dữ liệu.
     */
    public void create(TaiKhoan tk) {
        if (tk == null) return;
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Bắt đầu một Transaction mới
            em.getTransaction().begin();
            // Đưa thực thể vào trạng thái quản lý (persist)
            em.persist(tk);
            // Xác nhận lưu trữ vĩnh viễn dữ liệu
            em.getTransaction().commit();
        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào (ví dụ: trùng khóa chính), thực hiện Rollback
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Chuyển tiếp lỗi để tầng Service xử lý
            throw e;
        } finally {
            em.close();
        }
    }
}

