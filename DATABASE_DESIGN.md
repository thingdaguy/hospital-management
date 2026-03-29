# Hệ thống Quản lý Bệnh viện Đa khoa

## Mô tả bài toán

Một bệnh viện đa khoa lớn cần xây dựng hệ thống cơ sở dữ liệu toàn diện để quản lý các hoạt động khám chữa bệnh.

Bệnh viện được chia thành nhiều **Khoa** (Nội, Ngoại, Nhi, Sản...).  
Mỗi khoa có:
- Mã khoa (PK)
- Tên khoa
- Số điện thoại liên hệ

---

## ️ Nhân sự

### Bác sĩ
- Mã bác sĩ (PK)
- Tên bác sĩ
- Ngày vào làm
- Chuyên môn
- Thuộc về một khoa (FK → Khoa)

### Y tá
- Mã y tá (PK)
- Tên y tá
- Năm kinh nghiệm
- Thuộc về một khoa (FK → Khoa)

Một khoa có nhiều bác sĩ và y tá.  
Mỗi bác sĩ/y tá chỉ thuộc một khoa.

---

## Ca trực

- Mã ca (PK)
- Tên ca (Sáng, Chiều, Đêm)
- Thời gian bắt đầu
- Thời gian kết thúc

Quan hệ:
- Bác sĩ (N-N) Ca trực
- Y tá (N-N) Ca trực

---

## ️ Bệnh nhân

- Mã bệnh nhân (PK)
- Tên bệnh nhân
- Ngày sinh
- Số điện thoại
- Mã bác sĩ tiếp nhận (FK → Bác sĩ)
- Mã phòng (FK → Phòng bệnh)

Mỗi bệnh nhân:
- Được 1 bác sĩ tiếp nhận
- Ở 1 phòng bệnh

---

## Bảo hiểm y tế

- Mã thẻ (PK)
- Ngày hết hạn
- Mức hưởng (%)
- Mã bệnh nhân (FK, UNIQUE)

Quan hệ:
- Bệnh nhân (1-1) Bảo hiểm

---

## Phòng bệnh

- Mã phòng (PK)
- Loại phòng (Thường, VIP)
- Số giường tối đa

Quan hệ:
- Phòng (1-N) Bệnh nhân

---

## Lượt điều trị

- Mã lượt (PK)
- Ngày điều trị
- Thời gian
- Kết quả
- Mã bác sĩ (FK)
- Mã bệnh nhân (FK)

Quan hệ:
- Bác sĩ (1-N) Lượt điều trị
- Bệnh nhân (1-N) Lượt điều trị

---

## Dịch vụ y tế

- Mã dịch vụ (PK)
- Tên dịch vụ
- Đơn giá

### Chi tiết dịch vụ
- (Mã lượt, Mã dịch vụ) (PK)

Quan hệ:
- Lượt điều trị (N-N) Dịch vụ

---

## Đơn thuốc

- Mã đơn (PK)
- Ngày kê
- Ghi chú
- Mã lượt (FK)

Quan hệ:
- Lượt điều trị (1-N) Đơn thuốc

---

## Thuốc

- Mã thuốc (PK)
- Tên thuốc
- Thành phần
- Giá bán

### Chi tiết đơn thuốc
- (Mã đơn, Mã thuốc) (PK)
- Số lượng

Quan hệ:
- Đơn thuốc (N-N) Thuốc

---

## Hóa đơn

- Mã hóa đơn (PK)
- Ngày lập
- Tổng tiền
- Trạng thái
- Mã bệnh nhân (FK)

Quan hệ:
- Bệnh nhân (1-N) Hóa đơn

---

# Ràng buộc (Relationships)

## 1. Cơ cấu tổ chức
- Khoa (1) — (N) Bác sĩ
- Khoa (1) — (N) Y tá
- Bác sĩ (N) — (N) Ca trực
- Y tá (N) — (N) Ca trực

## 2. Tiếp nhận & Lưu trú
- Bác sĩ (1) — (N) Bệnh nhân
- Bệnh nhân (1) — (1) Bảo hiểm
- Phòng (1) — (N) Bệnh nhân

## 3. Khám chữa bệnh
- Bác sĩ (1) — (N) Lượt điều trị
- Bệnh nhân (1) — (N) Lượt điều trị
- Lượt điều trị (N) — (N) Dịch vụ
- Lượt điều trị (1) — (N) Đơn thuốc
- Đơn thuốc (N) — (N) Thuốc

