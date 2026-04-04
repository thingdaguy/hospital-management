package com.hospital.app.controller;

import com.hospital.app.dto.BenhNhanRowDTO;
import com.hospital.app.entity.BenhNhan;
import com.hospital.app.service.BenhNhanService;
import com.hospital.app.view.BenhNhanDialog;
import com.hospital.app.view.MainForm;

import javax.swing.*;
import java.util.List;

/**
 * Controller quản lý tab Bệnh Nhân trong MainForm.
 * Khởi tạo các sự kiện UI, ánh xạ lậnh bấm nút sang các method xử lí nghiệp vụ.
 */
public class BenhNhanTabController {
    private final MainForm view;
    private final BenhNhanService benhNhanService;

    /**
     * Khởi tạo controller với view và service, sau đó găn sự kiện ngay.
     */
    public BenhNhanTabController(MainForm view, BenhNhanService benhNhanService) {
        this.view = view;
        this.benhNhanService = benhNhanService;
        wireEvents();
    }

    /**
     * Gắn tất cả các ActionListener cho các nút trong tab Bệnh Nhân.
     * Tự động nạp dữ liệu ban đầu khi khởi tạo Tab.
     */
    private void wireEvents() {
        // Sự kiện Tìm kiếm bệnh nhân
        view.getBtnSearchBN().addActionListener(e -> searchPatients());
        // Sự kiện Làm mới danh sách bệnh nhân
        view.getBtnRefreshBN().addActionListener(e -> loadPatients());
        // Sự kiện Mở form thêm mới bệnh nhân
        view.getBtnAddBN().addActionListener(e -> addPatient());
        // Sự kiện Mở form chỉnh sửa thông tin bệnh nhân
        view.getBtnEditBN().addActionListener(e -> editPatient());
        // Sự kiện Thực hiện xóa hồ sơ bệnh nhân
        view.getBtnDeleteBN().addActionListener(e -> deletePatient());
        
        // Tự động tải dữ liệu lần đầu
        loadPatients();
    }

    /** 
     * Nạp toàn bộ danh sách bệnh nhân từ Service và cập nhật lên bảng hiển thị.
     */
    private void loadPatients() {
        List<BenhNhanRowDTO> rows = benhNhanService.listAllForTable();
        view.populatePatientTable(rows);
    }

    /** 
     * Lọc danh sách bệnh nhân dựa trên từ khóa nhập vào ô tìm kiếm.
     */
    private void searchPatients() {
        String kw = view.getSearchFieldBN().getText();
        List<BenhNhanRowDTO> rows = benhNhanService.searchByName(kw);
        view.populatePatientTable(rows);
    }

    /**
     * Quy trình thêm bệnh nhân mới:
     * 1. Hiển thị Dialog nhập liệu bệnh nhân và bảo hiểm y tế.
     * 2. Thu thập dữ liệu từ Dialog sau khi người dùng bấm OK.
     * 3. Gọi Service để thực hiện lưu trữ ( BN + BHYT ).
     */
    private void addPatient() {
        try {
            // Mở cửa sổ Dialog nhập liệu rỗng (Thêm mới)
            BenhNhanDialog dlg = new BenhNhanDialog(view, "Thêm Mới Bệnh Nhân", null);
            dlg.setVisible(true);
            
            if (dlg.isOk()) {
                BenhNhan bn = dlg.getBenhNhan();
                // Thu thập thông tin Bảo hiểm y tế liên kết (Nếu người dùng có nhập)
                com.hospital.app.entity.BaoHiem bh = dlg.getBaoHiem();
                
                // Thực hiện lưu trữ đồng thời
                benhNhanService.saveWithEncounter(bn, null, bh);
                loadPatients(); // Cập nhật lại giao diện bảng
                JOptionPane.showMessageDialog(view, "Thành công: Đã thêm hồ sơ bệnh nhân mới!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // Hiển thị popup báo lỗi nếu mã BN bị trùng hoặc lỗi hệ thống
            JOptionPane.showMessageDialog(view, "Lỗi nghiệp vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Quy trình sửa thông tin bệnh nhân:
     * 1. Kiểm tra xem người dùng đã chọn bệnh nhân nào trên bảng chưa.
     * 2. Hiển thị Dialog chứa thông tin hiện tại của bệnh nhân đó.
     * 3. Lưu lại các thay đổi sau khi người dùng xác nhận.
     */
    private void editPatient() {
        String id = view.getSelectedPatientId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một hàng (bệnh nhân) trên bảng để thực hiện sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Tải dữ liệu bệnh nhân từ DB
            BenhNhan bn = benhNhanService.findById(id);
            if (bn == null) return;
            
            // Mở cửa sổ Dialog nạp sẵn dữ liệu bệnh nhân (Chế độ sửa)
            BenhNhanDialog dlg = new BenhNhanDialog(view, "Sửa Thông Tin Bệnh Nhân", bn);
            dlg.setVisible(true);
            
            if (dlg.isOk()) {
                // Cập nhật thông tin thực thể bằng dữ liệu mới từ Dialog
                benhNhanService.update(dlg.getBenhNhan());
                loadPatients();
                JOptionPane.showMessageDialog(view, "Thành công: Đã cập nhật thông tin bệnh nhân!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi kỹ thuật khi sửa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Quy trình xóa hồ sơ bệnh nhân:
     * Chú ý: Bệnh nhân chỉ xóa được nếu không còn ràng buộc dữ liệu hóa đơn/điều trị (Kiểm soát tại tầng Service).
     */
    private void deletePatient() {
        String id = view.getSelectedPatientId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn bệnh nhân cần xóa trên bảng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Yêu cầu xác nhận (Confirmation) để tránh xóa nhầm dữ liệu quan trọng
        int confirm = JOptionPane.showConfirmDialog(view, "Hành động này không thể hoàn tác. Bạn có chắc chắn muốn xóa hồ sơ bệnh nhân này?", "Xác nhận xóa dữ liệu", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Thực hiện xóa qua Service
                benhNhanService.delete(id);
                loadPatients(); // Cập nhật lại giao diện bảng
                JOptionPane.showMessageDialog(view, "Thành công: Đã xóa hồ sơ bệnh nhân khỏi hệ thống.", "Xóa bệnh nhân", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Hiển thị lỗi nếu bệnh nhân đang có hóa đơn/lượt điều trị nên không xóa được
                JOptionPane.showMessageDialog(view, "Không thể xóa: " + ex.getMessage(), "Lỗi ràng buộc dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
