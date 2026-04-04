package com.hospital.app.service;

import com.hospital.app.dao.BacSiDAO;
import com.hospital.app.dto.BacSiRowDTO;
import com.hospital.app.entity.BacSi;

import java.util.List;
import java.util.stream.Collectors;

public class BacSiService {
    private final BacSiDAO bacSiDAO = new BacSiDAO();

    /**
     * Lấy danh sách toàn bộ bác sĩ (dạng Entity).
     */
    public List<BacSi> findAll() {
        return bacSiDAO.findAll();
    }

    /**
     * Tìm thông tin bác sĩ theo mã định danh (ID).
     */
    public BacSi findById(String id) {
        return bacSiDAO.findById(id);
    }

    /**
     * Lấy danh sách toàn bộ bác sĩ và chuyển đổi sang Row DTO để hiển thị trên bảng.
     */
    public List<BacSiRowDTO> listAllForTable() {
        return bacSiDAO.findAll().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm bác sĩ theo tên. Nếu từ khóa trống, trả về tất cả.
     */
    public List<BacSiRowDTO> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return bacSiDAO.searchByTenContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi từ thực thể BacSi sang BacSiRowDTO để tách biệt dữ liệu hiển thị.
     */
    private BacSiRowDTO toRow(BacSi b) {
        // Lấy tên khoa từ thực thể Khoa liên kết (tránh lỗi null)
        String tenKhoa = b.getKhoa() != null ? b.getKhoa().getTenKhoa() : "";
        return new BacSiRowDTO(
                b.getMaBacSi(),
                b.getTenBacSi(),
                b.getNgayVaoLam(),
                b.getChuyenMon(),
                tenKhoa
        );
    }

    /**
     * Lưu mới thông tin một bác sĩ vào Cơ sở dữ liệu.
     */
    public void save(BacSi bacSi) {
        bacSiDAO.save(bacSi);
    }

    /**
     * Cập nhật thông tin bác sĩ hiện có.
     */
    public void update(BacSi bacSi) {
        bacSiDAO.update(bacSi);
    }

    /**
     * Xóa bác sĩ khỏi hệ thống dựa trên ID.
     */
    public void delete(String id) {
        bacSiDAO.delete(id);
    }
}
