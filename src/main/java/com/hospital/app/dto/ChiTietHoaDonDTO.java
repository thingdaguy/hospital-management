package com.hospital.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO chứa toàn bộ thông tin chi tiết của một hóa đơn,
 * bao gồm thông tin bệnh nhân, các khoản chi phí và trạng thái thanh toán.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDonDTO {
    /** Mã hóa đơn */
    private String maHoaDon;
    /** Mã bệnh nhân */
    private String maBenhNhan;
    /** Họ tên bệnh nhân */
    private String tenBenhNhan;
    /** Ngày sinh bệnh nhân */
    private LocalDate ngaySinh;
    /** Số thẻ BHYT (null nếu không có) */
    private String soTheBHYT;
    /** Tổng tiền thuốc */
    private BigDecimal tongTienThuoc;
    /** Tổng tiền dịch vụ */
    private BigDecimal tongTienDichVu;
    /** Tổng tiền phòng nội trú */
    private BigDecimal tongTienPhong;
    /** Số ngày nằm phòng */
    private long soNgayPhong;
    /** Số tiền BHYT chi trả */
    private BigDecimal giamGiaBHYT;
    /** Tổng tiền thực thu sau giảm giá */
    private BigDecimal tongTienHoaDon;
    /** Trạng thái hóa đơn (Đã thanh toán / Chưa thanh toán) */
    private String trangThaiHoaDon;
    /** Danh sách dịch vụ đã sử dụng */
    private List<ChiTietDichVuDTO> danhSachDichVu;
}
