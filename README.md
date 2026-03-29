# Hệ thống quản lý bệnh viện đa khoa

Ứng dụng mẫu theo **Java Swing MVC** + **JPA/Hibernate**, bám [DATABASE_DESIGN.md](DATABASE_DESIGN.md) và yêu cầu trong [ASSIGNMENT.md](ASSIGNMENT.md). Hiện tại đã có **cấu trúc thư mục đầy đủ các tầng** (Entity / DTO / DAO / Service / Controller / View / `MainApp`) và tính năng **danh sách + tìm kiếm bệnh nhân** trên form chính.

## Yêu cầu môi trường

| Thành phần | Phiên bản gợi ý |
|------------|-----------------|
| JDK        | 17+             |
| Maven      | 3.8+            |
| MySQL      | 8.x             |

## Cài đặt nhanh

### 1. Tạo database và nạp dữ liệu

Đăng nhập MySQL (CLI hoặc Workbench) với cmd: 
```text
mysql -u root -p
```
sau đó dán toàn bộ file:

```text
sql/hospital_schema_and_data.sql
```

Script sẽ tạo database `hospital_db`, toàn bộ bảng và dữ liệu mẫu (khoa, bác sĩ, bệnh nhân, lượt điều trị, v.v.).

### 2. Cấu hình kết nối JPA

Mở `src/main/resources/META-INF/persistence.xml` và chỉnh cho khớp máy:

- `jakarta.persistence.jdbc.url` — host, port, tên DB (mặc định `hospital_db`)
- `jakarta.persistence.jdbc.user` — user MySQL
- `jakarta.persistence.jdbc.password` — mật khẩu

`hibernate.hbm2ddl.auto` đang là **`validate`**: Hibernate chỉ kiểm tra entity khớp schema, **không** tự tạo/sửa bảng (schema do file SQL định nghĩa).

### 3. Biên dịch và chạy

Trong thư mục gốc project (nơi có `pom.xml`):

```bash
mvn compile exec:java
```

Hoặc chạy class `com.hospital.app.MainApp` từ IDE.

Sau khi chạy, cửa sổ **Danh sách bệnh nhân** hiển thị bảng; dùng **Tìm kiếm** (theo tên) hoặc **Tải lại** để nạp lại toàn bộ.

## Cấu trúc thư mục (tóm tắt)

```text
BigProject/
├── pom.xml
├── README.md
├── ASSIGNMENT.md
├── DATABASE_DESIGN.md
├── sql/
│   └── hospital_schema_and_data.sql    # DDL + INSERT mẫu
└── src/main/
    ├── java/com/hospital/app/
    │   ├── MainApp.java                 # Điểm vào ứng dụng
    │   ├── entity/                      # Entity JPA (quan hệ như ERD)
    │   ├── dto/                         # DTO cho View (ví dụ hàng bảng BN)
    │   ├── dao/                         # JPQL / EntityManager
    │   ├── service/                     # Nghiệp vụ, map Entity → DTO
    │   ├── controller/                  # Gắn View ↔ Service
    │   ├── view/                        # Swing: MainForm (SubForm bổ sung sau)
    │   └── util/                        # JpaUtil (EntityManagerFactory)
    └── resources/META-INF/
        └── persistence.xml
```

## JPQL đã có (mở rộng dần theo ASSIGNMENT)

- `BenhNhanDAO.findAllWithBacSiAndPhong()` — danh sách BN + `JOIN FETCH` bác sĩ tiếp nhận, phòng
- `BenhNhanDAO.searchByTenContaining(...)` — tìm theo tên (LIKE, không phân biệt hoa thường)

Có thể thêm các DAO/Service khác (khoa, hóa đơn, thống kê…) theo cùng pattern.

## Gợi ý cho dev mới

1. Đọc `DATABASE_DESIGN.md` để nắm thực thể và quan hệ.
2. Entity nằm trong `entity/`; khi đổi cột/bảng, cập nhật **cả** SQL và annotation cho khớp (`validate`).
3. Thêm màn hình: tạo `SubForm` mới + Controller tương ứng, gọi từ menu trên `MainForm` khi cần.

## Ghi chú

- Dự án dùng **Lombok** (`@Getter`, `@Setter`, …) để giảm boilerplate; IDE cần bật annotation processing (Maven đã cấu hình `maven-compiler-plugin`).
- Khi thoát app, `JpaUtil.shutdown()` được gọi qua shutdown hook để đóng `EntityManagerFactory` gọn gàng.
