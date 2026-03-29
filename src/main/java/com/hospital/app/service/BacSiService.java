package com.hospital.app.service;

import com.hospital.app.dao.BacSiDAO;
import com.hospital.app.dto.BacSiRowDTO;
import com.hospital.app.entity.BacSi;

import java.util.List;
import java.util.stream.Collectors;

public class BacSiService {
    private final BacSiDAO bacSiDAO = new BacSiDAO();

    public List<BacSi> findAll() {
        return bacSiDAO.findAll();
    }

    public BacSi findById(String id) {
        return bacSiDAO.findById(id);
    }

    public List<BacSiRowDTO> listAllForTable() {
        return bacSiDAO.findAll().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    public List<BacSiRowDTO> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        return bacSiDAO.searchByTenContaining(keyword).stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    private BacSiRowDTO toRow(BacSi b) {
        String tenKhoa = b.getKhoa() != null ? b.getKhoa().getTenKhoa() : "";
        return new BacSiRowDTO(
                b.getMaBacSi(),
                b.getTenBacSi(),
                b.getNgayVaoLam(),
                b.getChuyenMon(),
                tenKhoa
        );
    }

    public void save(BacSi bacSi) {
        bacSiDAO.save(bacSi);
    }

    public void update(BacSi bacSi) {
        bacSiDAO.update(bacSi);
    }

    public void delete(String id) {
        bacSiDAO.delete(id);
    }
}
