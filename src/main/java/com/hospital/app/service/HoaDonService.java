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

    /**
     * Lấy danh sách tóm tắt các hóa đơn của một bệnh nhân.
     * Thường dùng để hiển thị lịch sử thanh toán trên giao diện.
     */
    public List<HoaDonRowDTO> listByBenhNhan(String maBenhNhan) {
        // Truy xuất dữ liệu từ DAO
        List<HoaDon> list = hoaDonDAO.findByMaBenhNhan(maBenhNhan);
        // Chuyển đổi sang DTO để bảo mật thông tin và tối ưu hiển thị
        return list.stream().map(h -> new HoaDonRowDTO(
                h.getMaHoaDon(),
                h.getNgayLap(),
                h.getTongTien(),
                h.getTrangThai()
        )).collect(Collectors.toList());
    }

    /**
     * Tính toán và trả về toàn bộ chi tiết chi phí của một hóa đơn.
     * Bao gồm: Tiền phòng, tiền thuốc, tiền dịch vụ và mức khấu trừ BHYT.
     */
    public ChiTietHoaDonDTO getChiTietHoaDon(String maHoaDon) {
        // Tìm hóa đơn kèm theo nạp sẵn (fetch join) các thông tin cơ bản
        HoaDon hd = hoaDonDAO.findByIdWithDetails(maHoaDon);
        if (hd == null) return null;

        LuotDieuTri luot = hd.getLuotDieuTri();

        // ── 1. Thông tin bệnh nhân ──────────────────────────────────────────────
        String maBn = "";
        String tenBn = "";
        LocalDate ngaySinh = null;
        String soTheBHYT = null;   // Mặc định null nếu không tham gia BHYT

        if (luot != null && luot.getBenhNhan() != null) {
            var bn = luot.getBenhNhan();
            maBn     = bn.getMaBenhNhan();
            tenBn    = bn.getTenBenhNhan();
            ngaySinh = bn.getNgaySinh();
            if (bn.getBaoHiem() != null) {
                soTheBHYT = bn.getBaoHiem().getMaThe();
            }
        }

        // ── 2. Tính toán chi tiết các khoản phí ─────────────────────────────────
        BigDecimal tongTienThuoc = BigDecimal.ZERO;
        BigDecimal tongTienDichVu = BigDecimal.ZERO;
        List<ChiTietDichVuDTO> danhSachDichVu = new ArrayList<>();
        BigDecimal tongTienPhong = BigDecimal.ZERO;
        long soNgay = 0;
        BigDecimal giamGia = BigDecimal.ZERO;

        if (luot != null) {
            // Tính số ngày nằm viện thực tế (Tối thiểu là 1 ngày cho trường hợp nhập/xuất cùng ngày)
            long days = 1;
            if (luot.getNgayKetThuc() != null) {
                days = java.time.temporal.ChronoUnit.DAYS.between(luot.getNgayBatDau(), luot.getNgayKetThuc());
                if (days <= 0) days = 1;
            }
            soNgay = days;

            // Tính tiền phòng: Đơn giá phòng x Số ngày nằm
            if (luot.getPhongBenh() != null && luot.getPhongBenh().getDonGia() != null) {
                tongTienPhong = luot.getPhongBenh().getDonGia().multiply(BigDecimal.valueOf(days));
            }

            // Tính tổng tiền thuốc: Cộng dồn từ tất cả các đơn thuốc trong lượt điều trị
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

            // Tính tổng tiền dịch vụ kỹ thuật (Xét nghiệm, siêu âm...)
            if (luot.getChiTietDichVus() != null) {
                for (ChiTietDichVu ctdv : luot.getChiTietDichVus()) {
                    BigDecimal gia = ctdv.getDichVu().getDonGia();
                    tongTienDichVu = tongTienDichVu.add(gia);
                    danhSachDichVu.add(new ChiTietDichVuDTO(ctdv.getDichVu().getTenDichVu(), gia));
                }
            }

            // Tính mức giảm trừ Bảo hiểm y tế (BHYT)
            BigDecimal rawTotal = tongTienPhong.add(tongTienThuoc).add(tongTienDichVu);
            if (luot.getBenhNhan() != null && luot.getBenhNhan().getBaoHiem() != null) {
                // Giảm giá = Tổng cộng x % hưởng BHYT
                BigDecimal phanTram = luot.getBenhNhan().getBaoHiem().getMucHuong()
                        .divide(BigDecimal.valueOf(100));
                giamGia = rawTotal.multiply(phanTram);
            }
        }

        // Đóng gói toàn bộ thông tin vào DTO để hiển thị lên bảng kê chi tiết
        return new ChiTietHoaDonDTO(
                hd.getMaHoaDon(),
                maBn, tenBn, ngaySinh, soTheBHYT,
                tongTienThuoc, tongTienDichVu, tongTienPhong,
                soNgay, giamGia,
                hd.getTongTien(),
                hd.getTrangThai(),
                danhSachDichVu
        );
    }
    
    /**
     * Thực hiện quy trình nghiệp vụ xuất viện và lập hóa đơn thu phí.
     * Luồng xử lý: Cập nhật lượt điều trị -> Tính chi phí -> Khấu trừ BHYT -> Lưu hóa đơn.
     */
    public HoaDon createDischargeInvoice(LuotDieuTri detachedLuot) {
        if (detachedLuot == null) throw new IllegalArgumentException("Không tìm thấy lượt điều trị");
        
        jakarta.persistence.EntityManager em = com.hospital.app.util.JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Bắt đầu phiên làm việc (Giao dịch)
            
            // Nạp lại đối tượng để đảm bảo nó được quản lý bởi Hibernate (Persistent state)
            LuotDieuTri luot = em.find(LuotDieuTri.class, detachedLuot.getMaLuot());
            if (luot == null) throw new IllegalArgumentException("Lượt điều trị không tồn tại trong hệ thống");

            // Kiểm tra trùng lặp: Nếu lượt điều trị này đã từng lập hóa đơn thì trả về hóa đơn cũ
            if (luot.getHoaDon() != null) {
                if (luot.getNgayKetThuc() == null) {
                    luot.setNgayKetThuc(luot.getHoaDon().getNgayLap());
                }
                luot.setTrangThai("Đã xuất viện");
                em.getTransaction().commit();
                return luot.getHoaDon();
            }

            // Thiết lập ngày xuất viện và trạng thái mới
            if (luot.getNgayKetThuc() == null) {
                luot.setNgayKetThuc(LocalDate.now());
            }
            luot.setTrangThai("Đã xuất viện");
            
            // Tính toán tài chính tương tự các bước trong getChiTietHoaDon
            long days = java.time.temporal.ChronoUnit.DAYS.between(luot.getNgayBatDau(), luot.getNgayKetThuc());
            if (days <= 0) days = 1;
            
            BigDecimal tongTienPhong = BigDecimal.ZERO;
            if (luot.getPhongBenh() != null && luot.getPhongBenh().getDonGia() != null) {
                tongTienPhong = luot.getPhongBenh().getDonGia().multiply(BigDecimal.valueOf(days));
            }
            
            BigDecimal tongTienThuoc = BigDecimal.ZERO;
            if (luot.getDonThuocs() != null) {
                for (var donThuoc : luot.getDonThuocs()) {
                    if (donThuoc.getChiTietDonThuocs() != null) {
                        for (ChiTietDonThuoc ctdt : donThuoc.getChiTietDonThuocs()) {
                            BigDecimal sl = BigDecimal.valueOf(ctdt.getSoLuong());
                            tongTienThuoc = tongTienThuoc.add(sl.multiply(ctdt.getThuoc().getGiaBan()));
                        }
                    }
                }
            }

            BigDecimal tongTienDichVu = BigDecimal.ZERO;
            if (luot.getChiTietDichVus() != null) {
                for (ChiTietDichVu ctdv : luot.getChiTietDichVus()) {
                    tongTienDichVu = tongTienDichVu.add(ctdv.getDichVu().getDonGia());
                }
            }
            
            BigDecimal tong = tongTienPhong.add(tongTienThuoc).add(tongTienDichVu);
            
            // Khấu trừ phần BHYT đài thọ (nếu có)
            if (luot.getBenhNhan() != null && luot.getBenhNhan().getBaoHiem() != null) {
                BigDecimal phanTram = luot.getBenhNhan().getBaoHiem().getMucHuong().divide(BigDecimal.valueOf(100));
                BigDecimal discount = tong.multiply(phanTram);
                // Bệnh nhân chỉ phải trả phần còn lại sau khi trừ BHYT
                tong = tong.subtract(discount);
            }

            // Tạo bản ghi hóa đơn mới
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon("HD" + (System.currentTimeMillis() % 10000000L));
            hd.setNgayLap(LocalDate.now());
            hd.setTongTien(tong);
            hd.setTrangThai("Đã thanh toán");
            hd.setLuotDieuTri(luot);
            
            em.persist(hd);
            
            em.getTransaction().commit(); // Xác nhận mọi thay đổi vào CSDL
            return hd;
        } catch (Exception e) {
            // Xử lý lỗi: Nếu bất kỳ bước nào thất bại, hủy bỏ toàn bộ thay đổi (Rollback)
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e; // Báo cáo lỗi kỹ thuật
        } finally {
            em.close(); // Đảm bảo đóng kết nối để tối ưu tài nguyên
        }
    }
}
