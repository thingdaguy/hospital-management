package com.hospital.app.dao;

import com.hospital.app.entity.LuotDieuTri;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO phục vụ các thao tác cơ sở dữ liệu đối với Lượt điều trị.
 */
public class LuotDieuTriDAO {

    /**
     * Lưu mới một lượt điều trị vào Database.
     * Sử dụng để khởi tạo quá trình nhập viện/khám bệnh của bệnh nhân.
     */
    public void save(LuotDieuTri luotDieuTri) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Mở giao dịch
            em.persist(luotDieuTri);      // Lưu đối tượng
            em.getTransaction().commit(); // Xác nhận lưu
        } catch (Exception e) {
            // Nếu có lỗi (vi phạm ràng buộc, lỗi kết nối...), thực hiện Rollback
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Ném lỗi để tầng UI hiển thị thông báo
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin của lượt điều trị (ví dụ: cập nhật ngày kết thúc khi xuất viện).
     */
    public void update(LuotDieuTri luotDieuTri) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(luotDieuTri);        // Cập nhật các thay đổi
            em.getTransaction().commit();
        } catch (Exception e) {
            // Hoàn tác nếu quá trình cập nhật bị lỗi
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm một lượt điều trị theo mã ID.
     */
    public LuotDieuTri findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(LuotDieuTri.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Tìm lượt điều trị kèm theo tất cả thông tin liên quan (BN, BS, Phòng).
     * Sử dụng JOIN FETCH để nạp dữ liệu ngay lập tức, tối ưu hiệu năng.
     */
    public LuotDieuTri findByIdWithDetails(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Kết hợp các bảng liên quan để lấy đầy đủ thông tin trong 1 lần truy vấn
            String jpql = "SELECT l FROM LuotDieuTri l " +
                          "JOIN FETCH l.benhNhan " +
                          "JOIN FETCH l.bacSiDieuTri " +
                          "LEFT JOIN FETCH l.phongBenh " +
                          "WHERE l.maLuot = :id";
            TypedQuery<LuotDieuTri> query = em.createQuery(jpql, LuotDieuTri.class);
            query.setParameter("id", id);
            List<LuotDieuTri> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số lượt điều trị đang "hoạt động" (chưa kết thúc) trong một phòng cụ thể.
     * Dùng để kiểm tra sức chứa của phòng bệnh.
     */
    public long countActiveByPhong(String maPhong) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Đếm các lượt có mã phòng tương ứng và chưa có ngày kết thúc
            String jpql = "SELECT COUNT(l) FROM LuotDieuTri l WHERE l.phongBenh.maPhong = :maPhong AND l.ngayKetThuc IS NULL";
            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("maPhong", maPhong);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy toàn bộ danh sách lượt điều trị hiện có trong hệ thống, kèm chi tiết.
     * Sắp xếp theo ngày bắt đầu mới nhất lên đầu.
     */
    public List<LuotDieuTri> findAllWithDetails() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lấy danh sách lượt điều trị, JOIN nạp sẵn các thực thể liên quan
            String jpql = "SELECT l FROM LuotDieuTri l " +
                          "JOIN FETCH l.benhNhan " +
                          "JOIN FETCH l.bacSiDieuTri " +
                          "LEFT JOIN FETCH l.phongBenh " +
                          "ORDER BY l.ngayBatDau DESC";
            return em.createQuery(jpql, LuotDieuTri.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm lượt điều trị đang diễn ra của một bệnh nhân cụ thể.
     */
    public LuotDieuTri findActiveByBenhNhan(String maBenhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Tìm lượt điều trị của BN chưa có ngày kết thúc (đang nằm viện)
            String jpql = "SELECT l FROM LuotDieuTri l " +
                          "JOIN FETCH l.benhNhan " +
                          "JOIN FETCH l.bacSiDieuTri " +
                          "LEFT JOIN FETCH l.phongBenh " +
                          "WHERE l.benhNhan.maBenhNhan = :maBn AND l.ngayKetThuc IS NULL";
            TypedQuery<LuotDieuTri> query = em.createQuery(jpql, LuotDieuTri.class);
            query.setParameter("maBn", maBenhNhan);
            List<LuotDieuTri> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
    
    /**
     * Tìm kiếm lượt điều trị theo tên bệnh nhân.
     */
    public List<LuotDieuTri> searchByTenBenhNhan(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lọc danh sách lượt điều trị theo tên BN chứa từ khóa
            String jpql = "SELECT l FROM LuotDieuTri l " +
                          "JOIN FETCH l.benhNhan b " +
                          "JOIN FETCH l.bacSiDieuTri " +
                          "LEFT JOIN FETCH l.phongBenh " +
                          "WHERE b.tenBenhNhan LIKE :kw " +
                          "ORDER BY l.ngayBatDau DESC";
            return em.createQuery(jpql, LuotDieuTri.class)
                     .setParameter("kw", "%" + keyword + "%")
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
