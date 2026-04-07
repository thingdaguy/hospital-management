package com.hospital.app.dao;

import com.hospital.app.entity.BenhNhan;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO bệnh nhân — truy vấn JPQL cho danh sách và các thao tác CRUD.
 */
public class BenhNhanDAO {

    /**
     * Tìm nạp toàn bộ danh sách bệnh nhân kèm theo Bảo Hiểm.
     * Sử dụng FETCH JOIN để tránh lỗi N+1 query.
     */
    public List<BenhNhan> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lấy bệnh nhân và nạp sẵn thông tin bảo hiểm
            String jpql = "SELECT DISTINCT b FROM BenhNhan b LEFT JOIN FETCH b.baoHiem ORDER BY b.maBenhNhan";
            TypedQuery<BenhNhan> q = em.createQuery(jpql, BenhNhan.class);
            return q.getResultList();
        } finally {
            // Đảm bảo đóng EntityManager để giải phóng tài nguyên
            em.close();
        }
    }

    /**
     * Lấy toàn bộ bệnh nhân kèm bác sĩ tiếp nhận và phòng (JOIN FETCH tránh N+1).
     * Dùng cho các chức năng cần thông tin chi tiết lượt điều trị.
     */
    public List<BenhNhan> findAllWithBacSiAndPhong() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Truy vấn lồng để nạp sẵn các thực thể liên quan qua Lượt điều trị
            String jpql = "SELECT DISTINCT b FROM BenhNhan b " +
                          "LEFT JOIN FETCH b.baoHiem " +
                          "LEFT JOIN FETCH b.luotDieuTris ldt " +
                          "LEFT JOIN FETCH ldt.bacSiDieuTri " +
                          "LEFT JOIN FETCH ldt.phongBenh " +
                          "ORDER BY b.maBenhNhan";
            TypedQuery<BenhNhan> q = em.createQuery(jpql, BenhNhan.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm kiếm bệnh nhân theo tên (không phân biệt hoa thường).
     */
    public List<BenhNhan> searchByTenContaining(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Tìm kiếm mờ theo tên bệnh nhân, kết hợp nạp thông tin bảo hiểm
            String jpql = "SELECT DISTINCT b FROM BenhNhan b " +
                          "LEFT JOIN FETCH b.baoHiem " +
                          "LEFT JOIN FETCH b.luotDieuTris ldt " +
                          "LEFT JOIN FETCH ldt.bacSiDieuTri " +
                          "LEFT JOIN FETCH ldt.phongBenh " +
                          "WHERE LOWER(b.tenBenhNhan) LIKE LOWER(:kw) " +
                          "ORDER BY b.maBenhNhan";
            TypedQuery<BenhNhan> q = em.createQuery(jpql, BenhNhan.class);
            q.setParameter("kw", "%" + keyword + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lấy thông tin bệnh nhân theo ID.
     */
    public BenhNhan findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Tìm thực thể trực tiếp bằng khóa chính
            return em.find(BenhNhan.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Lưu mới một bệnh nhân vào hệ thống.
     */
    public void save(BenhNhan benhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Mở giao dịch
            em.persist(benhNhan);        // Lưu thực thể
            em.getTransaction().commit(); // Xác nhận lưu
        } catch (Exception e) {
            // Nếu có lỗi, thực hiện quay xe (rollback) giao dịch
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin bệnh nhân hiện tại.
     */
    public void update(BenhNhan benhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(benhNhan);          // Hợp nhất thay đổi
            em.getTransaction().commit();
        } catch (Exception e) {
            // Xử lý lỗi: Hoàn tác nếu cập nhật thất bại
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa bệnh nhân khỏi hệ thống dựa theo ID.
     */
    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BenhNhan b = em.find(BenhNhan.class, id);
            if (b != null) {
                // Kiểm tra xem bệnh nhân có lượt điều trị nào không
                Long ldtCount = em.createQuery("SELECT COUNT(l) FROM LuotDieuTri l WHERE l.benhNhan.maBenhNhan = :id", Long.class)
                                  .setParameter("id", id)
                                  .getSingleResult();
                if (ldtCount > 0) {
                    throw new RuntimeException("Bệnh nhân đã có lịch sử khám/điều trị. Không thể xóa dữ liệu!");
                }
                
                // Xóa bảo hiểm y tế nếu có
                em.createQuery("DELETE FROM BaoHiem bh WHERE bh.benhNhan.maBenhNhan = :id")
                  .setParameter("id", id)
                  .executeUpdate();

                em.remove(b);            // Xóa thực thể
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            // Lỗi có thể xảy ra nếu BN đang có dữ liệu liên quan ở bảng khác
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
