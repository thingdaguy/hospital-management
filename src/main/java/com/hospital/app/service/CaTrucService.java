package com.hospital.app.service;

import com.hospital.app.dao.CaTrucDAO;
import com.hospital.app.dto.CaTrucRowDTO;
import com.hospital.app.entity.CaTruc;

import java.util.List;
import java.util.stream.Collectors;

public class CaTrucService {

    private final CaTrucDAO dao = new CaTrucDAO();

    public List<CaTruc> findAll() {
        return dao.findAll();
    }

    public CaTruc findById(String id) {
        return dao.findById(id);
    }

    public List<CaTrucRowDTO> listAllForTable() {
        return dao.findAll().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    public List<CaTrucRowDTO> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }

        return dao.findAll().stream()
                .filter(c -> c.getTenCa().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    private CaTrucRowDTO toRow(CaTruc c) {
        return new CaTrucRowDTO(
                c.getMaCa(),
                c.getTenCa(),
                c.getThoiGianBatDau(),
                c.getThoiGianKetThuc()
        );
    }

    public void save(CaTruc ct) {
        dao.save(ct);
    }

    public void update(CaTruc ct) {
        dao.update(ct);
    }

    public void delete(String id) {
        dao.delete(id);
    }
}