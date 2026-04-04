package com.hospital.app.controller;

import com.hospital.app.dto.LuotDieuTriRowDTO;
import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.BenhNhan;
import com.hospital.app.entity.LuotDieuTri;
import com.hospital.app.entity.PhongBenh;
import com.hospital.app.service.BacSiService;
import com.hospital.app.service.BenhNhanService;
import com.hospital.app.service.HoaDonService;
import com.hospital.app.service.LuotDieuTriService;
import com.hospital.app.service.PhongBenhService;
import com.hospital.app.service.DonThuocService;
import com.hospital.app.service.ThuocService;
import com.hospital.app.view.HoaDonDialog;
import com.hospital.app.view.LuotDieuTriDialog;
import com.hospital.app.view.MainForm;
import com.hospital.app.view.PrescriptionDialog;
import com.hospital.app.entity.Thuoc;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý tab Lượt Điều Trị trong MainForm.
 * Xử lí toàn bộ vòng đời của lượt khám: tiếp nhận, sửa, hiển thị hóa đơn và xuất viện.
 */
public class LuotDieuTriTabController {

    private final MainForm view;
    private final LuotDieuTriService luotDieuTriService;
    private final BenhNhanService benhNhanService;
    private final BacSiService bacSiService;
    private final PhongBenhService phongBenhService;
    private final HoaDonService hoaDonService;
    private final DonThuocService donThuocService;
    private final ThuocService thuocService;

    /**
     * Khởi tạo controller bằng cách inject các service cần thiết và găn sự kiện UI.
     */
    public LuotDieuTriTabController(MainForm view, LuotDieuTriService luotDieuTriService,
                                    BenhNhanService benhNhanService, BacSiService bacSiService,
                                    PhongBenhService phongBenhService, HoaDonService hoaDonService,
                                    DonThuocService donThuocService, ThuocService thuocService) {
        this.view = view;
        this.luotDieuTriService = luotDieuTriService;
        this.benhNhanService = benhNhanService;
        this.bacSiService = bacSiService;
        this.phongBenhService = phongBenhService;
        this.hoaDonService = hoaDonService;
        this.donThuocService = donThuocService;
        this.thuocService = thuocService;
        wireEvents();
    }

    /**
     * Gắn các bộ lắng nghe sự kiện (ActionListener) cho các thành phần UI trong Tab Lượt điều trị.
     * Tự động nạp dữ liệu ban đầu khi khởi tạo.
     */
    private void wireEvents() {
        // Sự kiện Tìm kiếm
        view.getBtnSearchLDT().addActionListener(e -> search());
        // Sự kiện Làm mới danh sách
        view.getBtnRefreshLDT().addActionListener(e -> loadData());
        // Sự kiện Tiếp nhận bệnh nhân mới
        view.getBtnAddLDT().addActionListener(e -> addEncounter());
        // Sự kiện Chỉnh sửa thông tin lượt điều trị
        view.getBtnEditLDT().addActionListener(e -> editEncounter());
        // Sự kiện Xem lịch sử hóa đơn
        view.getBtnInvoiceLDT().addActionListener(e -> showInvoices());
        // Sự kiện Kê đơn thuốc
        view.getBtnPrescribeLDT().addActionListener(e -> prescribe());
        // Sự kiện Thực hiện thủ tục xuất viện
        view.getBtnDischargeLDT().addActionListener(e -> discharge());
        
        // Nạp dữ liệu mặc định lần đầu
        loadData();
    }

    /** 
     * Nạp toàn bộ danh sách lượt điều trị (kèm thông tin BN, BS, Phòng) từ Service lên bảng hiển thị.
     */
    private void loadData() {
        List<LuotDieuTriRowDTO> rows = luotDieuTriService.listAllForTable();
        view.populateEncounterTable(rows);
    }

    /** 
     * Truy xuất và lọc danh sách lượt điều trị dựa trên từ khóa tên bệnh nhân. 
     */
    private void search() {
        String kw = view.getSearchFieldLDT().getText();
        List<LuotDieuTriRowDTO> rows = luotDieuTriService.searchByPatientName(kw);
        view.populateEncounterTable(rows);
    }

