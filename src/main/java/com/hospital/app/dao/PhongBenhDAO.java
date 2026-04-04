package com.hospital.app.dao;

import com.hospital.app.entity.PhongBenh;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO cho Phòng Bệnh — truy xuất và CRUD cơ bản.
 */
public class PhongBenhDAO {

    /**
     * Lấy danh sách toàn bộ phòng bệnh hiện có trong Database.
     */
    public List<PhongBenh> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Truy xuất tất cả các phòng bệnh, sắp xếp theo mã phòng
            String jpql = "SELECT p FROM PhongBenh p ORDER BY p.maPhong";
            TypedQuery<PhongBenh> q = em.createQuery(jpql, PhongBenh.class);
            return q.getResultList();
        } finally {
            // Giải phóng tài nguyên EntityManager
            em.close();
        }
    }

    /**
     * Tìm kiếm phòng bệnh theo loại phòng hoặc mã phòng (không phân biệt hoa thường).
     */
    public List<PhongBenh> searchByLoaiPhongContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Tìm phòng dựa trên mã hoặc loại phòng (Vip, Thường,...)
            String jpql = "SELECT p FROM PhongBenh p WHERE LOWER(p.loaiPhong) LIKE LOWER(:kw) OR LOWER(p.maPhong) LIKE LOWER(:kw) ORDER BY p.maPhong";
            TypedQuery<PhongBenh> q = em.createQuery(jpql, PhongBenh.class);
            q.setParameter("kw", "%" + keyword + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy chi tiết thông tin một phòng bệnh theo mã ID.
     */
    public PhongBenh findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Ánh xạ trực tiếp từ Khóa chính (Mã phòng)
            return em.find(PhongBenh.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Thêm một danh mục phòng bệnh mới vào hệ thống.
     */
    public void save(PhongBenh phongBenh) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Bắt đầu phiên làm việc
            em.persist(phongBenh);      // Ghi nhận thực thể mới
            em.getTransaction().commit(); // Lưu vĩnh viễn vào DB
        } catch (Exception e) {
            // Nếu trùng mã hoặc lỗi hệ thống, thực hiện Rollback
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e; // Báo lỗi cho tầng nghiệp vụ
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin phòng bệnh (số giường, đơn giá, v.v.).
     */
    public void update(PhongBenh phongBenh) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(phongBenh);         // Hợp nhất dữ liệu thay đổi
            em.getTransaction().commit();
        } catch (Exception e) {
            // Hoàn tác nếu việc cập nhật thất bại
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa phòng bệnh khỏi danh sách.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Truy tìm thực thể phòng cần xóa
            PhongBenh p = em.find(PhongBenh.class, id);
            if (p != null) {
                em.remove(p);            // Thực hiện xóa
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            // Lỗi xảy ra nếu phòng này đang có bệnh nhân điều trị (vi phạm ràng buộc)
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
