package com.hospital.app.service;

import com.hospital.app.dao.HoaDonDAO;
import com.hospital.app.dto.HoaDonRowDTO;
import com.hospital.app.entity.HoaDon;

import java.util.List;
import java.util.stream.Collectors;

public class HoaDonService {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public List<HoaDonRowDTO> listByBenhNhan(String maBenhNhan) {
        List<HoaDon> list = hoaDonDAO.findByMaBenhNhan(maBenhNhan);
        return list.stream().map(h -> new HoaDonRowDTO(
                h.getMaHoaDon(),
                h.getNgayLap(),
                h.getTongTien(),
                h.getTrangThai()
        )).collect(Collectors.toList());
    }
}
