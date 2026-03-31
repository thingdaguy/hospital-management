-- =============================================================================
-- Login DB Table — tai khoan dang nhap 
-- =============================================================================

USE hospital_db;

CREATE TABLE IF NOT EXISTS tai_khoan (
    ma_tai_khoan   VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_dang_nhap  VARCHAR(50) NOT NULL UNIQUE,
    mat_khau_hash  VARCHAR(255) NOT NULL,
    ho_ten         VARCHAR(200) NULL,
    vai_tro        VARCHAR(30) NOT NULL DEFAULT 'NHANVIEN',
    trang_thai     INT NOT NULL DEFAULT 1, -- 1=active, 0=inactive
    ngay_tao       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_cap_nhat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- VÍ DỤ TẠO TÀI KHOẢN ADMIN
-- =============================================================================
-- Ví dụ: với password '1', bạn tạo hash bằng BCrypt rồi dán vào dưới.
--
INSERT INTO tai_khoan (ma_tai_khoan, ten_dang_nhap, mat_khau_hash, ho_ten, vai_tro, trang_thai)
VALUES ('TK_ADMIN_01', 'admin', '$2a$10$fhmQytELij7SfTbjFYAyqOXLD2QZG5tfGBOQovyLaB9MUe.DsQVcS', 'Quản trị', 'ADMIN', 1);

