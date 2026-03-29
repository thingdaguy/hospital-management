-- =============================================================================
-- Hospital DB — schema + seed data (MySQL 8+)
-- Khớp với DATABASE_DESIGN.md và các entity JPA trong project
-- =============================================================================

CREATE DATABASE IF NOT EXISTS hospital_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hospital_db;

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------------
-- 1. Khoa
-- ---------------------------------------------------------------------------
CREATE TABLE khoa (
    ma_khoa       VARCHAR(20)  NOT NULL PRIMARY KEY,
    ten_khoa      VARCHAR(200) NOT NULL,
    so_dien_thoai VARCHAR(30)  NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 2. Bác sĩ, Y tá, Ca trực, Phòng
-- ---------------------------------------------------------------------------
CREATE TABLE bac_si (
    ma_bac_si     VARCHAR(20)  NOT NULL PRIMARY KEY,
    ten_bac_si    VARCHAR(200) NOT NULL,
    ngay_vao_lam  DATE         NOT NULL,
    chuyen_mon    VARCHAR(200) NOT NULL,
    ma_khoa       VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_bac_si_khoa FOREIGN KEY (ma_khoa) REFERENCES khoa (ma_khoa)
) ENGINE=InnoDB;

CREATE TABLE y_ta (
    ma_y_ta          VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_y_ta         VARCHAR(200) NOT NULL,
    nam_kinh_nghiem  INT          NOT NULL,
    ma_khoa          VARCHAR(20) NOT NULL,
    CONSTRAINT fk_y_ta_khoa FOREIGN KEY (ma_khoa) REFERENCES khoa (ma_khoa)
) ENGINE=InnoDB;

CREATE TABLE ca_truc (
    ma_ca               VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_ca              VARCHAR(50) NOT NULL,
    thoi_gian_bat_dau   TIME        NOT NULL,
    thoi_gian_ket_thuc  TIME        NOT NULL
) ENGINE=InnoDB;

CREATE TABLE phong_benh (
    ma_phong          VARCHAR(20) NOT NULL PRIMARY KEY,
    loai_phong        VARCHAR(20) NOT NULL,
    so_giuong_toi_da  INT         NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 3. N-N: Bác sĩ — Ca trực, Y tá — Ca trực
-- ---------------------------------------------------------------------------
CREATE TABLE bac_si_ca_truc (
    ma_bac_si VARCHAR(20) NOT NULL,
    ma_ca     VARCHAR(20) NOT NULL,
    PRIMARY KEY (ma_bac_si, ma_ca),
    CONSTRAINT fk_bsc_bac_si FOREIGN KEY (ma_bac_si) REFERENCES bac_si (ma_bac_si),
    CONSTRAINT fk_bsc_ca     FOREIGN KEY (ma_ca)     REFERENCES ca_truc (ma_ca)
) ENGINE=InnoDB;

CREATE TABLE y_ta_ca_truc (
    ma_y_ta VARCHAR(20) NOT NULL,
    ma_ca   VARCHAR(20) NOT NULL,
    PRIMARY KEY (ma_y_ta, ma_ca),
    CONSTRAINT fk_ytc_y_ta FOREIGN KEY (ma_y_ta) REFERENCES y_ta (ma_y_ta),
    CONSTRAINT fk_ytc_ca   FOREIGN KEY (ma_ca)   REFERENCES ca_truc (ma_ca)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 4. Bệnh nhân, Bảo hiểm
-- ---------------------------------------------------------------------------
CREATE TABLE benh_nhan (
    ma_benh_nhan   VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_benh_nhan  VARCHAR(200) NOT NULL,
    ngay_sinh      DATE         NOT NULL,
    so_dien_thoai  VARCHAR(30)  NULL,
    ma_bac_si      VARCHAR(20) NOT NULL,
    ma_phong       VARCHAR(20) NOT NULL,
    CONSTRAINT fk_bn_bac_si FOREIGN KEY (ma_bac_si) REFERENCES bac_si (ma_bac_si),
    CONSTRAINT fk_bn_phong  FOREIGN KEY (ma_phong)  REFERENCES phong_benh (ma_phong)
) ENGINE=InnoDB;

CREATE TABLE bao_hiem (
    ma_the        VARCHAR(20) NOT NULL PRIMARY KEY,
    ngay_het_han  DATE        NOT NULL,
    muc_huong     DECIMAL(5,2) NOT NULL,
    ma_benh_nhan  VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT fk_bh_bn FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan (ma_benh_nhan)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 5. Lượt điều trị, Dịch vụ, Chi tiết dịch vụ
-- ---------------------------------------------------------------------------
CREATE TABLE luot_dieu_tri (
    ma_luot        VARCHAR(20) NOT NULL PRIMARY KEY,
    ngay_dieu_tri  DATE        NOT NULL,
    thoi_gian      TIME        NOT NULL,
    ket_qua        VARCHAR(500) NOT NULL,
    ma_bac_si      VARCHAR(20) NOT NULL,
    ma_benh_nhan   VARCHAR(20) NOT NULL,
    CONSTRAINT fk_ldt_bac_si FOREIGN KEY (ma_bac_si)    REFERENCES bac_si (ma_bac_si),
    CONSTRAINT fk_ldt_bn     FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan (ma_benh_nhan)
) ENGINE=InnoDB;

CREATE TABLE dich_vu (
    ma_dich_vu  VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_dich_vu VARCHAR(200) NOT NULL,
    don_gia     DECIMAL(14,2) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE chi_tiet_dich_vu (
    ma_luot    VARCHAR(20) NOT NULL,
    ma_dich_vu VARCHAR(20) NOT NULL,
    PRIMARY KEY (ma_luot, ma_dich_vu),
    CONSTRAINT fk_ctdv_luot FOREIGN KEY (ma_luot)    REFERENCES luot_dieu_tri (ma_luot),
    CONSTRAINT fk_ctdv_dv   FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu (ma_dich_vu)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 6. Đơn thuốc, Thuốc, Chi tiết đơn thuốc
-- ---------------------------------------------------------------------------
CREATE TABLE don_thuoc (
    ma_don    VARCHAR(20) NOT NULL PRIMARY KEY,
    ngay_ke   DATE        NOT NULL,
    ghi_chu   VARCHAR(500) NULL,
    ma_luot   VARCHAR(20) NOT NULL,
    CONSTRAINT fk_dt_luot FOREIGN KEY (ma_luot) REFERENCES luot_dieu_tri (ma_luot)
) ENGINE=InnoDB;

CREATE TABLE thuoc (
    ma_thuoc    VARCHAR(20) NOT NULL PRIMARY KEY,
    ten_thuoc   VARCHAR(200) NOT NULL,
    thanh_phan  VARCHAR(500) NOT NULL,
    gia_ban     DECIMAL(14,2) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE chi_tiet_don_thuoc (
    ma_don    VARCHAR(20) NOT NULL,
    ma_thuoc  VARCHAR(20) NOT NULL,
    so_luong  INT          NOT NULL,
    PRIMARY KEY (ma_don, ma_thuoc),
    CONSTRAINT fk_ctdt_don   FOREIGN KEY (ma_don)   REFERENCES don_thuoc (ma_don),
    CONSTRAINT fk_ctdt_thuoc FOREIGN KEY (ma_thuoc) REFERENCES thuoc (ma_thuoc)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 7. Hóa đơn
-- ---------------------------------------------------------------------------
CREATE TABLE hoa_don (
    ma_hoa_don  VARCHAR(20) NOT NULL PRIMARY KEY,
    ngay_lap    DATE         NOT NULL,
    tong_tien   DECIMAL(14,2) NOT NULL,
    trang_thai  VARCHAR(50)  NOT NULL,
    ma_benh_nhan VARCHAR(20) NOT NULL,
    CONSTRAINT fk_hd_bn FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan (ma_benh_nhan)
) ENGINE=InnoDB;

-- =============================================================================
-- DỮ LIỆU MẪU
-- =============================================================================
-- =============================================================================
-- SAMPLE DATA (ENGLISH CONTENT, VIETNAMESE SCHEMA)
-- =============================================================================

INSERT INTO khoa (ma_khoa, ten_khoa, so_dien_thoai) VALUES
('K01', 'Internal Medicine', '028-1111-0001'),
('K02', 'Surgery', '028-1111-0002'),
('K03', 'Pediatrics', '028-1111-0003'),
('K04', 'Obstetrics', '028-1111-0004');

INSERT INTO ca_truc (ma_ca, ten_ca, thoi_gian_bat_dau, thoi_gian_ket_thuc) VALUES
('CA1', 'Morning',  '06:00:00', '14:00:00'),
('CA2', 'Afternoon','14:00:00', '22:00:00'),
('CA3', 'Night',    '22:00:00', '06:00:00');

INSERT INTO phong_benh (ma_phong, loai_phong, so_giuong_toi_da) VALUES
('P101', 'Standard', 4),
('P102', 'Standard', 4),
('P201', 'VIP', 2),
('P202', 'VIP', 2),
('P301', 'Standard', 6);

INSERT INTO bac_si (ma_bac_si, ten_bac_si, ngay_vao_lam, chuyen_mon, ma_khoa) VALUES
('BS01', 'John Nguyen', '2015-03-01', 'Endocrinology', 'K01'),
('BS02', 'Anna Tran', '2012-07-15', 'Gastroenterology', 'K01'),
('BS03', 'Michael Le', '2018-01-10', 'Orthopedics', 'K02'),
('BS04', 'David Pham', '2016-11-20', 'Pediatrics', 'K03'),
('BS05', 'Sophia Hoang', '2014-05-05', 'Obstetrics', 'K04');

INSERT INTO y_ta (ma_y_ta, ten_y_ta, nam_kinh_nghiem, ma_khoa) VALUES
('YT01', 'Lily Vo', 5, 'K01'),
('YT02', 'Kevin Dang', 3, 'K02'),
('YT03', 'Emma Bui', 7, 'K03'),
('YT04', 'Ryan Do', 2, 'K04');

INSERT INTO bac_si_ca_truc (ma_bac_si, ma_ca) VALUES
('BS01', 'CA1'), ('BS01', 'CA2'),
('BS02', 'CA2'), ('BS03', 'CA1'),
('BS04', 'CA3'), ('BS05', 'CA1');

INSERT INTO y_ta_ca_truc (ma_y_ta, ma_ca) VALUES
('YT01', 'CA1'), ('YT01', 'CA3'),
('YT02', 'CA2'), ('YT03', 'CA1'),
('YT04', 'CA2');

INSERT INTO benh_nhan (ma_benh_nhan, ten_benh_nhan, ngay_sinh, so_dien_thoai, ma_bac_si, ma_phong) VALUES
('BN001', 'Alice Nguyen', '1988-04-12', '0901111222', 'BS01', 'P101'),
('BN002', 'Bob Le', '1995-09-23', '0903333444', 'BS02', 'P102'),
('BN003', 'Cindy Pham', '2001-01-30', '0905555666', 'BS03', 'P201'),
('BN004', 'Tom Hoang', '2015-06-18', '0907777888', 'BS04', 'P301'),
('BN005', 'Linda Vo', '1992-12-05', NULL, 'BS05', 'P202'),
('BN006', 'Mark Dang', '1979-08-14', '0912000111', 'BS01', 'P101');

INSERT INTO bao_hiem (ma_the, ngay_het_han, muc_huong, ma_benh_nhan) VALUES
('BH001', '2027-12-31', 80.00, 'BN001'),
('BH002', '2026-06-30', 95.00, 'BN002'),
('BH003', '2028-03-15', 100.00, 'BN003'),
('BH004', '2027-01-01', 100.00, 'BN004'),
('BH005', '2026-09-20', 70.00, 'BN005'),
('BH006', '2025-11-30', 80.00, 'BN006');

INSERT INTO dich_vu (ma_dich_vu, ten_dich_vu, don_gia) VALUES
('DV01', 'General Checkup', 200000),
('DV02', 'X-Ray', 350000),
('DV03', 'Ultrasound', 450000),
('DV04', 'Blood Test', 300000),
('DV05', 'Post-Surgery Care', 500000);

INSERT INTO luot_dieu_tri (ma_luot, ngay_dieu_tri, thoi_gian, ket_qua, ma_bac_si, ma_benh_nhan) VALUES
('LDT01', '2025-03-10', '09:30:00', 'Stable condition, continue treatment', 'BS01', 'BN001'),
('LDT02', '2025-03-11', '10:00:00', 'Abdominal pain reduced', 'BS02', 'BN002'),
('LDT03', '2025-03-12', '08:15:00', 'No infection, healing well', 'BS03', 'BN003'),
('LDT04', '2025-03-13', '14:45:00', 'Fever reduced, continue antibiotics', 'BS04', 'BN004'),
('LDT05', '2025-03-14', '11:00:00', 'Normal pregnancy', 'BS05', 'BN005');

INSERT INTO chi_tiet_dich_vu (ma_luot, ma_dich_vu) VALUES
('LDT01', 'DV01'), ('LDT01', 'DV04'),
('LDT02', 'DV01'), ('LDT02', 'DV03'),
('LDT03', 'DV02'),
('LDT04', 'DV01'), ('LDT04', 'DV04'),
('LDT05', 'DV03');

INSERT INTO don_thuoc (ma_don, ngay_ke, ghi_chu, ma_luot) VALUES
('DT01', '2025-03-10', 'Take after meals', 'LDT01'),
('DT02', '2025-03-11', NULL, 'LDT02'),
('DT03', '2025-03-12', 'Use for pain if needed', 'LDT03');

INSERT INTO thuoc (ma_thuoc, ten_thuoc, thanh_phan, gia_ban) VALUES
('TH01', 'Metformin 500mg', 'Metformin', 5000),
('TH02', 'Omeprazole 20mg', 'Omeprazole', 8000),
('TH03', 'Paracetamol 500mg', 'Paracetamol', 2000),
('TH04', 'Amoxicillin 500mg', 'Amoxicillin', 12000);

INSERT INTO chi_tiet_don_thuoc (ma_don, ma_thuoc, so_luong) VALUES
('DT01', 'TH01', 60),
('DT01', 'TH03', 20),
('DT02', 'TH02', 28),
('DT03', 'TH03', 10);

INSERT INTO hoa_don (ma_hoa_don, ngay_lap, tong_tien, trang_thai, ma_benh_nhan) VALUES
('HD01', '2025-03-10', 850000, 'Paid', 'BN001'),
('HD02', '2025-03-11', 1200000, 'Pending', 'BN002'),
('HD03', '2025-03-12', 450000, 'Paid', 'BN003'),
('HD04', '2025-03-15', 200000, 'Paid', 'BN004');