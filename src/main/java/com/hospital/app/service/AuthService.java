package com.hospital.app.service;

import com.hospital.app.dao.TaiKhoanDAO;
import com.hospital.app.entity.TaiKhoan;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Dịch vụ xác thực (Authentication) — Xử lý đăng nhập và kiểm tra quyền truy cập.
 */
public class AuthService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Thực hiện kiểm tra đăng nhập cho tài khoản Quản trị viên (ADMIN).
     * @param username Tên đăng nhập.
     * @param rawPassword Mật khẩu chưa mã hóa từ form đăng nhập.
     * @return Đối tượng TaiKhoan nếu đăng nhập thành công, ngược lại trả về null.
     */
    public TaiKhoan loginAdmin(String username, String rawPassword) {
        // Kiểm tra tính hợp lệ của đầu vào cơ bản
        if (username == null || username.isBlank()) return null;
        if (rawPassword == null) return null;

        // 1. Tìm tài khoản trong Cơ sở dữ liệu theo tên đăng nhập
        TaiKhoan tk = taiKhoanDAO.findByTenDangNhap(username);
        
        // 2. Kiểm tra sự tồn tại của tài khoản
        if (tk == null) return null;
        
        // 3. Kiểm tra trạng thái tài khoản (Chỉ cho phép nếu trang_thai = 1 - Hoạt động)
        if (tk.getTrangThai() == null || tk.getTrangThai() != 1) return null;
        
        // 4. Kiểm tra dữ liệu mật khẩu băm có tồn tại hay không
        if (tk.getMatKhauHash() == null || tk.getMatKhauHash().isBlank()) return null;

        // 5. Sử dụng BCrypt để so sánh mật khẩu nhập vào với mã băm lưu trong DB
        // Kỹ thuật này an toàn vì không cần giải mã mật khẩu gốc
        boolean ok = BCrypt.checkpw(rawPassword, tk.getMatKhauHash());
        
        // Trả về thực thể tài khoản nếu khớp mật khẩu, ngược lại báo lỗi (null)
        return ok ? tk : null;
    }
}

