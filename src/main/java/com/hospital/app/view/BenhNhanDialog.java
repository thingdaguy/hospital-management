package com.hospital.app.view;

import com.hospital.app.entity.BacSi;
import com.hospital.app.entity.BenhNhan;
import com.hospital.app.entity.PhongBenh;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BenhNhanDialog extends JDialog {

    private JTextField txtMaBenhNhan;
    private JTextField txtTenBenhNhan;
    private JTextField txtNgaySinh;
    private JTextField txtSoDienThoai;
    private JComboBox<ComboItem> cbBacSi;
    private JComboBox<ComboItem> cbPhongBenh;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final BenhNhan benhNhan;

    public BenhNhanDialog(Frame parent, String title, List<BacSi> bacSis, List<PhongBenh> phongBenhs,
            BenhNhan benhNhan) {
        super(parent, title, true);
        this.benhNhan = benhNhan != null ? benhNhan : new BenhNhan();
        initComponents(bacSis, phongBenhs);
        fillData(benhNhan != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<BacSi> bacSis, List<PhongBenh> phongBenhs) {
        JPanel pnlInput = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMaBenhNhan = new JTextField(20);
        txtTenBenhNhan = new JTextField(20);
        txtNgaySinh = new JTextField(20);
        txtSoDienThoai = new JTextField(20);

        cbBacSi = new JComboBox<>();
        for (BacSi bs : bacSis) {
            cbBacSi.addItem(new ComboItem(bs.getMaBacSi(), bs.getMaBacSi() + " - " + bs.getTenBacSi()));
        }

        cbPhongBenh = new JComboBox<>();
        for (PhongBenh pb : phongBenhs) {
            cbPhongBenh.addItem(new ComboItem(pb.getMaPhong(), pb.getMaPhong() + " - " + pb.getLoaiPhong()));
        }

        pnlInput.add(new JLabel("Mã BN:"));
        pnlInput.add(txtMaBenhNhan);
        pnlInput.add(new JLabel("Họ Tên:"));
        pnlInput.add(txtTenBenhNhan);
        pnlInput.add(new JLabel("Ngày sinh (dd/MM/yyyy):"));
        pnlInput.add(txtNgaySinh);
        pnlInput.add(new JLabel("Điện thoại:"));
        pnlInput.add(txtSoDienThoai);
        pnlInput.add(new JLabel("Bác Sĩ Tiếp Nhận:"));
        pnlInput.add(cbBacSi);
        pnlInput.add(new JLabel("Phòng Bệnh:"));
        pnlInput.add(cbPhongBenh);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (validateData()) {
                saveData();
                isOk = true;
                dispose();
            }
        });

        add(pnlInput, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void fillData(boolean isEditMode) {
        if (isEditMode) {
            txtMaBenhNhan.setText(benhNhan.getMaBenhNhan());
            txtMaBenhNhan.setEditable(false);
            txtTenBenhNhan.setText(benhNhan.getTenBenhNhan());
            if (benhNhan.getNgaySinh() != null) {
                txtNgaySinh.setText(benhNhan.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            txtSoDienThoai.setText(benhNhan.getSoDienThoai());

            if (benhNhan.getBacSiTiepNhan() != null) {
                setComboSelected(cbBacSi, benhNhan.getBacSiTiepNhan().getMaBacSi());
            }
            if (benhNhan.getPhongBenh() != null) {
                setComboSelected(cbPhongBenh, benhNhan.getPhongBenh().getMaPhong());
            }
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

    private boolean validateData() {
        if (txtMaBenhNhan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã bệnh nhân không được để trống!");
            return false;
        }
        if (txtTenBenhNhan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bệnh nhân không được để trống!");
            return false;
        }
        try {
            LocalDate.parse(txtNgaySinh.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (định dạng dd/MM/yyyy)!");
            return false;
        }
        return true;
    }

    private void saveData() {
        benhNhan.setMaBenhNhan(txtMaBenhNhan.getText().trim());
        benhNhan.setTenBenhNhan(txtTenBenhNhan.getText().trim());
        benhNhan.setNgaySinh(LocalDate.parse(txtNgaySinh.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        benhNhan.setSoDienThoai(txtSoDienThoai.getText().trim());

        ComboItem selectedBs = (ComboItem) cbBacSi.getSelectedItem();
        if (selectedBs != null) {
            BacSi bs = new BacSi();
            bs.setMaBacSi(selectedBs.getId());
            benhNhan.setBacSiTiepNhan(bs);
        }

        ComboItem selectedPb = (ComboItem) cbPhongBenh.getSelectedItem();
        if (selectedPb != null) {
            PhongBenh pb = new PhongBenh();
            pb.setMaPhong(selectedPb.getId());
            benhNhan.setPhongBenh(pb);
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public static class ComboItem {
        private String id;
        private String label;

        public ComboItem(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
