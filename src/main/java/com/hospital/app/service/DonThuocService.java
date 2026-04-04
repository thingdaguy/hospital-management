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
import java.util.List;
import java.util.Map;

/**
 * Service xử lý nghiệp vụ kê đơn thuốc.
 */
public class DonThuocService {

    private final DonThuocDAO donThuocDAO = new DonThuocDAO();

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

            // 1. Tìm lượt điều trị
            LuotDieuTri luot = em.find(LuotDieuTri.class, maLuot);
            if (luot == null) throw new RuntimeException("Không tìm thấy lượt điều trị.");

            // 2. Tạo đơn thuốc
            DonThuoc don = new DonThuoc();
            don.setMaDon("DT" + (System.currentTimeMillis() % 10000000L));
            don.setNgayKe(LocalDate.now());
            don.setGhiChu(ghiChu);
            don.setLuotDieuTri(luot);

            em.persist(don);

            // 3. Thêm chi tiết đơn thuốc
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

                em.persist(ct);
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
