package com.hospital.app.service;

import com.hospital.app.dao.LuotDieuTriDAO;
import com.hospital.app.dto.LuotDieuTriRowDTO;
import com.hospital.app.entity.LuotDieuTri;

import java.util.List;
import java.util.stream.Collectors;

public class LuotDieuTriService {
    private final LuotDieuTriDAO dao = new LuotDieuTriDAO();

    /**
     * Lấy danh sách toàn bộ lượt điều trị chuyển đổi sang dạng Row DTO để hiển thị lên bảng.
     */
    public List<LuotDieuTriRowDTO> listAllForTable() {
        // Nạp dữ liệu từ DAO và sử dụng Stream API để ánh xạ sang DTO
        return dao.findAllWithDetails().stream().map(this::toRow).collect(Collectors.toList());
    }

    /**
     * Tìm kiếm lượt điều trị theo tên bệnh nhân.
     * Nếu từ khóa trống, sẽ trả về toàn bộ danh sách.
     */
    public List<LuotDieuTriRowDTO> searchByPatientName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listAllForTable();
        }
        // Gọi DAO tìm kiếm và chuyển đổi kết quả sang DTO
        return dao.searchByTenBenhNhan(keyword).stream().map(this::toRow).collect(Collectors.toList());
    }

    /**
     * Hàm hỗ trợ chuyển đổi thực thể (Entity) sang đối tượng vận chuyển dữ liệu (DTO).
     * Giúp tách biệt lớp dữ liệu và lớp hiển thị.
     */
    private LuotDieuTriRowDTO toRow(LuotDieuTri l) {
        // Xử lý các giá trị null để tránh lỗi hiển thị khi thông tin chưa đầy đủ
        String tenBn = l.getBenhNhan() != null ? l.getBenhNhan().getTenBenhNhan() : "";
        String tenBs = l.getBacSiDieuTri() != null ? l.getBacSiDieuTri().getTenBacSi() : "";
        String tenKhoa = (l.getBacSiDieuTri() != null && l.getBacSiDieuTri().getKhoa() != null) 
                         ? l.getBacSiDieuTri().getKhoa().getTenKhoa() : "";
        String maPhong = l.getPhongBenh() != null ? l.getPhongBenh().getMaPhong() : "";

        return new LuotDieuTriRowDTO(
                l.getMaLuot(),
                tenBn,
                tenBs,
                tenKhoa,
                maPhong,
                l.getNgayBatDau(),
                l.getNgayKetThuc(),
                l.getTrangThai()
        );
    }

    /**
     * Tìm lượt điều trị theo ID cơ bản.
     */
    public LuotDieuTri findById(String id) {
        return dao.findById(id);
    }
    
    /**
     * Tìm lượt điều trị kèm đầy đủ chi tiết liên quan (JOIN FETCH).
     */
    public LuotDieuTri findByIdWithDetails(String id) {
        return dao.findByIdWithDetails(id);
    }

    /**
     * Nghiệp vụ: Lưu lượt điều trị mới.
     * Kiểm tra điều kiện: Một bệnh nhân không được có 2 lượt điều trị "Đang điều trị" cùng lúc.
     */
    public void save(LuotDieuTri l) {
        if (l.getBenhNhan() != null && l.getBenhNhan().getMaBenhNhan() != null) {
            // Kiểm tra xem bệnh nhân có lượt điều trị nào chưa kết thúc không
            LuotDieuTri active = dao.findActiveByBenhNhan(l.getBenhNhan().getMaBenhNhan());
            if (active != null) {
                // Ném ngoại lệ nghiệp vụ nếu vi phạm quy tắc: 1 BN - 1 lượt điều trị hiện hành
                throw new IllegalStateException("Bệnh nhân đang có một lượt điều trị (" + active.getMaLuot() + ") chưa xuất viện. Vui lòng xuất viện trước khi tiếp nhận mới!");
            }
        }
        dao.save(l);
    }

    /**
     * Cập nhật thông tin lượt điều trị (Xử lý thông qua tầng DAO).
     */
    public void update(LuotDieuTri l) {
        dao.update(l);
    }
}
