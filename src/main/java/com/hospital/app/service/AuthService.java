package com.hospital.app.service;

import com.hospital.app.dao.TaiKhoanDAO;
import com.hospital.app.entity.TaiKhoan;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Auth — dang nhap theo vai tro.
 */
public class AuthService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Dang nhap ADMIN.
     *
     * @return Tai khoan neu hop le, nguoc lai null.
     */
    public TaiKhoan loginAdmin(String username, String rawPassword) {
        if (username == null || username.isBlank()) return null;
        if (rawPassword == null) return null;

        TaiKhoan tk = taiKhoanDAO.findByTenDangNhap(username);
        if (tk == null) return null;
        if (tk.getTrangThai() == null || tk.getTrangThai() != 1) return null;
        if (tk.getMatKhauHash() == null || tk.getMatKhauHash().isBlank()) return null;

        boolean ok = BCrypt.checkpw(rawPassword, tk.getMatKhauHash());
        return ok ? tk : null;
    }
}

