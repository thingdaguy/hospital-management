package com.hospital.app.service;

import com.hospital.app.dao.PhongBenhDAO;
import com.hospital.app.dto.PhongBenhRowDTO;
import com.hospital.app.entity.PhongBenh;

import java.util.List;
import java.util.stream.Collectors;

public class PhongBenhService {
    private final PhongBenhDAO phongBenhDAO = new PhongBenhDAO();

    /**
     * Lấy danh sách toàn bộ phòng bệnh (dạng Entity).
     */
    public List<PhongBenh> findAll() {
        return phongBenhDAO.findAll();
    }

    /**
     * Tìm phòng bệnh theo mã định danh (ID).
     */
    public PhongBenh findById(String id) {
        return phongBenhDAO.findById(id);
    }

    /**
     * Lấy danh sách phòng bệnh chuyển đổi sang Row DTO để hiển thị lên bảng.
     */
    public List<PhongBenhRowDTO> listAllForTable() {
        return phongBenhDAO.findAll().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm phòng theo loại phòng hoặc mã phòng.
     */
    public List<PhongBenhRowDTO> searchByLoaiPhong(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return phongBenhDAO.searchByLoaiPhongContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Entity sang DTO để hiển thị rút gọn thông tin phòng.
     */
    private PhongBenhRowDTO toRow(PhongBenh p) {
        return new PhongBenhRowDTO(
                p.getMaPhong(),
                p.getLoaiPhong(),
                p.getSoGiuongToiDa()
        );
    }

    /**
     * Thêm mới một phòng bệnh vào hệ thống.
     */
    public void save(PhongBenh phongBenh) {
        phongBenhDAO.save(phongBenh);
    }

    /**
     * Cập nhật thông tin phòng bệnh.
     * Logic: Không cho phép hạ số giường tối đa xuống thấp hơn số bệnh nhân hiện có trong phòng.
     */
    public void update(PhongBenh phongBenh) {
        // Kiểm tra số lượng bệnh nhân thực tế đang nằm tại phòng này
        long currentOccupancy = new com.hospital.app.service.BenhNhanService().countByPhong(phongBenh.getMaPhong());
        if (phongBenh.getSoGiuongToiDa() != null && phongBenh.getSoGiuongToiDa() < currentOccupancy) {
            // Ngăn chặn hành động nếu vi phạm quy tắc về sức chứa
            throw new IllegalArgumentException("Số giường tối đa không thể nhỏ hơn số bệnh nhân hiện đang ở trong phòng (" + currentOccupancy + ")");
        }
        phongBenhDAO.update(phongBenh);
    }

    /**
     * Xóa phòng bệnh khỏi hệ thống.
     * Logic: Chỉ xóa được nếu phòng hiện không có bệnh nhân nào đang điều trị.
     */
    public void delete(String id) {
        // Kiểm tra xem phòng có còn người ở hay không
        long occupied = new com.hospital.app.service.BenhNhanService().countByPhong(id);
        if (occupied > 0) {
            // Chặn xóa để tránh lỗi ràng buộc dữ liệu (Bệnh nhân không có phòng)
            throw new IllegalArgumentException("Không thể xóa phòng vì còn " + occupied + " bệnh nhân đang ở trong phòng.");
        }
        phongBenhDAO.delete(id);
    }
}
