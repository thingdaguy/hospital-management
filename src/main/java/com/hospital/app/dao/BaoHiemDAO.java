package com.hospital.app.dao;

import com.hospital.app.entity.BaoHiem;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;

/**
 * DAO thực hiện các thao tác JPA liên quan đến Bảo Hiểm Y Tế.
 */
public class BaoHiemDAO {

    /**
     * Ghi mới dữ liệu Bảo Hiểm Y Tế vào hệ thống.
     * Lưu ý: Thực thể BenhNhan phải tồn tại trước đó để đảm bảo tính toàn vẹn tham chiếu.
     */
    public void save(BaoHiem baoHiem) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Khởi tạo tiến trình giao dịch (Transaction)
            em.getTransaction().begin();
            // Lưu đối tượng xuống CSDL
            em.persist(baoHiem);
            // Xác nhận hoàn tất giao dịch
            em.getTransaction().commit();
        } catch (Exception e) {
            // Xử lý khi có lỗi xảy ra: Hoàn tác (Rollback) để bảo vệ dữ liệu
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Ném lỗi lên tầng Service xử lý hiển thị
            throw e;
        } finally {
            // Đảm bảo đóng EntityManager để giải phóng tài nguyên hệ thống
            em.close();
        }
    }

    /**
     * Cập nhật thông tin thẻ bảo hiểm đã được khai báo trước đó.
     */
    public void update(BaoHiem baoHiem) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Sử dụng merge để đồng bộ các thay đổi vào thực thể đang quản lý
            em.merge(baoHiem);
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
}