## 4. Thanh toán
- Bệnh nhân (1) — (N) Hóa đơn

---

# Phụ thuộc hàm (Functional Dependencies)

## 1. Khoa
ma_khoa → ten_khoa, so_dien_thoai

## 2. Bác sĩ
ma_bac_si → ten_bac_si, ngay_vao_lam, chuyen_mon, ma_khoa

## 3. Y tá
ma_y_ta → ten_y_ta, nam_kinh_nghiem, ma_khoa

## 4. Ca trực
ma_ca → ten_ca, thoi_gian_bat_dau, thoi_gian_ket_thuc

## 5. Bác sĩ - Ca trực
(ma_bac_si, ma_ca) → ∅

## 6. Y tá - Ca trực
(ma_y_ta, ma_ca) → ∅

## 7. Bệnh nhân
ma_benh_nhan → ten_benh_nhan, ngay_sinh, so_dien_thoai, ma_bac_si, ma_phong

## 8. Bảo hiểm
ma_the → ngay_het_han, muc_huong, ma_benh_nhan  
ma_benh_nhan → ma_the

## 9. Phòng bệnh
ma_phong → loai_phong, so_giuong_toi_da

## 10. Lượt điều trị
ma_luot → ngay_dieu_tri, thoi_gian, ket_qua, ma_bac_si, ma_benh_nhan

## 11. Dịch vụ
ma_dich_vu → ten_dich_vu, don_gia

## 12. Chi tiết dịch vụ
(ma_luot, ma_dich_vu) → ∅

## 13. Đơn thuốc
ma_don → ngay_ke, ghi_chu, ma_luot

## 14. Thuốc
ma_thuoc → ten_thuoc, thanh_phan, gia_ban

## 15. Chi tiết đơn thuốc
(ma_don, ma_thuoc) → so_luong

## 16. Hóa đơn
ma_hoa_don → ngay_lap, tong_tien, trang_thai, ma_benh_nhan


# Database Schema

## 1. khoa (Department)
| Column          | Type         | Constraints        |
|-----------------|-------------|-------------------|
| ma_khoa         | VARCHAR(20) | PK                |
| ten_khoa        | VARCHAR(200)| NOT NULL          |
| so_dien_thoai   | VARCHAR(30) | NULL              |

## 2. bac_si (Doctor)
| Column         | Type         | Constraints                          |
|----------------|-------------|--------------------------------------|
| ma_bac_si      | VARCHAR(20) | PK                                   |
| ten_bac_si     | VARCHAR(200)| NOT NULL                             |
| ngay_vao_lam   | DATE        | NOT NULL                             |
| chuyen_mon     | VARCHAR(200)| NOT NULL                             |
| ma_khoa        | VARCHAR(20) | FK → khoa(ma_khoa)                   |

## 3. y_ta (Nurse)
| Column            | Type         | Constraints                        |
|-------------------|-------------|------------------------------------|
| ma_y_ta           | VARCHAR(20) | PK                                 |
| ten_y_ta          | VARCHAR(200)| NOT NULL                           |
| nam_kinh_nghiem   | INT         | NOT NULL                           |
| ma_khoa           | VARCHAR(20) | FK → khoa(ma_khoa)                 |

## 4. ca_truc (Shift)
| Column               | Type        | Constraints |
|----------------------|------------|-------------|
| ma_ca                | VARCHAR(20)| PK          |
| ten_ca               | VARCHAR(50)| NOT NULL    |
| thoi_gian_bat_dau    | TIME       | NOT NULL    |
| thoi_gian_ket_thuc   | TIME       | NOT NULL    |

## 5. phong_benh (Room)
| Column            | Type         | Constraints |
|-------------------|-------------|-------------|
| ma_phong          | VARCHAR(20) | PK          |
| loai_phong        | VARCHAR(20) | NOT NULL    |
| so_giuong_toi_da  | INT         | NOT NULL    |

## 6. bac_si_ca_truc (Doctor-Shift)
| Column      | Type         | Constraints                          |
|-------------|-------------|--------------------------------------|
| ma_bac_si   | VARCHAR(20) | PK, FK → bac_si(ma_bac_si)           |
| ma_ca       | VARCHAR(20) | PK, FK → ca_truc(ma_ca)              |

