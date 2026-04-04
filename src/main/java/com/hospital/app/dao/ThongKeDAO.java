package com.hospital.app.dao;

import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO thống kê — tập hợp các JPQL chuyên dụng phục vụ Dashboard.
 * Hỗ trợ các truy vấn linh hoạt theo ngày và theo khoảng thời gian lũy kế.
 */
public class ThongKeDAO {

    /**
     * Truy xuất tổng số lượng bệnh nhân đã từng đăng ký trong hệ thống.
     */
    public long getTotalPatients() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Đếm tất cả bản ghi từ thực thể BenhNhan
            return em.createQuery("SELECT COUNT(b) FROM BenhNhan b", Long.class)
                     .getSingleResult();
        } finally { em.close(); }
    }

    /**
     * Truy xuất tổng số lượng bác sĩ hiện có.
     */
    public long getTotalDoctors() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Đếm tất cả thực thể BacSi
            return em.createQuery("SELECT COUNT(d) FROM BacSi d", Long.class)
                     .getSingleResult();
        } finally { em.close(); }
    }

    /**
     * Tính toán tổng doanh thu thực tế trong một khoảng thời gian xác định.
     * @param from Ngày bắt đầu.
     * @param to Ngày kết thúc.
     * @return Tổng số tiền (BigDecimal).
     */
    public BigDecimal getRevenueInRange(LocalDate from, LocalDate to) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Tính tổng trường tongTien của các hóa đơn có ngày lập nằm trong [from, to]
            // Sử dụng COALESCE để đảm bảo không trả về null nếu không có hóa đơn nào
            BigDecimal result = em.createQuery(
                    "SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h " +
                    "WHERE h.ngayLap >= :from AND h.ngayLap <= :to", BigDecimal.class)
                     .setParameter("from", from)
                     .setParameter("to",   to)
                     .getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally { em.close(); }
    }

    /**
     * Truy xuất doanh thu phát sinh duy nhất trong một ngày cụ thể.
     */
    public BigDecimal getRevenueOnDate(LocalDate date) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lọc hóa đơn theo ngày lập chính xác
            BigDecimal result = em.createQuery(
                    "SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h " +
                    "WHERE h.ngayLap = :date", BigDecimal.class)
                     .setParameter("date", date)
                     .getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally { em.close(); }
    }

    /**
     * Lấy danh sách Top BN có tổng chi trả cao nhất trong kỳ.
     * @param limit Số lượng bản ghi cần lấy (vd Top 5).
     */
    public List<Object[]> getTopExpensivePatientsInRange(LocalDate from, LocalDate to, int limit) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Kết hợp bảng HoaDon, LuotDieuTri, BenhNhan.
            // Gom nhóm theo mã BN và sắp xếp theo tổng doanh thu giảm dần.
            String jpql = "SELECT b.tenBenhNhan, SUM(h.tongTien) " +
                          "FROM HoaDon h " +
                          "JOIN h.luotDieuTri l " +
                          "JOIN l.benhNhan b " +
                          "WHERE h.ngayLap >= :from AND h.ngayLap <= :to " +
                          "GROUP BY b.maBenhNhan, b.tenBenhNhan " +
                          "ORDER BY SUM(h.tongTien) DESC";
            return em.createQuery(jpql, Object[].class)
                     .setParameter("from", from).setParameter("to", to)
                     .setMaxResults(limit)
                     .getResultList();
        } finally { em.close(); }
    }

    /**
     * Lấy danh sách Top Bác sĩ có lượt tiếp nhận nhiều nhất trong kỳ.
     */
    public List<Object[]> getTopDoctorsByEncountersInRange(LocalDate from, LocalDate to, int limit) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Đếm số lượng thực thể LuotDieuTri ứng với từng bác sĩ
            String jpql = "SELECT d.tenBacSi, COUNT(l) " +
                          "FROM LuotDieuTri l " +
                          "JOIN l.bacSiDieuTri d " +
                          "WHERE l.ngayBatDau >= :from AND l.ngayBatDau <= :to " +
                          "GROUP BY d.maBacSi, d.tenBacSi " +
                          "ORDER BY COUNT(l) DESC";
            return em.createQuery(jpql, Object[].class)
                     .setParameter("from", from).setParameter("to", to)
                     .setMaxResults(limit)
                     .getResultList();
        } finally { em.close(); }
    }

    /**
     * Danh sách bệnh nhân đang nằm viện tại một thời điểm cụ thể.
     * Logic: BN có ngày vào viện <= ngày chọn VÀ (chưa ra viện HOẶC ngày ra viện > ngày chọn).
     */
    public List<Object[]> getInTreatmentOnDate(LocalDate date) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lọc các lượt điều trị còn hiệu lực tại thời điểm 'date'
            String jpql = "SELECT b.tenBenhNhan, d.tenBacSi " +
                          "FROM LuotDieuTri l " +
                          "JOIN l.benhNhan b " +
                          "JOIN l.bacSiDieuTri d " +
                          "WHERE l.ngayBatDau <= :date " +
                          "  AND (l.ngayKetThuc IS NULL OR l.ngayKetThuc > :date) " +
                          "ORDER BY b.tenBenhNhan";
            return em.createQuery(jpql, Object[].class)
                     .setParameter("date", date)
                     .getResultList();
        } finally { em.close(); }
    }
}