    /**
     * Nghiệp vụ Tiếp nhận bệnh nhân mới:
     * 1. Chuẩn bị danh sách dữ liệu thực tế (BN, BS, Phòng) để hiển thị trong Dialog.
     * 2. Kiểm tra sức chứa hiện tại của các phòng bệnh.
     * 3. Hiển thị Dialog nhập liệu cho người dùng.
     */
    private void addEncounter() {
        try {
            // Lấy dữ liệu danh mục để đổ vào các ComboBox trong Dialog
            List<BenhNhan> bnList = new com.hospital.app.dao.BenhNhanDAO().findAllWithBacSiAndPhong();
            List<BacSi> bsList = bacSiService.findAll();
            List<PhongBenh> pbList = phongBenhService.findAll();

            // Tính toán số giường đang sử dụng trong mỗi phòng để cảnh báo khi chọn phòng
            Map<String, Long> occupancy = new HashMap<>();
            for (PhongBenh pb : pbList) {
                occupancy.put(pb.getMaPhong(), benhNhanService.countByPhong(pb.getMaPhong()));
            }

            // Hiển thị cửa sổ nhập liệu
            LuotDieuTriDialog dlg = new LuotDieuTriDialog(view, "Tiếp Nhận Bệnh Nhân", bnList, bsList, pbList, occupancy, null);
            dlg.setVisible(true);

            if (dlg.isOk()) {
                // Thu thập dữ liệu từ Dialog và gán vào Object Lượt điều trị
                LuotDieuTri ldt = dlg.getLuotDieuTri();
                
                // Thiết lập các quan hệ liên kết (BN, BS, Phòng)
                String maBn = dlg.getSelectedMaBenhNhan();
                BenhNhan bn = new BenhNhan();
                bn.setMaBenhNhan(maBn);
                ldt.setBenhNhan(bn);
                
                String maBs = dlg.getSelectedMaBacSi();
                BacSi bs = new BacSi();
                bs.setMaBacSi(maBs);
                ldt.setBacSiDieuTri(bs);
                
                String maPb = dlg.getSelectedMaPhong();
                if (maPb != null) {
                    PhongBenh pb = new PhongBenh();
                    pb.setMaPhong(maPb);
                    ldt.setPhongBenh(pb);
                }
                
                // Lưu vào CSDL thông qua Service (Bao gồm logic check trùng lượt điều trị)
                luotDieuTriService.save(ldt);
                loadData(); // Cập nhật lại giao diện
                JOptionPane.showMessageDialog(view, "Tiếp nhận thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // Xử lý lỗi: Hiển thị popup thông báo nếu có lỗi nghiệp vụ (ví dụ: BN đang điều trị chưa ra viện)
            JOptionPane.showMessageDialog(view, "Lỗi tiếp nhận: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Chỉnh sửa thông tin lượt điều trị:
     * Cho phép thay đổi bác sĩ hoặc chuyển phòng bệnh cho bệnh nhân đang trong viện.
     */
    private void editEncounter() {
        // Lấy ID từ dòng đang được chọn trên bảng
        String id = view.getSelectedEncounterId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lượt điều trị để sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Tải thông tin chi tiết lượt điều trị
            LuotDieuTri ldt = luotDieuTriService.findByIdWithDetails(id);
            if (ldt == null) return;
            
            // Chặn sửa đổi nếu bệnh nhân đã xuất viện (Trạng thái cuối cùng)
            if (ldt.getNgayKetThuc() != null) {
                JOptionPane.showMessageDialog(view, "Lượt điều trị này đã xuất viện, không thể sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chuẩn bị dữ liệu phục vụ Dialog sửa đổi
            List<BenhNhan> bnList = new com.hospital.app.dao.BenhNhanDAO().findAllWithBacSiAndPhong();
            List<BacSi> bsList = bacSiService.findAll();
            List<PhongBenh> pbList = phongBenhService.findAll();

            Map<String, Long> occupancy = new HashMap<>();
            for (PhongBenh pb : pbList) {
                occupancy.put(pb.getMaPhong(), benhNhanService.countByPhong(pb.getMaPhong()));
            }

            LuotDieuTriDialog dlg = new LuotDieuTriDialog(view, "Sửa Lượt Điều Trị", bnList, bsList, pbList, occupancy, ldt);
            dlg.setVisible(true);

            if (dlg.isOk()) {
                // Cập nhật lại các thông tin liên kết mới
                String maBs = dlg.getSelectedMaBacSi();
                BacSi bs = new BacSi();
                bs.setMaBacSi(maBs);
                ldt.setBacSiDieuTri(bs);
                
                String maPb = dlg.getSelectedMaPhong();
                if (maPb != null) {
                    PhongBenh pb = new PhongBenh();
                    pb.setMaPhong(maPb);
                    ldt.setPhongBenh(pb);
                } else {
                    ldt.setPhongBenh(null);
                }

                // Ghi nhận thay đổi vào Database
                luotDieuTriService.update(ldt);
                loadData();
                JOptionPane.showMessageDialog(view, "Sửa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi sửa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Truy xuất và hiển thị danh sách các hóa đơn liên quan đến bệnh nhân của lượt điều trị này.
     */
    private void showInvoices() {
        String id = view.getSelectedEncounterId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lượt điều trị để xem hóa đơn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LuotDieuTri ldt = luotDieuTriService.findByIdWithDetails(id);
            if (ldt == null) return;
            
            // Lấy lịch sử hóa đơn của bệnh nhân
            var hoaDons = hoaDonService.listByBenhNhan(ldt.getBenhNhan().getMaBenhNhan());
            // Mở Dialog danh sách hóa đơn, truyền hàm callback xử lý khi người dùng chọn xem chi tiết 1 hóa đơn
            HoaDonDialog dlg = new HoaDonDialog(view, "Hóa Đơn", hoaDons, maHoaDon -> {
                try {
                    // Xem bảng kê chi tiết viện phí của hóa đơn được chọn
                    var chiTiet = hoaDonService.getChiTietHoaDon(maHoaDon);
                    if (chiTiet != null) {
                        com.hospital.app.view.ChiTietHoaDonDialog chiTietDlg = new com.hospital.app.view.ChiTietHoaDonDialog(view, maHoaDon, chiTiet);
                        chiTietDlg.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(view, "Không có dữ liệu chi tiết.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi khi tải chi tiết hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
            dlg.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi nạp danh sách hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Nghiệp vụ Xuất viện & Thanh toán hóa đơn:
     * Tính toán toàn bộ chi phí phát sinh, lập hóa đơn và đóng lượt điều trị.
     */
    private void discharge() {
        String id = view.getSelectedEncounterId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lượt điều trị để xuất viện.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LuotDieuTri currentLdt = luotDieuTriService.findByIdWithDetails(id);
            // Kiểm tra trạng thái: Không thể xuất viện nếu BN đã xuất viện từ trước
            if (currentLdt == null || currentLdt.getNgayKetThuc() != null) {
                JOptionPane.showMessageDialog(view, "Lượt điều trị này đã xuất viện hoặc không tồn tại!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Hộp thoại xác nhận cuối cùng
            int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận Xuất viện và lập hóa đơn thanh toán cho bệnh nhân này?", "Xác nhận xuất viện", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi Service thực hiện quy trình xuất viện (Bao gồm tính tiền giường, thuốc, dịch vụ, BHYT)
                com.hospital.app.entity.HoaDon hd = hoaDonService.createDischargeInvoice(currentLdt);
                
                loadData(); // Cập nhật trạng thái mới "Đã xuất viện" lên bảng
                
                // Hiển thị thông báo thành công kèm tóm tắt hóa đơn vừa lập
                JOptionPane.showMessageDialog(view, "Đã thực hiện xuất viện thành công.\nSố hóa đơn: " + hd.getMaHoaDon() + "\nTổng thanh toán: " + hd.getTongTien() + " VNĐ", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // Xử lý lỗi trong quá trình lập hóa đơn hoặc cập nhật DB
            JOptionPane.showMessageDialog(view, "Lỗi khi thực hiện quy trình xuất viện: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở form kê đơn thuốc cho bệnh nhân đang trong lượt điều trị.
     */
    private void prescribe() {
        String id = view.getSelectedEncounterId();
        if (id == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn lượt điều trị để kê đơn.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LuotDieuTri ldt = luotDieuTriService.findByIdWithDetails(id);
            if (ldt == null) return;

            // Chặn kê đơn nếu bệnh nhân đã xuất viện
            if (ldt.getNgayKetThuc() != null) {
                JOptionPane.showMessageDialog(view, "Bệnh nhân đã xuất viện, không thể kê đơn mới!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tải danh sách thuốc để doctor chọn
            List<Thuoc> allThuoc = thuocService.findAll();

            // Lấy dữ liệu đơn thuốc cũ (nếu có) để nạp vào dialog
            Map<String, Object> oldData = donThuocService.findLatestDataByLuotDieuTri(id);
            Map<String, Integer> initialItems = null;
            String initialNote = "";
            if (oldData != null) {
                initialItems = (Map<String, Integer>) oldData.get("items");
                initialNote = (String) oldData.get("ghiChu");
            }

            PrescriptionDialog dlg = new PrescriptionDialog(view, "Kê Đơn Thuốc - " + ldt.getBenhNhan().getTenBenhNhan(), 
                                                            allThuoc, initialItems, initialNote);
            dlg.setVisible(true);

            if (dlg.isOk()) {
                // Thực hiện lưu đơn thuốc qua Service
                donThuocService.prescribe(ldt.getMaLuot(), dlg.getGhiChu(), dlg.getItems());
                JOptionPane.showMessageDialog(view, "Đã cập nhật đơn thuốc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi kê đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
