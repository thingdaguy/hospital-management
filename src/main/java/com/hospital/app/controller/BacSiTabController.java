package com.hospital.app.controller;

import com.hospital.app.dto.BacSiRowDTO;
import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.Khoa;
import com.hospital.app.service.BacSiService;
import com.hospital.app.service.KhoaService;
import com.hospital.app.view.BacSiDialog;
import com.hospital.app.view.MainForm;

import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller quản lý tab Bác Sĩ trong MainForm.
 * Xử lí nghiệp vụ thêm, sửa, xóa, tìm kiếm bác sĩ.
 */
public class BacSiTabController {
    private final MainForm view;
    private final BacSiService bacSiService;
    private final KhoaService khoaService;

    /**
     * Khởi tạo controller, inject các service cần thiết và găn sự kiện UI.
     */
    public BacSiTabController(MainForm view, BacSiService bacSiService, KhoaService khoaService) {
        this.view = view;
        this.bacSiService = bacSiService;
        this.khoaService = khoaService;
        wireEvents();
    }

    /**
     * Gắn các bộ lắng nghe sự kiện (ActionListener) cho các thành phần UI trong Tab Bác sĩ.
     * Tự động nạp dữ liệu ban đầu khi khởi tạo.
     */
    private void wireEvents() {
        // Làm mới danh sách
        view.getBtnRefreshBS().addActionListener(e -> loadDoctors());
        // Tìm kiếm bác sĩ
        view.getBtnSearchBS().addActionListener(e -> searchDoctors());
        // Thêm mới bác sĩ
        view.getBtnAddBS().addActionListener(e -> addDoctor());
        // Sửa thông tin bác sĩ
        view.getBtnEditBS().addActionListener(e -> editDoctor());
        // Xóa bác sĩ
        view.getBtnDeleteBS().addActionListener(e -> deleteDoctor());
        
        // Tải dữ liệu mặc định
        loadDoctors();
    }

    /** 
     * Làm sạch ô tìm kiếm và nạp lại toàn bộ danh sách bác sĩ từ Cơ sở dữ liệu lên bảng.
     */
    private void loadDoctors() {
        view.getSearchFieldBS().setText("");
        List<BacSiRowDTO> rows = bacSiService.listAllForTable();
        view.populateDoctorTable(rows);
    }

    /** 
     * Thực hiện truy vấn danh sách bác sĩ dựa trên tên từ khóa người dùng nhập vào.
     */
    private void searchDoctors() {
        String kw = view.getSearchFieldBS().getText();
        List<BacSiRowDTO> rows = bacSiService.searchByName(kw);
        view.populateDoctorTable(rows);
    }

    /**
     * Nghiệp vụ Thêm mới Bác sĩ:
     * 1. Lấy danh sách các Khoa hiện có để hiển thị trong Dialog chọn lựa.
     * 2. Hiển thị Dialog nhập liệu cho người dùng.
     * 3. Lưu thông tin bác sĩ mới vào CSDL nếu người dùng xác nhận.
     */
    private void addDoctor() {
        try {
            // Nạp danh sách khoa phục vụ ComboBox trong Dialog
            List<Khoa> khoas = khoaService.findAll();
            // Mở cửa sổ nhập liệu ở chế độ thêm mới (null bác sĩ)
            BacSiDialog dlg = new BacSiDialog(view, "Thêm Mới Bác Sĩ", khoas, null);
            dlg.setVisible(true);
            
            if (dlg.isOk()) {
                // Lưu vào DB thông qua Service
                bacSiService.save(dlg.getBacSi());
                loadDoctors(); // Cập nhật lại giao diện bảng
                JOptionPane.showMessageDialog(view, "Thành công: Đã thêm hồ sơ bác sĩ mới!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // Xử lý lỗi hệ thống hoặc lỗi nghiệp vụ (ví dụ: trùng mã BS)
            JOptionPane.showMessageDialog(view, "Lỗi khi xử lý thêm mới: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Nghiệp vụ Chỉnh sửa thông tin Bác sĩ:
     * 1. Kiểm tra bác sĩ được chọn trên bảng.
     * 2. Nạp dữ liệu hiện tại của bác sĩ đó từ DB.
     * 3. Hiển thị Dialog sửa đổi.
     */
    private void editDoctor() {
        // Lấy mã bác sĩ từ dòng đang chọn
        String id = view.getSelectedDoctorId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bác sĩ trên danh sách để thực hiện sửa đổi.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Tải thông tin thực thể đầy đủ
            BacSi bs = bacSiService.findById(id);
            if (bs == null) return;
            
            // Lấy danh sách khoa liên quan
            List<Khoa> khoas = khoaService.findAll();
            
            // Mở Dialog nạp sẵn thông tin cũ
            BacSiDialog dlg = new BacSiDialog(view, "Cập Nhật Thông Tin Bác Sĩ", khoas, bs);
            dlg.setVisible(true);
            
            if (dlg.isOk()) {
                // Ghi nhận thay đổi vào DB
                bacSiService.update(dlg.getBacSi());
                loadDoctors();
                JOptionPane.showMessageDialog(view, "Thành công: Đã cập nhật hồ sơ bác sĩ!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi kỹ thuật khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 
     * Nghiệp vụ Xóa bác sĩ:
     * Yêu cầu xác nhận và xử lý các ràng buộc dữ liệu (nếu bác sĩ đang điều trị bệnh nhân).
     */
    private void deleteDoctor() {
        String id = view.getSelectedDoctorId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn bác sĩ cần xóa khỏi hệ thống.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Hộp thoại xác nhận quan trọng
        int confirm = JOptionPane.showConfirmDialog(view, "Dữ liệu bác sĩ '" + id + "' sẽ bị xóa vĩnh viễn. Bạn có chắc chắn không?", "Xác nhận xóa hồ sơ", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Thực hiện xóa
                bacSiService.delete(id);
                loadDoctors();
                JOptionPane.showMessageDialog(view, "Thành công: Đã xóa hồ sơ bác sĩ.", "Kết quả xóa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Lỗi thường gặp: Bác sĩ đang phụ trách lượt điều trị nên không thể xóa do ràng buộc FK
                JOptionPane.showMessageDialog(view, "Không thể xóa bác sĩ: " + ex.getMessage(), "Lỗi ràng buộc dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
