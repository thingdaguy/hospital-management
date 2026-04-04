package com.hospital.app.controller;

import com.hospital.app.dto.PhongBenhRowDTO;
import com.hospital.app.entity.PhongBenh;
import com.hospital.app.service.PhongBenhService;
import com.hospital.app.view.MainForm;
import com.hospital.app.view.PhongBenhDialog;

import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller quản lý tab Phòng Bệnh trong MainForm.
 * Xử lí CRUD phòng bệnh và tìm kiếm theo loại phòng.
 */
public class PhongBenhTabController {
    private final MainForm view;
    private final PhongBenhService phongBenhService;

    /** Khởi tạo controller và găn sự kiện UI cho tab Phòng Bệnh. */
    public PhongBenhTabController(MainForm view, PhongBenhService phongBenhService) {
        this.view = view;
        this.phongBenhService = phongBenhService;
        wireEvents();
    }

    /**
     * Gắn các bộ lắng nghe sự kiện (ActionListener) cho các thành phần UI trong Tab Phòng bệnh.
     * Tự động nạp dữ liệu ban đầu khi khởi tạo.
     */
    private void wireEvents() {
        // Làm mới dữ liệu
        view.getBtnRefreshPB().addActionListener(e -> loadRooms());
        // Tìm kiếm phòng
        view.getBtnSearchPB().addActionListener(e -> searchRooms());
        // Thêm mới phòng
        view.getBtnAddPB().addActionListener(e -> addRoom());
        // Sửa thông tin phòng
        view.getBtnEditPB().addActionListener(e -> editRoom());
        // Xóa phòng
        view.getBtnDeletePB().addActionListener(e -> deleteRoom());
        
        // Nạp dữ liệu mặc định
        loadRooms();
    }

    /** 
     * Làm sạch ô tìm kiếm và nạp lại toàn bộ danh sách phòng bệnh từ CSDL lên bảng hiển thị.
     */
    private void loadRooms() {
        view.getSearchFieldPB().setText("");
        List<PhongBenhRowDTO> rows = phongBenhService.listAllForTable();
        view.populateRoomTable(rows);
    }

    /** 
     * Thực hiện lọc danh sách phòng bệnh dựa trên loại phòng người dùng nhập vào ô tìm kiếm.
     */
    private void searchRooms() {
        String kw = view.getSearchFieldPB().getText();
        List<PhongBenhRowDTO> rows = phongBenhService.searchByLoaiPhong(kw);
        view.populateRoomTable(rows);
    }

    /**
     * Nghiệp vụ Thêm mới Phòng bệnh:
     * 1. Hiển thị Dialog nhập liệu phòng bệnh mới.
     * 2. Lưu vào CSDL và cập nhật giao diện nếu người dùng xác nhận.
     */
    private void addRoom() {
        try {
            // Mở cửa sổ nhập liệu ở chế độ thêm mới (null phòng)
            PhongBenhDialog dlg = new PhongBenhDialog(view, "Cấu Hình Phòng Bệnh Mới", null);
            dlg.setVisible(true);
            
            if (dlg.isOk()) {
                // Lưu vào DB qua Service
                phongBenhService.save(dlg.getPhongBenh());
                loadRooms(); // Cập nhật lại giao diện bảng
                JOptionPane.showMessageDialog(view, "Thành công: Đã thêm phòng bệnh mới vào hệ thống!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // Xử lý lỗi hệ thống hoặc trùng mã phòng
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm mới: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Nghiệp vụ Chỉnh sửa thông tin Phòng bệnh:
     * 1. Kiểm tra phòng bệnh được chọn trên bảng.
     * 2. Hiển thị thông tin hiện tại lên Dialog để sửa đổi.
     * 3. Kiểm soát logic hạ số giường (đảm bảo không nhỏ hơn số người đang ở).
     */
    private void editRoom() {
        // Lấy mã phòng từ dòng được chọn
        String id = view.getSelectedRoomId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phòng bệnh trên bảng để thực hiện sửa đổi.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Tải thông tin thực thể từ DB
            PhongBenh pb = phongBenhService.findById(id);
            if (pb == null) return;
            
            // Mở Dialog nạp sẵn thông tin cũ
            PhongBenhDialog dlg = new PhongBenhDialog(view, "Cập Nhật Thông Tin Phòng", pb);
            dlg.setVisible(true);
            
            if (dlg.isOk()) {
                // Ghi nhận thay đổi (Service sẽ check logic số giường)
                phongBenhService.update(dlg.getPhongBenh());
                loadRooms();
                JOptionPane.showMessageDialog(view, "Thành công: Đã cập nhật cấu hình phòng bệnh!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // Hiển thị lỗi nếu hạ số giường sai logic hoặc lỗi DB
            JOptionPane.showMessageDialog(view, "Lỗi kỹ thuật khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 
     * Nghiệp vụ Xóa phòng bệnh:
     * Yêu cầu xác nhận và đảm bảo phòng hiện đang trống (không có bệnh nhân).
     */
    private void deleteRoom() {
        String id = view.getSelectedRoomId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần xóa trên bảng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Hộp thoại xác nhận cuối cùng
        int confirm = JOptionPane.showConfirmDialog(view, "Dữ liệu phòng '" + id + "' sẽ bị xóa vĩnh viễn. Bạn có chắc chắn không?", "Xác nhận xóa dữ liệu", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Thực hiện xóa
                phongBenhService.delete(id);
                loadRooms();
                JOptionPane.showMessageDialog(view, "Thành công: Đã xóa phòng bệnh khỏi hệ thống.", "Kết quả xóa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Lỗi thường gặp: Phòng vẫn còn bệnh nhân đang điều trị nên không thể xóa
                JOptionPane.showMessageDialog(view, "Không thể xóa: " + ex.getMessage(), "Lỗi ràng buộc dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
