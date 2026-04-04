package com.hospital.app.service;

import com.hospital.app.dao.TaiKhoanDAO;
import com.hospital.app.entity.TaiKhoan;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Dịch vụ quản lý tài khoản người dùng và quản trị viên (Admin).
 * Chịu trách nhiệm về logic validation và mã hóa mật khẩu.
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Tạo mới một tài khoản với quyền Quản trị viên (ADMIN).
     * @param id Mã định danh tài khoản.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu gốc (Chưa mã hóa).
     * @param fullName Họ tên đầy đủ của người dùng.
     */
    public void createAdmin(String id, String username, String password, String fullName) {
        // Validation: Kiểm tra đầu vào không được trống
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Mã tài khoản không được để trống");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Mật khẩu không được để trống");

        // Nghiệp vụ: Kiểm tra tính duy nhất của tên đăng nhập
        if (taiKhoanDAO.findByTenDangNhap(username) != null) {
            throw new RuntimeException("Tên đăng nhập '" + username + "' đã tồn tại trong hệ thống!");
        }

        // Bảo mật: Sử dụng BCrypt để băm (hash) mật khẩu kèm muối (salt)
        // Điều này giúp bảo vệ mật khẩu ngay cả khi cơ sở dữ liệu bị rò rỉ
        String salt = BCrypt.gensalt(10); // Tạo salt với độ phức tạp cấp 10
        String hashed = BCrypt.hashpw(password, salt); // Thực hiện băm mật khẩu

        // Khởi tạo thực thể TaiKhoan và thiết lập các thuộc tính mặc định
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(id);
        tk.setTenDangNhap(username);
        tk.setMatKhauHash(hashed); // Lưu mật khẩu đã được mã hóa
        tk.setHoTen(fullName);
        tk.setVaiTro("ADMIN");
        tk.setTrangThai(1); // Mặc định trạng thái 1 (Đang hoạt động)

        // Thực hiện lưu xuống Database thông qua DAO
        taiKhoanDAO.create(tk);
    }
}
