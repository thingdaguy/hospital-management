package com.hospital.app.service;

import com.hospital.app.dao.ThuocDAO;
import com.hospital.app.dto.ThuocRowDTO;
import com.hospital.app.entity.Thuoc;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service cho nghiệp vụ thuốc.
 */
public class ThuocService {

    private final ThuocDAO thuocDAO = new ThuocDAO();

    public List<ThuocRowDTO> listAllForTable() {
        return thuocDAO.findAll().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    public List<ThuocRowDTO> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return thuocDAO.searchByTenContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    private ThuocRowDTO toRow(Thuoc t) {
        return new ThuocRowDTO(
                t.getMaThuoc(),
                t.getTenThuoc(),
                t.getThanhPhan(),
                t.getGiaBan()
        );
    }

    public Thuoc findById(String id) {
        return thuocDAO.findById(id);
    }

    public void save(Thuoc thuoc) {
        if (thuocDAO.findById(thuoc.getMaThuoc()) != null) {
            throw new RuntimeException("Mã thuốc " + thuoc.getMaThuoc() + " đã tồn tại.");
        }
        thuocDAO.save(thuoc);
    }

    public void update(Thuoc thuoc) {
        thuocDAO.update(thuoc);
    }

    public void delete(String id) {
        thuocDAO.delete(id);
    }
    
    public List<Thuoc> findAll() {
        return thuocDAO.findAll();
    }
}
