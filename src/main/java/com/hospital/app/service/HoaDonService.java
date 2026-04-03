package com.hospital.app.service;

import com.hospital.app.dao.HoaDonDAO;
import com.hospital.app.dto.HoaDonRowDTO;
import com.hospital.app.entity.HoaDon;

import com.hospital.app.dto.ChiTietDichVuDTO;
import com.hospital.app.dto.ChiTietHoaDonDTO;
import com.hospital.app.entity.LuotDieuTri;
import com.hospital.app.entity.ChiTietDichVu;
import com.hospital.app.entity.ChiTietDonThuoc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    public ChiTietHoaDonDTO getChiTietHoaDon(String maHoaDon) {
        HoaDon hd = hoaDonDAO.findById(maHoaDon);
        if (hd == null) return null;

        String maBN = hd.getBenhNhan().getMaBenhNhan();
        LocalDate ngayLap = hd.getNgayLap();

        List<LuotDieuTri> luotDieuTris = hoaDonDAO.findLuotDieuTriForInvoice(maBN, ngayLap);

        BigDecimal tongTienThuoc = BigDecimal.ZERO;
        BigDecimal tongTienDichVu = BigDecimal.ZERO;
        List<ChiTietDichVuDTO> danhSachDichVu = new ArrayList<>();

        for (LuotDieuTri luot : luotDieuTris) {
            if (luot.getDonThuocs() != null) {
                for (var donThuoc : luot.getDonThuocs()) {
                    if (donThuoc.getChiTietDonThuocs() != null) {
                        for (ChiTietDonThuoc ctdt : donThuoc.getChiTietDonThuocs()) {
                            BigDecimal sl = BigDecimal.valueOf(ctdt.getSoLuong());
                            BigDecimal gia = ctdt.getThuoc().getGiaBan();
                            tongTienThuoc = tongTienThuoc.add(sl.multiply(gia));
                        }
                    }
                }
            }

            if (luot.getChiTietDichVus() != null) {
                for (ChiTietDichVu ctdv : luot.getChiTietDichVus()) {
                    BigDecimal gia = ctdv.getDichVu().getDonGia();
                    tongTienDichVu = tongTienDichVu.add(gia);
                    danhSachDichVu.add(new ChiTietDichVuDTO(ctdv.getDichVu().getTenDichVu(), gia));
                }
            }
        }

        return new ChiTietHoaDonDTO(tongTienThuoc, tongTienDichVu, hd.getTongTien(), danhSachDichVu);
    }
}
