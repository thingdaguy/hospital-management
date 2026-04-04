package com.hospital.app.dao;

import com.hospital.app.entity.BacSi;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO cho Bác Sĩ — cung cấp phương thức truy vấn và CRUD cơ bản.
 */
public class BacSiDAO {

    /**
     * Tìm nạp toàn bộ danh sách bác sĩ từ Database.
     * Sử dụng FETCH JOIN để lấy luôn thông tin Khoa, tránh lỗi LazyInitializationException.
     */
    public List<BacSi> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Truy vấn tất cả bác sĩ, đồng thời nạp thông tin khoa liên quan
            String jpql = "SELECT DISTINCT b FROM BacSi b LEFT JOIN FETCH b.khoa ORDER BY b.maBacSi";
            TypedQuery<BacSi> q = em.createQuery(jpql, BacSi.class);
            return q.getResultList();
        } finally {
            // Đảm bảo đóng EntityManager sau khi truy vấn xong
            em.close();
        }
    }

    /**
     * Tìm kiếm bác sĩ theo một phần tên (không phân biệt hoa thường).
     */
    public List<BacSi> searchByTenContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Tìm bác sĩ có tên chứa từ khóa, sử dụng LOWER để tìm kiếm linh hoạt
            String jpql = "SELECT DISTINCT b FROM BacSi b LEFT JOIN FETCH b.khoa WHERE LOWER(b.tenBacSi) LIKE LOWER(:kw) ORDER BY b.maBacSi";
            TypedQuery<BacSi> q = em.createQuery(jpql, BacSi.class);
            q.setParameter("kw", "%" + keyword + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy thông tin chi tiết một bác sĩ dựa trên mã định danh (ID).
     */
    public BacSi findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Tìm thực thể bác sĩ theo khóa chính
            return em.find(BacSi.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Lưu thông tin một bác sĩ mới vào Database.
     */
    public void save(BacSi bacSi) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Bắt đầu giao dịch tài chính/dữ liệu
            em.persist(bacSi);           // Lưu thực thể mới
            em.getTransaction().commit(); // Xác nhận thay đổi vào DB
        } catch (Exception e) {
            // Nếu có lỗi xảy ra, thực hiện quay xe (rollback) để tránh dữ liệu rác
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e; // Ném lỗi ra lớp Service để thông báo cho người dùng
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin hiện có của một bác sĩ.
     */
    public void update(BacSi bacSi) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(bacSi);             // Đồng bộ hóa các thay đổi của thực thể
            em.getTransaction().commit();
        } catch (Exception e) {
            // Xử lý lỗi: Hoàn tác nếu commit thất bại
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa hoàn toàn một bác sĩ khỏi hệ thống dựa trên ID.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Tìm lại bác sĩ để đảm bảo thực thể đang ở trạng thái managed trước khi xóa
            BacSi b = em.find(BacSi.class, id);
            if (b != null) {
                em.remove(b);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            // Hoàn tác nếu việc xóa bị vi phạm ràng buộc dữ liệu (ví dụ: đang có lượt điều trị)
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
