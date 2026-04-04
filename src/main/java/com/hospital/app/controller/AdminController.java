package com.hospital.app.controller;

import com.hospital.app.service.TaiKhoanService;
import com.hospital.app.view.AdminDialog;
import javax.swing.*;

/**
 * Lớp điều khiển (Controller) cho cửa sổ quản lý tài khoản Quản trị viên (AdminDialog).
 * Xử lý các thao tác nhập liệu và lưu trữ tài khoản hệ thống.
 */
public class AdminController {

    private final AdminDialog view;
    private final TaiKhoanService taiKhoanService;

    /**
     * Khởi tạo AdminController.
     * @param view Cửa sổ Dialog nhập liệu Admin.
     * @param taiKhoanService Dịch vụ xử lý tài khoản.
     */
    public AdminController(AdminDialog view, TaiKhoanService taiKhoanService) {
        this.view = view;
        this.taiKhoanService = taiKhoanService;
        // Đăng ký sự kiện ngay khi khởi tạo
        initEvents();
    }

    /**
     * Gắn bộ lắng nghe sự kiện (Listener) cho nút Lưu trên giao diện.
     */
    private void initEvents() {
        // Lắng nghe sự kiện nhấn nút "Lưu"
        view.getBtnSave().addActionListener(e -> saveAdmin());
    }

    /**
     * Thu thập dữ liệu từ giao diện và thực hiện tạo mới tài khoản Quản trị viên.
     */
    private void saveAdmin() {
        // 1. Trích xuất thông tin từ các trường nhập liệu (với trim() để xóa khoảng trắng thừa)
        String id = view.getTxtId().getText().trim();
        String username = view.getTxtUsername().getText().trim();
        String password = new String(view.getTxtPassword().getPassword());
        String fullName = view.getTxtFullName().getText().trim();

        // 2. Kiểm tra tính đầy đủ của dữ liệu đầu vào bắt buộc
        if (id.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ: Mã tài khoản, Tên đăng nhập và Mật khẩu!");
            return;
        }

        try {
            // 3. Gọi Service thực hiện nghiệp vụ lưu trữ (có kèm băm mật khẩu bảo mật)
            taiKhoanService.createAdmin(id, username, password, fullName);
            
            // 4. Thông báo thành công và đóng cửa sổ
            JOptionPane.showMessageDialog(view, "Thành công: Đã khởi tạo tài khoản Quản trị viên mới!");
            view.dispose();
        } catch (Exception ex) {
            // Hiển thị lỗi hệ thống hoặc trùng tên đăng nhập
            JOptionPane.showMessageDialog(view, "Lỗi khi tạo tài khoản: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}
