package com.hospital.app.service;

import com.hospital.app.dao.DonThuocDAO;
import com.hospital.app.entity.ChiTietDonThuoc;
import com.hospital.app.entity.ChiTietDonThuocId;
import com.hospital.app.entity.DonThuoc;
import com.hospital.app.entity.LuotDieuTri;
import com.hospital.app.entity.Thuoc;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service xử lý nghiệp vụ kê đơn thuốc.
 */
public class DonThuocService {

    private final DonThuocDAO donThuocDAO = new DonThuocDAO();

    /**
     * Lấy dữ liệu đơn thuốc mới nhất của lượt điều trị để hiển thị lên UI.
     */
    public Map<String, Object> findLatestDataByLuotDieuTri(String maLuot) {
        DonThuoc dt = donThuocDAO.findLatestByLuotDieuTri(maLuot);
        if (dt == null) return null;

        Map<String, Integer> items = new HashMap<>();
        for (ChiTietDonThuoc ct : dt.getChiTietDonThuocs()) {
            items.put(ct.getThuoc().getMaThuoc(), ct.getSoLuong());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("ghiChu", dt.getGhiChu());
        result.put("items", items);
        return result;
    }

    /**
     * Tạo một đơn thuốc mới cho một lượt điều trị.
     * @param maLuot Mã lượt điều trị.
     * @param ghiChu Ghi chú đơn thuốc.
     * @param thuocQuantityMap Bản đồ chứa mã thuốc và số lượng tương ứng.
     */
    public void prescribe(String maLuot, String ghiChu, Map<String, Integer> thuocQuantityMap) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Tìm xem đã có đơn thuốc cho lượt này chưa
            String jpql = "SELECT d FROM DonThuoc d WHERE d.luotDieuTri.maLuot = :maLuot ORDER BY d.ngayKe DESC, d.maDon DESC";
            DonThuoc don = em.createQuery(jpql, DonThuoc.class)
                             .setParameter("maLuot", maLuot)
                             .setMaxResults(1)
                             .getResultStream().findFirst().orElse(null);

            if (don == null) {
                // Tạo mới nếu chưa có
                don = new DonThuoc();
                don.setMaDon("DT" + (System.currentTimeMillis() % 10000000L));
                don.setNgayKe(LocalDate.now());
                LuotDieuTri luot = em.find(LuotDieuTri.class, maLuot);
                if (luot == null) throw new RuntimeException("Không tìm thấy lượt điều trị.");
                don.setLuotDieuTri(luot);
                em.persist(don);
            } else {
                // Cập nhật ngày
                don.setNgayKe(LocalDate.now());
                // Xóa các chi tiết cũ (tận dụng orphanRemoval)
                don.getChiTietDonThuocs().clear();
                em.flush(); // Bắt buộc flush để orphanRemoval thực hiện DELETE trước khi INSERT
            }

            don.setGhiChu(ghiChu);

            // Thêm các chi tiết mới
            for (Map.Entry<String, Integer> entry : thuocQuantityMap.entrySet()) {
                String maThuoc = entry.getKey();
                Integer soLuong = entry.getValue();

                Thuoc t = em.find(Thuoc.class, maThuoc);
                if (t == null) throw new RuntimeException("Không tìm thấy thuốc mã: " + maThuoc);

                ChiTietDonThuoc ct = new ChiTietDonThuoc();
                ct.setId(new ChiTietDonThuocId(don.getMaDon(), maThuoc));
                ct.setDonThuoc(don);
                ct.setThuoc(t);
                ct.setSoLuong(soLuong);
                
                don.getChiTietDonThuocs().add(ct);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<DonThuoc> findByLuotDieuTri(String maLuot) {
        return donThuocDAO.findByLuotDieuTri(maLuot);
    }
}
