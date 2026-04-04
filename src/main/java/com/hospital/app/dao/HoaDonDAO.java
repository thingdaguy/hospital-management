package com.hospital.app.dao;

import com.hospital.app.entity.HoaDon;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import com.hospital.app.entity.LuotDieuTri;


/**
 * DAO thực hiện các truy vấn JPA liên quan đến Hóa Đơn.
 */
public class HoaDonDAO {

    /**
     * Truy xuất toàn bộ hóa đơn của bệnh nhân dựa vào mã số định danh.
     * Kết quả được sắp xếp theo thời gian lập hóa đơn (mới nhất lên đầu).
     */
    public List<HoaDon> findByMaBenhNhan(String maBenhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Lấy hóa đơn thông qua mối quan hệ bắc cầu: HoaDon -> LuotDieuTri -> BenhNhan
            String jpql = "SELECT h FROM HoaDon h WHERE h.luotDieuTri.benhNhan.maBenhNhan = :maBn ORDER BY h.ngayLap DESC";
            TypedQuery<HoaDon> q = em.createQuery(jpql, HoaDon.class);
            q.setParameter("maBn", maBenhNhan);
            return q.getResultList();
        } finally {
            // Giải phóng tài nguyên sau truy vấn
            em.close();
        }
    }

    /**
     * Tìm kiếm một hóa đơn cụ thể theo mã số hóa đơn (Mã ID).
     */
    public HoaDon findById(String maHoaDon) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // Ánh xạ thực thể cơ bản dựa trên khóa chính
            return em.find(HoaDon.class, maHoaDon);
        } finally {
            em.close();
        }
    }

    /**
     * Tìm hóa đơn kèm theo toàn bộ thông tin chi tiết (Lượt điều trị, BN, Bảo hiểm, Phòng).
     * Đặc biệt: Nạp sẵn các tập hợp (Collections) như Dịch vụ và Đơn thuốc để tránh lỗi Lazy Loading khi hiển thị.
     */
    public HoaDon findByIdWithDetails(String maHoaDon) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // JPQL: Sử dụng JOIN FETCH để nạp các thông tin quan trọng trong một lần SQL duy nhất
            String jpql = "SELECT h FROM HoaDon h " +
                          "INNER JOIN FETCH h.luotDieuTri l " +
                          "LEFT JOIN FETCH l.benhNhan b " +
                          "LEFT JOIN FETCH b.baoHiem " +
                          "LEFT JOIN FETCH l.phongBenh " +
                          "WHERE h.maHoaDon = :id";
            TypedQuery<HoaDon> q = em.createQuery(jpql, HoaDon.class);
            q.setParameter("id", maHoaDon);
            List<HoaDon> results = q.getResultList();
            if (results.isEmpty()) return null;
            
            HoaDon hd = results.get(0);

            // Kỹ thuật nạp cưỡng bức (Forced Initialization):
            // Truy cập vào size() của các tập hợp Lazy để Hibernate/JPA tải dữ liệu lên RAM trước khi đóng EntityManager
            LuotDieuTri l = hd.getLuotDieuTri();
            l.getChiTietDichVus().size(); // Nạp danh sách dịch vụ
            for (var cd : l.getChiTietDichVus()) cd.getDichVu().getTenDichVu(); // Nạp chi tiết tên dịch vụ
            
            l.getDonThuocs().size(); // Nạp danh sách đơn thuốc
            for (var dt : l.getDonThuocs()) {
                dt.getChiTietDonThuocs().size(); // Nạp chi tiết thuốc trong đơn
                for (var ctdt : dt.getChiTietDonThuocs()) ctdt.getThuoc().getTenThuoc();
            }
            return hd;
        } finally {
            em.close();
        }
    }

    /**
     * Lưu trữ hóa đơn mới được lập vào Cơ sở dữ liệu.
     */
    public void save(HoaDon hoaDon) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Bắt đầu giao dịch tài chính
            em.persist(hoaDon);          // Lưu dữ liệu hóa đơn
            em.getTransaction().commit(); // Xác nhận giao dịch thành công
        } catch (Exception e) {
            // Nếu có lỗi (lỗi DB, trùng mã...), quay về trạng thái trước đó
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Báo cáo lỗi cho tầng giao diện xử lý (hiển thị popup thông báo)
        } finally {
            em.close();
        }
    }
}
