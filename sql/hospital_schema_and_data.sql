-- =============================================================================
-- Hospital DB — schema + seed data (MySQL 8+)
-- Khớp với DATABASE_DESIGN.md và các entity JPA trong project
-- Cập nhật: mã thẻ BHYT theo định dạng thực tế (16 ký tự số),
--           mã lượt điều trị dạng LDT + 7 chữ số ngẫu nhiên.
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
-- 2. Bác sĩ & Phòng bệnh
-- ---------------------------------------------------------------------------
CREATE TABLE bac_si (
    ma_bac_si     VARCHAR(20)  NOT NULL PRIMARY KEY,
    ten_bac_si    VARCHAR(200) NOT NULL,
    ngay_vao_lam  DATE         NOT NULL,
    chuyen_mon    VARCHAR(200) NOT NULL,
    ma_khoa       VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_bac_si_khoa FOREIGN KEY (ma_khoa) REFERENCES khoa (ma_khoa)
) ENGINE=InnoDB;

CREATE TABLE phong_benh (
    ma_phong          VARCHAR(20)   NOT NULL PRIMARY KEY,
    loai_phong        VARCHAR(20)   NOT NULL,
    so_giuong_toi_da  INT           NOT NULL,
    don_gia           DECIMAL(14,2) NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 3. Bệnh nhân & Bảo hiểm
-- ---------------------------------------------------------------------------
CREATE TABLE benh_nhan (
    ma_benh_nhan   VARCHAR(20)  NOT NULL PRIMARY KEY,
    ten_benh_nhan  VARCHAR(200) NOT NULL,
    ngay_sinh      DATE         NOT NULL,
    so_dien_thoai  VARCHAR(30)  NULL
) ENGINE=InnoDB;

-- ma_the: 16 chữ số theo định dạng thẻ BHYT Việt Nam
CREATE TABLE bao_hiem (
    ma_the        VARCHAR(20)   NOT NULL PRIMARY KEY,
    ngay_het_han  DATE          NOT NULL,
    muc_huong     DECIMAL(5,2)  NOT NULL,
    ma_benh_nhan  VARCHAR(20)   NOT NULL UNIQUE,
    CONSTRAINT fk_bh_bn FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan (ma_benh_nhan)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 4. Lượt điều trị, Dịch vụ, Chi tiết dịch vụ
-- ---------------------------------------------------------------------------
CREATE TABLE luot_dieu_tri (
    ma_luot        VARCHAR(20)  NOT NULL PRIMARY KEY,
    ngay_bat_dau   DATE         NOT NULL,
    ngay_ket_thuc  DATE         NULL,
    trang_thai     VARCHAR(50)  NOT NULL,
    ket_qua        VARCHAR(500) NULL,
    ma_bac_si      VARCHAR(20)  NOT NULL,
    ma_benh_nhan   VARCHAR(20)  NOT NULL,
    ma_phong       VARCHAR(20)  NULL,
    CONSTRAINT fk_ldt_bac_si FOREIGN KEY (ma_bac_si)    REFERENCES bac_si (ma_bac_si),
    CONSTRAINT fk_ldt_bn     FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan (ma_benh_nhan),
    CONSTRAINT fk_ldt_phong  FOREIGN KEY (ma_phong)     REFERENCES phong_benh (ma_phong)
) ENGINE=InnoDB;

CREATE TABLE dich_vu (
    ma_dich_vu  VARCHAR(20)   NOT NULL PRIMARY KEY,
    ten_dich_vu VARCHAR(200)  NOT NULL,
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
-- 5. Đơn thuốc, Thuốc, Chi tiết đơn thuốc
-- ---------------------------------------------------------------------------
CREATE TABLE don_thuoc (
    ma_don    VARCHAR(20)  NOT NULL PRIMARY KEY,
    ngay_ke   DATE         NOT NULL,
    ghi_chu   VARCHAR(500) NULL,
    ma_luot   VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_dt_luot FOREIGN KEY (ma_luot) REFERENCES luot_dieu_tri (ma_luot)
) ENGINE=InnoDB;

CREATE TABLE thuoc (
    ma_thuoc    VARCHAR(20)   NOT NULL PRIMARY KEY,
    ten_thuoc   VARCHAR(200)  NOT NULL,
    thanh_phan  VARCHAR(500)  NOT NULL,
    gia_ban     DECIMAL(14,2) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE chi_tiet_don_thuoc (
    ma_don    VARCHAR(20) NOT NULL,
    ma_thuoc  VARCHAR(20) NOT NULL,
    so_luong  INT         NOT NULL,
    PRIMARY KEY (ma_don, ma_thuoc),
    CONSTRAINT fk_ctdt_don   FOREIGN KEY (ma_don)   REFERENCES don_thuoc (ma_don),
    CONSTRAINT fk_ctdt_thuoc FOREIGN KEY (ma_thuoc) REFERENCES thuoc (ma_thuoc)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- 6. Hóa đơn
-- ---------------------------------------------------------------------------
CREATE TABLE hoa_don (
    ma_hoa_don  VARCHAR(20)   NOT NULL PRIMARY KEY,
    ngay_lap    DATE          NOT NULL,
    tong_tien   DECIMAL(14,2) NOT NULL,
    trang_thai  VARCHAR(50)   NOT NULL,
    ma_luot     VARCHAR(20)   NOT NULL UNIQUE,
    CONSTRAINT fk_hd_luot FOREIGN KEY (ma_luot) REFERENCES luot_dieu_tri (ma_luot)
) ENGINE=InnoDB;

-- =============================================================================
-- DỮ LIỆU MẪU
-- =============================================================================

-- 1. Khoa
INSERT INTO khoa (ma_khoa, ten_khoa, so_dien_thoai) VALUES
('K01', 'Internal Medicine', '028-1111-0001'),
('K02', 'Surgery',           '028-1111-0002'),
('K03', 'Pediatrics',        '028-1111-0003'),
('K04', 'Obstetrics',        '028-1111-0004');

-- 2. Phòng bệnh
INSERT INTO phong_benh (ma_phong, loai_phong, so_giuong_toi_da, don_gia) VALUES
('P101', 'Standard', 4, 150000),
('P102', 'Standard', 4, 150000),
('P201', 'VIP',      2, 500000),
('P202', 'VIP',      2, 500000),
('P301', 'Standard', 6, 120000);

-- 3. Bác sĩ
INSERT INTO bac_si (ma_bac_si, ten_bac_si, ngay_vao_lam, chuyen_mon, ma_khoa) VALUES
('BS01', 'John Nguyen',  '2015-03-01', 'Endocrinology',    'K01'),
('BS02', 'Anna Tran',    '2012-07-15', 'Gastroenterology', 'K01'),
('BS03', 'Michael Le',   '2018-01-10', 'Orthopedics',      'K02'),
('BS04', 'David Pham',   '2016-11-20', 'Pediatrics',       'K03'),
('BS05', 'Sophia Hoang', '2014-05-05', 'Obstetrics',       'K04');

-- 4. Bệnh nhân
INSERT INTO benh_nhan (ma_benh_nhan, ten_benh_nhan, ngay_sinh, so_dien_thoai) VALUES
('BN001', 'Alice Nguyen', '1988-04-12', '0901111222'),
('BN002', 'Bob Le',       '1995-09-23', '0903333444'),
('BN003', 'Cindy Pham',   '2001-01-30', '0905555666'),
('BN004', 'Tom Hoang',    '2015-06-18', '0907777888'),
('BN005', 'Linda Vo',     '1992-12-05', NULL),
('BN006', 'Mark Dang',    '1979-08-14', '0912000111');

-- 5. Bảo hiểm y tế
-- Định dạng ma_the: 16 chữ số theo chuẩn thẻ BHYT Việt Nam
--   [2 số đối tượng][3 mã tỉnh/cơ quan][4 năm sinh][ngày tháng sinh][3 số thứ tự]
INSERT INTO bao_hiem (ma_the, ngay_het_han, muc_huong, ma_benh_nhan) VALUES
('4030019881209001', '2027-12-31', 80.00,  'BN001'),  -- Alice Nguyen, 80% BHYT
('4030019950923002', '2026-06-30', 95.00,  'BN002'),  -- Bob Le, 95% BHYT
('4050020010130003', '2028-03-15', 100.00, 'BN003'),  -- Cindy Pham, 100% (miễn phí hoàn toàn)
('4050020150618004', '2027-01-01', 100.00, 'BN004'),  -- Tom Hoang, 100% (trẻ em)
('4060019921205005', '2026-09-20', 70.00,  'BN005'),  -- Linda Vo, 70% BHYT
('4030019790814006', '2025-11-30', 80.00,  'BN006');  -- Mark Dang, 80% BHYT (hết hạn)

-- 6. Dịch vụ
INSERT INTO dich_vu (ma_dich_vu, ten_dich_vu, don_gia) VALUES
('DV01', 'General Checkup',   200000),
('DV02', 'X-Ray',             350000),
('DV03', 'Ultrasound',        450000),
('DV04', 'Blood Test',        300000),
('DV05', 'Post-Surgery Care', 500000);

-- 7. Lượt điều trị  (mã dạng LDT + 7 chữ số)
INSERT INTO luot_dieu_tri
    (ma_luot, ngay_bat_dau, ngay_ket_thuc, trang_thai, ket_qua, ma_bac_si, ma_benh_nhan, ma_phong)
VALUES
('LDT1839201', '2025-03-08', '2025-03-10', 'Đã xuất viện', 'Stable condition, continue treatment',  'BS01', 'BN001', 'P101'),
('LDT2948102', '2025-03-11', '2025-03-11', 'Đã xuất viện', 'Abdominal pain reduced',                'BS02', 'BN002', NULL),
('LDT3495803', '2025-03-10', '2025-03-12', 'Đã xuất viện', 'No infection, healing well',            'BS03', 'BN003', 'P201'),
('LDT4586904', '2025-03-15', '2025-03-15', 'Đã xuất viện', 'Fever reduced, continue antibiotics',   'BS04', 'BN004', NULL),
('LDT5687005', '2025-03-14', NULL,          'Đang điều trị','Normal pregnancy',                      'BS05', 'BN005', 'P202'),
('LDT6798106', '2025-03-18', '2025-03-18', 'Đã xuất viện', 'Follow-up check',                       'BS01', 'BN001', NULL);

-- 8. Chi tiết dịch vụ
INSERT INTO chi_tiet_dich_vu (ma_luot, ma_dich_vu) VALUES
('LDT1839201', 'DV01'), ('LDT1839201', 'DV04'),
('LDT2948102', 'DV01'), ('LDT2948102', 'DV03'),
('LDT3495803', 'DV02'),
('LDT4586904', 'DV01'), ('LDT4586904', 'DV04'),
('LDT5687005', 'DV03'),
('LDT6798106', 'DV01');

-- 9. Đơn thuốc
INSERT INTO don_thuoc (ma_don, ngay_ke, ghi_chu, ma_luot) VALUES
('DT01', '2025-03-10', 'Take after meals',         'LDT1839201'),
('DT02', '2025-03-11', NULL,                        'LDT2948102'),
('DT03', '2025-03-12', 'Use for pain if needed',   'LDT3495803'),
('DT04', '2025-03-15', 'Take strictly',            'LDT4586904'),
('DT05', '2025-03-18', 'Follow up',                'LDT6798106');

-- 10. Thuốc
INSERT INTO thuoc (ma_thuoc, ten_thuoc, thanh_phan, gia_ban) VALUES
('TH01', 'Metformin 500mg',   'Metformin',   5000),
('TH02', 'Omeprazole 20mg',   'Omeprazole',  8000),
('TH03', 'Paracetamol 500mg', 'Paracetamol', 2000),
('TH04', 'Amoxicillin 500mg', 'Amoxicillin', 12000);

-- 11. Chi tiết đơn thuốc
INSERT INTO chi_tiet_don_thuoc (ma_don, ma_thuoc, so_luong) VALUES
('DT01', 'TH01', 60),
('DT01', 'TH03', 20),
('DT02', 'TH02', 28),
('DT03', 'TH03', 10),
('DT04', 'TH04', 14),
('DT05', 'TH03', 10);

-- 12. Hóa đơn
INSERT INTO hoa_don (ma_hoa_don, ngay_lap, tong_tien, trang_thai, ma_luot) VALUES
('HD01', '2025-03-10', 228000, 'Đã thanh toán',   'LDT1839201'),
('HD02', '2025-03-11',  43700, 'Chưa thanh toán', 'LDT2948102'),
('HD03', '2025-03-12',      0, 'Đã thanh toán',   'LDT3495803'),
('HD04', '2025-03-15',      0, 'Đã thanh toán',   'LDT4586904'),
('HD05', '2025-03-18',  44000, 'Đã thanh toán',   'LDT6798106');