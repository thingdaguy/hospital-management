package com.hospital.app.view;

import com.hospital.app.entity.BaoHiem;
import com.hospital.app.entity.BenhNhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Dialog thêm mới hoặc sửa thông tin bệnh nhân.
 * Bao gồm phần thông tin cơ bản và phần thông tin bảo hiểm y tế (tuỳ chọn).
 */
public class BenhNhanDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Controls thông tin cơ bản ─────────────────────────────────────────
    private JTextField txtMaBenhNhan;
    private JTextField txtTenBenhNhan;
    private JTextField txtNgaySinh;
    private JTextField txtSoDienThoai;

    // ─── Controls BHYT ─────────────────────────────────────────────────────
    private JCheckBox chkCoBHYT;
    private JTextField txtSoThe;
    private JTextField txtMucHuong;
    private JTextField txtNgayHetHan;
    private JPanel pnlBhyt;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean isOk = false;
    private final BenhNhan benhNhan;

    /**
     * Khởi tạo dialog.
     *
     * @param parent    Cửa sổ cha
     * @param title     Tiêu đề dialog
     * @param benhNhan  Đối tượng bệnh nhân (null = thêm mới)
     */
    public BenhNhanDialog(Frame parent, String title, BenhNhan benhNhan) {
        super(parent, title, true);
        this.benhNhan = benhNhan != null ? benhNhan : new BenhNhan();
        initComponents();
        fillData(benhNhan != null);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ─── Khởi tạo giao diện ─────────────────────────────────────────────────

    /**
     * Xây dựng toàn bộ UI của dialog: phần thông tin cơ bản và section BHYT.
     */
    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBorder(new EmptyBorder(14, 16, 10, 16));

        // ── Panel thông tin cơ bản ──
        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xC8D8EE)),
                "Thông tin bệnh nhân",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(0x1A73C8)
        ));
        pnlInfo.setBackground(Color.WHITE);

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        txtMaBenhNhan  = createField();
        txtTenBenhNhan = createField();
        txtNgaySinh    = createField();
        txtSoDienThoai = createField();

        addRow(pnlInfo, "Mã BN *:",               txtMaBenhNhan,  0, lc, fc);
        addRow(pnlInfo, "Họ Tên *:",              txtTenBenhNhan, 1, lc, fc);
        addRow(pnlInfo, "Ngày sinh (dd/MM/yyyy) *:", txtNgaySinh, 2, lc, fc);
        addRow(pnlInfo, "Điện thoại:",             txtSoDienThoai, 3, lc, fc);

        // ── Panel BHYT ──
        JPanel pnlBhytWrapper = new JPanel(new BorderLayout());
        pnlBhytWrapper.setBackground(Color.WHITE);
        pnlBhytWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xC8D8EE)),
                "Bảo hiểm y tế",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(0x1A73C8)
        ));

        // Checkbox bật/tắt nhập BHYT
        chkCoBHYT = new JCheckBox("Bệnh nhân có thẻ BHYT");
        chkCoBHYT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkCoBHYT.setOpaque(false);
        chkCoBHYT.setBorder(new EmptyBorder(4, 4, 4, 0));

        pnlBhyt = new JPanel(new GridBagLayout());
        pnlBhyt.setOpaque(false);
        pnlBhyt.setVisible(false);   // ẩn mặc định cho đến khi tick checkbox

        txtSoThe      = createField();
        txtMucHuong   = createField();
        txtMucHuong.setToolTipText("Nhập số từ 0–100, ví dụ: 80");
        txtNgayHetHan = createField();
        txtNgayHetHan.setToolTipText("Định dạng dd/MM/yyyy");

        addRow(pnlBhyt, "Số thẻ BHYT *:",           txtSoThe,      0, lc, fc);
        addRow(pnlBhyt, "Mức hưởng (%) *:",          txtMucHuong,   1, lc, fc);
        addRow(pnlBhyt, "Ngày hết hạn (dd/MM/yyyy) *:", txtNgayHetHan, 2, lc, fc);

        // Toggle hiển thị khi tick/untick
        chkCoBHYT.addActionListener(e -> {
            pnlBhyt.setVisible(chkCoBHYT.isSelected());
            pack(); // điều chỉnh lại kích thước dialog
        });

        pnlBhytWrapper.add(chkCoBHYT, BorderLayout.NORTH);
        pnlBhytWrapper.add(pnlBhyt,   BorderLayout.CENTER);

        // ── Buttons ──
        btnSave   = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        styleButton(btnSave,   new Color(0x1A73C8));
        styleButton(btnCancel, new Color(0x6C757D));

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnlButtons.setOpaque(false);
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

        root.add(pnlInfo,        BorderLayout.NORTH);
        root.add(pnlBhytWrapper, BorderLayout.CENTER);
        root.add(pnlButtons,     BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ─── Điền dữ liệu (chế độ Sửa) ──────────────────────────────────────────

    /**
     * Điền dữ liệu vào các field khi mở dialog ở chế độ chỉnh sửa.
     */
    private void fillData(boolean isEditMode) {
        if (!isEditMode) return;

        txtMaBenhNhan.setText(benhNhan.getMaBenhNhan());
        txtMaBenhNhan.setEditable(false);   // không cho đổi mã
        txtTenBenhNhan.setText(benhNhan.getTenBenhNhan());
        if (benhNhan.getNgaySinh() != null) {
            txtNgaySinh.setText(benhNhan.getNgaySinh().format(DATE_FMT));
        }
        txtSoDienThoai.setText(benhNhan.getSoDienThoai());

        // Điền thông tin BHYT nếu có
        if (benhNhan.getBaoHiem() != null) {
            chkCoBHYT.setSelected(true);
            pnlBhyt.setVisible(true);
            txtSoThe.setText(benhNhan.getBaoHiem().getMaThe());
            txtMucHuong.setText(benhNhan.getBaoHiem().getMucHuong().toPlainString());
            if (benhNhan.getBaoHiem().getNgayHetHan() != null) {
                txtNgayHetHan.setText(benhNhan.getBaoHiem().getNgayHetHan().format(DATE_FMT));
            }
        }
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    /**
     * Kiểm tra hợp lệ dữ liệu nhập. Nếu tick BHYT thì validate thêm các trường BHYT.
     */
    private boolean validateData() {
        if (txtMaBenhNhan.getText().trim().isEmpty()) {
            showError("Mã bệnh nhân không được để trống!");
            return false;
        }
        if (txtTenBenhNhan.getText().trim().isEmpty()) {
            showError("Tên bệnh nhân không được để trống!");
            return false;
        }
        if (!parseDate(txtNgaySinh.getText().trim())) {
            showError("Ngày sinh không hợp lệ (định dạng dd/MM/yyyy)!");
            return false;
        }

        // Validation riêng cho BHYT nếu bệnh nhân có thẻ
        if (chkCoBHYT.isSelected()) {
            if (txtSoThe.getText().trim().isEmpty()) {
                showError("Số thẻ BHYT không được để trống!");
                return false;
            }
            try {
                BigDecimal mucHuong = new BigDecimal(txtMucHuong.getText().trim());
                if (mucHuong.compareTo(BigDecimal.ZERO) < 0 || mucHuong.compareTo(BigDecimal.valueOf(100)) > 0) {
                    showError("Mức hưởng phải từ 0 đến 100!");
                    return false;
                }
            } catch (NumberFormatException ex) {
                showError("Mức hưởng phải là số (ví dụ: 80)!");
                return false;
            }
            if (!parseDate(txtNgayHetHan.getText().trim())) {
                showError("Ngày hết hạn BHYT không hợp lệ (định dạng dd/MM/yyyy)!");
                return false;
            }
        }
        return true;
    }

    // ─── Ghi dữ liệu ─────────────────────────────────────────────────────────

    /**
     * Ghi dữ liệu từ các field form vào entity {@link BenhNhan}.
     */
    private void saveData() {
        benhNhan.setMaBenhNhan(txtMaBenhNhan.getText().trim());
        benhNhan.setTenBenhNhan(txtTenBenhNhan.getText().trim());
        benhNhan.setNgaySinh(LocalDate.parse(txtNgaySinh.getText().trim(), DATE_FMT));
        String phone = txtSoDienThoai.getText().trim();
        benhNhan.setSoDienThoai(phone.isEmpty() ? null : phone);
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    /** @return true nếu người dùng bấm Lưu thành công */
    public boolean isOk() { return isOk; }

    /** @return Entity BenhNhan đã được điền dữ liệu */
    public BenhNhan getBenhNhan() { return benhNhan; }

    /**
     * Trả về đối tượng {@link BaoHiem} được tạo từ form nếu người dùng tick có thẻ BHYT,
     * ngược lại trả về null.
     *
     * @return BaoHiem mới hoặc null nếu bệnh nhân không có BHYT
     */
    public BaoHiem getBaoHiem() {
        if (!chkCoBHYT.isSelected()) return null;
        BaoHiem bh = new BaoHiem();
        bh.setMaThe(txtSoThe.getText().trim());
        bh.setMucHuong(new BigDecimal(txtMucHuong.getText().trim()));
        bh.setNgayHetHan(LocalDate.parse(txtNgayHetHan.getText().trim(), DATE_FMT));
        bh.setBenhNhan(benhNhan);
        return bh;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Thêm một cặp nhãn + field vào panel GridBag. */
    private void addRow(JPanel panel, String label, JTextField field,
                        int row, GridBagConstraints lc, GridBagConstraints fc) {
        lc.gridy = row;
        fc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl, lc);
        panel.add(field, fc);
    }

    /** Tạo JTextField với font chuẩn. */
    private JTextField createField() {
        JTextField tf = new JTextField(22);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return tf;
    }

    /** Constraint cho cột nhãn. */
    private GridBagConstraints labelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 0; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 6, 5, 10);
        return c;
    }

    /** Constraint cho cột field. */
    private GridBagConstraints fieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 1; c.fill  = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets  = new Insets(5, 0, 5, 6);
        return c;
    }

    /** Tô màu nút bấm. */
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 18, 6, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Phân tích ngày theo định dạng dd/MM/yyyy. */
    private boolean parseDate(String s) {
        try {
            LocalDate.parse(s, DATE_FMT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Hiện hộp thoại báo lỗi. */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
    }
}
