package com.hospital.app.service;

import com.hospital.app.dao.BenhNhanDAO;
import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.entity.BenhNhan;

import com.hospital.app.entity.LuotDieuTri;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service bệnh nhân — orchestration cho Controller, map Entity → DTO.
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();

    /**
     * Lấy toàn bộ danh sách bệnh nhân chuyển đổi sang định dạng bảng (DTO).
     * Nạp sẵn thông tin Bác sĩ và Phòng bệnh liên quan.
     */
    public List<BenhNhanRowDTO> listAllForTable() {
        return benhNhanDAO.findAllWithBacSiAndPhong().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm bệnh nhân theo tên.
     * Nếu không nhập gì sẽ trả về tất cả.
     */
    public List<BenhNhanRowDTO> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return benhNhanDAO.searchByTenContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi thực thể BenhNhan sang BenhNhanRowDTO chuyên biệt cho giao diện bảng.
     * Lấy thông tin lượt điều trị mới nhất để hiển thị bác sĩ và phòng đang điều trị.
     */
    private BenhNhanRowDTO toRow(BenhNhan b) {
        LuotDieuTri currentLdt = null;

        // Logic tìm lượt điều trị hiện hành hoặc mới nhất
        if (b.getLuotDieuTris() != null && !b.getLuotDieuTris().isEmpty()) {
            currentLdt = b.getLuotDieuTris().stream()
                    .filter(l -> l.getNgayKetThuc() == null) // Ưu tiên lượt chưa xuất viện
                    .findFirst()
                    .orElse(b.getLuotDieuTris().stream()
                            .max(Comparator.comparing(LuotDieuTri::getNgayBatDau)) // Hoặc lượt mới nhất đã xong
                            .orElse(null));
        }

        // Ánh xạ các thông tin liên quan
        String tenBs = (currentLdt != null && currentLdt.getBacSiDieuTri() != null) ? currentLdt.getBacSiDieuTri().getTenBacSi() : "";
        String soTheBHYT = (b.getBaoHiem() != null) ? b.getBaoHiem().getMaThe() : "";

        // Chỉ hiển thị thông tin phòng nếu bệnh nhân thực sự ĐANG nằm viện
        boolean dangNamVien = (currentLdt != null && currentLdt.getNgayKetThuc() == null);
        String maPhong = (dangNamVien && currentLdt.getPhongBenh() != null) ? currentLdt.getPhongBenh().getMaPhong() : "";
        String loaiPhong = (dangNamVien && currentLdt.getPhongBenh() != null) ? currentLdt.getPhongBenh().getLoaiPhong() : "";

        return new BenhNhanRowDTO(
                b.getMaBenhNhan(),
                b.getTenBenhNhan(),
                b.getNgaySinh(),
                b.getSoDienThoai(),
                soTheBHYT,
                tenBs,
                maPhong,
                loaiPhong);
    }

    /**
     * Tìm bệnh nhân theo mã định danh (ID).
     */
    public BenhNhan findById(String id) {
        return benhNhanDAO.findById(id);
    }

    /**
     * Nghiệp vụ tiếp nhận bệnh nhân mới: Lưu BN + BHYT + Lượt điều trị đầu tiên.
     * Kiểm tra tính duy nhất của mã bệnh nhân báo lỗi nếu trùng.
     */
    public void saveWithEncounter(BenhNhan benhNhan, LuotDieuTri luotDieuTri, com.hospital.app.entity.BaoHiem baoHiem) {
        if (benhNhan == null) {
            throw new IllegalArgumentException("Dữ liệu bệnh nhân không hợp lệ.");
        }
        String maBn = benhNhan.getMaBenhNhan();
        if (maBn == null || maBn.isBlank()) {
            throw new IllegalArgumentException("Mã bệnh nhân không được để trống.");
        }
        // Kiểm tra xem mã BN đã tồn tại trong DB chưa
        if (benhNhanDAO.findById(maBn) != null) {
            throw new IllegalArgumentException("Mã bệnh nhân '" + maBn + "' đã tồn tại.");
        }
        
        // 1. Lưu thông tin cơ bản bệnh nhân
        benhNhanDAO.save(benhNhan);

        // 2. Lưu thẻ bảo hiểm nếu có thông tin (Kết nối 1-1)
        if (baoHiem != null) {
            baoHiem.setBenhNhan(benhNhan);
            new com.hospital.app.dao.BaoHiemDAO().save(baoHiem);
        }

        // 3. Khởi tạo lượt điều trị/tiếp nhận đầu tiên
        if (luotDieuTri != null) {
            luotDieuTri.setBenhNhan(benhNhan);
            new com.hospital.app.dao.LuotDieuTriDAO().save(luotDieuTri);
        }
    }

    /**
     * Lưu bệnh nhân kèm lượt điều trị (tương thích các trường hợp không có BHYT).
     */
    public void saveWithEncounter(BenhNhan benhNhan, LuotDieuTri luotDieuTri) {
        saveWithEncounter(benhNhan, luotDieuTri, null);
    }

    /**
     * Tra cứu nhanh lượt điều trị chưa xuất viện của bệnh nhân.
     */
    public LuotDieuTri findActiveLuotDieuTri(String maBenhNhan) {
        return new com.hospital.app.dao.LuotDieuTriDAO().findActiveByBenhNhan(maBenhNhan);
    }
    
    /**
     * Cập nhật các thay đổi liên quan đến lượt điều trị đang diễn ra.
     */
    public void updateActiveLuotDieuTri(LuotDieuTri luotDieuTri) {
        new com.hospital.app.dao.LuotDieuTriDAO().update(luotDieuTri);
    }

    /**
     * Lưu các cập nhật thông tin cá nhân của bệnh nhân.
     */
    public void update(BenhNhan benhNhan) {
        benhNhanDAO.update(benhNhan);
    }

    /**
     * Xóa hồ sơ bệnh nhân (Yêu cầu BN không còn ràng buộc hóa đơn/điều trị).
     */
    public void delete(String id) {
        benhNhanDAO.delete(id);
    }

    /**
     * Đếm số lượng bệnh nhân thực tế đang nằm trong một phòng bệnh.
     */
    public long countByPhong(String maPhong) {
        return new com.hospital.app.dao.LuotDieuTriDAO().countActiveByPhong(maPhong);
    }
}
