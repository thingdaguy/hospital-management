package com.hospital.app.service;

import com.hospital.app.dao.BenhNhanDAO;
import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.entity.BenhNhan;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service bệnh nhân — orchestration cho Controller, map Entity → DTO.
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();

    /**
     * Danh sách tất cả bệnh nhân để hiển thị bảng.
     */
    public List<BenhNhanRowDTO> listAllForTable() {
        return benhNhanDAO.findAllWithBacSiAndPhong().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Tìm theo tên (rỗng = trả về toàn bộ).
     */
    public List<BenhNhanRowDTO> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return benhNhanDAO.searchByTenContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    private BenhNhanRowDTO toRow(BenhNhan b) {
        String tenBs = b.getBacSiTiepNhan() != null ? b.getBacSiTiepNhan().getTenBacSi() : "";
        String maPhong = b.getPhongBenh() != null ? b.getPhongBenh().getMaPhong() : "";
        String loaiPhong = b.getPhongBenh() != null ? b.getPhongBenh().getLoaiPhong() : "";
        return new BenhNhanRowDTO(
                b.getMaBenhNhan(),
                b.getTenBenhNhan(),
                b.getNgaySinh(),
                b.getSoDienThoai(),
                tenBs,
                maPhong,
                loaiPhong
        );
    }
}
