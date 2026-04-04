package com.hospital.app.view;

import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.BenhNhan;
import com.hospital.app.entity.LuotDieuTri;
import com.hospital.app.entity.PhongBenh;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class LuotDieuTriDialog extends JDialog {

    private JComboBox<ComboItem> cbBenhNhan;
    private JComboBox<ComboItem> cbBacSi;
    private JComboBox<ComboItem> cbPhongBenh;
    private JTextField txtNgayBatDau;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private LuotDieuTri luotDieuTri;
    private final List<PhongBenh> danhSachPhong;

    public LuotDieuTriDialog(Frame parent, String title, 
                             List<BenhNhan> benhNhans, 
                             List<BacSi> bacSis, 
                             List<PhongBenh> phongBenhs,
                             Map<String, Long> roomOccupancy,
                             LuotDieuTri luotDieuTri) {
        super(parent, title, true);
        this.danhSachPhong = phongBenhs;
        this.luotDieuTri = luotDieuTri;
        
        initComponents(benhNhans, bacSis, phongBenhs, roomOccupancy);
        fillData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<BenhNhan> benhNhans, List<BacSi> bacSis, List<PhongBenh> phongBenhs, Map<String, Long> occupancy) {
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        cbBenhNhan = new JComboBox<>();
        for (BenhNhan bn : benhNhans) {
            cbBenhNhan.addItem(new ComboItem(bn.getMaBenhNhan(), bn.getMaBenhNhan() + " - " + bn.getTenBenhNhan()));
        }

        cbBacSi = new JComboBox<>();
        for (BacSi bs : bacSis) {
            cbBacSi.addItem(new ComboItem(bs.getMaBacSi(), bs.getMaBacSi() + " - " + bs.getTenBacSi()));
        }

        cbPhongBenh = new JComboBox<>();
        cbPhongBenh.addItem(new ComboItem("", "-- Ngoại trú (Không cần phòng) --"));
        for (PhongBenh pb : phongBenhs) {
            long current = occupancy.getOrDefault(pb.getMaPhong(), 0L);
            String label = pb.getMaPhong() + " - " + pb.getLoaiPhong() + " (" + current + "/" + pb.getSoGiuongToiDa() + ")";
            cbPhongBenh.addItem(new ComboItem(pb.getMaPhong(), label));
        }

        txtNgayBatDau = new JTextField(20);
        txtNgayBatDau.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        pnlInput.add(new JLabel("Bệnh Nhân:"));
        pnlInput.add(cbBenhNhan);
        pnlInput.add(new JLabel("Bác Sĩ Điều Trị:"));
        pnlInput.add(cbBacSi);
        pnlInput.add(new JLabel("Phòng Bệnh:"));
        pnlInput.add(cbPhongBenh);
        pnlInput.add(new JLabel("Ngày Nhập:"));
        pnlInput.add(txtNgayBatDau);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (validateData(occupancy)) {
                saveData();
                isOk = true;
                dispose();
            }
        });

        add(pnlInput, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void fillData() {
        if (luotDieuTri != null) {
            if (luotDieuTri.getBenhNhan() != null) setComboSelected(cbBenhNhan, luotDieuTri.getBenhNhan().getMaBenhNhan());
            if (luotDieuTri.getBacSiDieuTri() != null) setComboSelected(cbBacSi, luotDieuTri.getBacSiDieuTri().getMaBacSi());
            if (luotDieuTri.getPhongBenh() != null) setComboSelected(cbPhongBenh, luotDieuTri.getPhongBenh().getMaPhong());
            if (luotDieuTri.getNgayBatDau() != null) txtNgayBatDau.setText(luotDieuTri.getNgayBatDau().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            cbBenhNhan.setEnabled(false); // Không cho phép đổi bệnh nhân sau khi đã tạo lượt khám
        }
    }

    private void setComboSelected(JComboBox<ComboItem> cb, String id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).getId().equals(id)) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private boolean validateData(Map<String, Long> occupancy) {
        try {
            LocalDate.parse(txtNgayBatDau.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày nhập viện không hợp lệ (định dạng dd/MM/yyyy)!");
            return false;
        }

        ComboItem selectedPb = (ComboItem) cbPhongBenh.getSelectedItem();
        if (selectedPb != null && !selectedPb.getId().isEmpty()) {
            PhongBenh targetRoom = danhSachPhong.stream()
                .filter(p -> p.getMaPhong().equals(selectedPb.getId()))
                .findFirst()
                .orElse(null);

            if (targetRoom != null) {
                long current = occupancy.getOrDefault(targetRoom.getMaPhong(), 0L);
                boolean isSameRoom = (luotDieuTri != null && luotDieuTri.getPhongBenh() != null 
                    && targetRoom.getMaPhong().equals(luotDieuTri.getPhongBenh().getMaPhong()));
                
                if (!isSameRoom && current >= targetRoom.getSoGiuongToiDa()) {
                    JOptionPane.showMessageDialog(this, "Phòng này đã đầy, vui lòng chọn phòng khác!");
                    return false;
                }
            }
        }
        return true;
    }

    private void saveData() {
        if (luotDieuTri == null) {
            luotDieuTri = new LuotDieuTri();
            luotDieuTri.setMaLuot("LDT" + (System.currentTimeMillis() % 10000000L));
            luotDieuTri.setTrangThai("Đang điều trị");
        }
        luotDieuTri.setNgayBatDau(LocalDate.parse(txtNgayBatDau.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public boolean isOk() { return isOk; }
    public LuotDieuTri getLuotDieuTri() { return luotDieuTri; }
    
    public String getSelectedMaBenhNhan() {
        ComboItem it = (ComboItem) cbBenhNhan.getSelectedItem();
        return it != null ? it.getId() : null;
    }
    public String getSelectedMaBacSi() {
        ComboItem it = (ComboItem) cbBacSi.getSelectedItem();
        return it != null ? it.getId() : null;
    }
    public String getSelectedMaPhong() {
        ComboItem it = (ComboItem) cbPhongBenh.getSelectedItem();
        return (it != null && !it.getId().isEmpty()) ? it.getId() : null;
    }

    public static class ComboItem {
        private String id;
        private String label;
        public ComboItem(String id, String label) { this.id = id; this.label = label; }
        public String getId() { return id; }
        @Override public String toString() { return label; }
    }
}
