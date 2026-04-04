package com.hospital.app.controller;

import com.hospital.app.service.*;
import com.hospital.app.view.MainForm;

/**
 * Controller chính của ứng dụng - Đóng vai trò là trung tâm điều phối.
 * Chịu trách nhiệm khởi tạo các Service và kết nối các Controller con với Giao diện chính (MainForm).
 */
public class MainFormController {

    private final MainForm view;

    // Khởi tạo các lớp nghiệp vụ (Services) dùng chung cho toàn hệ thống
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final BacSiService bacSiService = new BacSiService();
    private final PhongBenhService phongBenhService = new PhongBenhService();
    private final KhoaService khoaService = new KhoaService();
    private final HoaDonService hoaDonService = new HoaDonService();
    private final ThongKeService thongKeService = new ThongKeService();
    private final LuotDieuTriService luotDieuTriService = new LuotDieuTriService();
    private final ThuocService thuocService = new ThuocService();
    private final DonThuocService donThuocService = new DonThuocService();

    /**
     * Hàm khởi tạo MainFormController.
     * @param view Đối tượng giao diện chính của ứng dụng.
     */
    public MainFormController(MainForm view) {
        this.view = view;
        // Thực hiện gán các controller con cho từng Tab tương ứng
        initControllers();
    }

    /**
     * Phương thức khởi tạo và kích hoạt các Controller của từng Tab/Chức năng.
     * Mỗi controller sẽ tự đăng ký các sự kiện (Event) cho các thành phần UI trong Tab đó.
     */
    private void initControllers() {
        // Tab Tổng quan (Dashboard)
        new OverviewController(view.getOverviewPanel(), thongKeService);
        // Tab Quản lý bệnh nhân
        new BenhNhanTabController(view, benhNhanService);
        // Tab Quản lý lượt điều trị (Tiếp nhận & Xuất viện)
        new LuotDieuTriTabController(view, luotDieuTriService, benhNhanService, bacSiService, phongBenhService, hoaDonService, donThuocService, thuocService);
        // Tab Quản lý bác sĩ
        new BacSiTabController(view, bacSiService, khoaService);
        // Tab Quản lý phòng bệnh
        new PhongBenhTabController(view, phongBenhService);
    }
}
