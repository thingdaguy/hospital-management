package com.hospital.app.service;

import com.hospital.app.dao.PhongBenhDAO;
import com.hospital.app.dto.PhongBenhRowDTO;
import com.hospital.app.entity.PhongBenh;

import java.util.List;
import java.util.stream.Collectors;

public class PhongBenhService {
    private final PhongBenhDAO phongBenhDAO = new PhongBenhDAO();

    public List<PhongBenh> findAll() {
        return phongBenhDAO.findAll();
    }

    public PhongBenh findById(String id) {
        return phongBenhDAO.findById(id);
    }

    public List<PhongBenhRowDTO> listAllForTable() {
        return phongBenhDAO.findAll().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    public List<PhongBenhRowDTO> searchByLoaiPhong(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return phongBenhDAO.searchByLoaiPhongContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    private PhongBenhRowDTO toRow(PhongBenh p) {
        return new PhongBenhRowDTO(
                p.getMaPhong(),
                p.getLoaiPhong(),
                p.getSoGiuongToiDa()
        );
    }

    public void save(PhongBenh phongBenh) {
        phongBenhDAO.save(phongBenh);
    }

    public void update(PhongBenh phongBenh) {
        long currentOccupancy = new com.hospital.app.service.BenhNhanService().countByPhong(phongBenh.getMaPhong());
        if (phongBenh.getSoGiuongToiDa() != null && phongBenh.getSoGiuongToiDa() < currentOccupancy) {
            throw new IllegalArgumentException("Số giường tối đa không thể nhỏ hơn số bệnh nhân hiện đang ở trong phòng (" + currentOccupancy + ")");
        }
        phongBenhDAO.update(phongBenh);
    }

    public void delete(String id) {
        // Prevent deleting a room that still has patients assigned
        long occupied = new com.hospital.app.service.BenhNhanService().countByPhong(id);
        if (occupied > 0) {
            throw new IllegalArgumentException("Không thể xóa phòng vì còn " + occupied + " bệnh nhân đang ở trong phòng.");
        }
        phongBenhDAO.delete(id);
    }
}
