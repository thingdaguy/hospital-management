package com.hospital.app.controller;

import com.hospital.app.service.TaiKhoanService;
import com.hospital.app.view.AdminDialog;
import javax.swing.*;

/**
 * Controller xử lý logic cho AdminDialog.
 */
public class AdminController {

    private final AdminDialog view;
    private final TaiKhoanService taiKhoanService;

    public AdminController(AdminDialog view, TaiKhoanService taiKhoanService) {
        this.view = view;
        this.taiKhoanService = taiKhoanService;
        initEvents();
    }

    private void initEvents() {
        view.getBtnSave().addActionListener(e -> saveAdmin());
    }

    private void saveAdmin() {
        String id = view.getTxtId().getText().trim();
        String username = view.getTxtUsername().getText().trim();
        String password = new String(view.getTxtPassword().getPassword());
        String fullName = view.getTxtFullName().getText().trim();

        if (id.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng điền đủ Mã, Tên đăng nhập và Mật khẩu!");
            return;
        }

        try {
            taiKhoanService.createAdmin(id, username, password, fullName);
            JOptionPane.showMessageDialog(view, "Thêm Admin thành công!");
            view.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}