## 7. y_ta_ca_truc (Nurse-Shift)
| Column    | Type         | Constraints                        |
|-----------|-------------|------------------------------------|
| ma_y_ta   | VARCHAR(20) | PK, FK → y_ta(ma_y_ta)             |
| ma_ca     | VARCHAR(20) | PK, FK → ca_truc(ma_ca)            |

## 8. benh_nhan (Patient)
| Column           | Type         | Constraints                          |
|------------------|-------------|--------------------------------------|
| ma_benh_nhan     | VARCHAR(20) | PK                                   |
| ten_benh_nhan    | VARCHAR(200)| NOT NULL                             |
| ngay_sinh        | DATE        | NOT NULL                             |
| so_dien_thoai    | VARCHAR(30) | NULL                                 |
| ma_bac_si        | VARCHAR(20) | FK → bac_si(ma_bac_si)               |
| ma_phong         | VARCHAR(20) | FK → phong_benh(ma_phong)            |

## 9. bao_hiem (Insurance)
| Column          | Type          | Constraints                          |
|-----------------|--------------|--------------------------------------|
| ma_the          | VARCHAR(20)  | PK                                   |
| ngay_het_han    | DATE         | NOT NULL                             |
| muc_huong       | DECIMAL(5,2) | NOT NULL                             |
| ma_benh_nhan    | VARCHAR(20)  | UNIQUE, FK → benh_nhan(ma_benh_nhan) |

## 10. luot_dieu_tri (Treatment Visit)
| Column          | Type         | Constraints                          |
|-----------------|-------------|--------------------------------------|
| ma_luot         | VARCHAR(20) | PK                                   |
| ngay_dieu_tri   | DATE        | NOT NULL                             |
| thoi_gian       | TIME        | NOT NULL                             |
| ket_qua         | VARCHAR(500)| NOT NULL                             |
| ma_bac_si       | VARCHAR(20) | FK → bac_si(ma_bac_si)               |
| ma_benh_nhan    | VARCHAR(20) | FK → benh_nhan(ma_benh_nhan)         |

## 11. dich_vu (Service)
| Column        | Type          | Constraints |
|---------------|--------------|-------------|
| ma_dich_vu    | VARCHAR(20)  | PK          |
| ten_dich_vu   | VARCHAR(200) | NOT NULL    |
| don_gia       | DECIMAL(14,2)| NOT NULL    |

## 12. chi_tiet_dich_vu (Service Detail)
| Column        | Type         | Constraints                          |
|---------------|-------------|--------------------------------------|
| ma_luot       | VARCHAR(20) | PK, FK → luot_dieu_tri(ma_luot)      |
| ma_dich_vu    | VARCHAR(20) | PK, FK → dich_vu(ma_dich_vu)         |

## 13. don_thuoc (Prescription)
| Column      | Type         | Constraints                          |
|-------------|-------------|--------------------------------------|
| ma_don      | VARCHAR(20) | PK                                   |
| ngay_ke     | DATE        | NOT NULL                             |
| ghi_chu     | VARCHAR(500)| NULL                                 |
| ma_luot     | VARCHAR(20) | FK → luot_dieu_tri(ma_luot)          |

## 14. thuoc (Medicine)
| Column        | Type          | Constraints |
|---------------|--------------|-------------|
| ma_thuoc      | VARCHAR(20)  | PK          |
| ten_thuoc     | VARCHAR(200) | NOT NULL    |
| thanh_phan    | VARCHAR(500) | NOT NULL    |
| gia_ban       | DECIMAL(14,2)| NOT NULL    |

## 15. chi_tiet_don_thuoc (Prescription Detail)
| Column      | Type         | Constraints                          |
|-------------|-------------|--------------------------------------|
| ma_don      | VARCHAR(20) | PK, FK → don_thuoc(ma_don)           |
| ma_thuoc    | VARCHAR(20) | PK, FK → thuoc(ma_thuoc)             |
| so_luong    | INT         | NOT NULL                             |

## 16. hoa_don (Invoice)
| Column         | Type          | Constraints                          |
|----------------|--------------|--------------------------------------|
| ma_hoa_don     | VARCHAR(20)  | PK                                   |
| ngay_lap       | DATE         | NOT NULL                             |
| tong_tien      | DECIMAL(14,2)| NOT NULL                             |
| trang_thai     | VARCHAR(50)  | NOT NULL                             |
| ma_benh_nhan   | VARCHAR(20)  | FK → benh_nhan(ma_benh_nhan)         |