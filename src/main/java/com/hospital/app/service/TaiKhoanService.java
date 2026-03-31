package com.hospital.app.service;

import com.hospital.app.dao.TaiKhoanDAO;
import com.hospital.app.entity.TaiKhoan;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service quản lý tài khoản người dùng/admin.
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Thêm mới tài khoản ADMIN.
     */
    public void createAdmin(String id, String username, String password, String fullName) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Mã tài khoản không được để trống");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Mật khẩu không được để trống");

        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (taiKhoanDAO.findByTenDangNhap(username) != null) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        // Hash mật khẩu
        String salt = BCrypt.gensalt(10);
        String hashed = BCrypt.hashpw(password, salt);

        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(id);
        tk.setTenDangNhap(username);
        tk.setMatKhauHash(hashed);
        tk.setHoTen(fullName);
        tk.setVaiTro("ADMIN");
        tk.setTrangThai(1); // 1 = Hoạt động

        taiKhoanDAO.create(tk);
    }
}
