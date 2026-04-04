package com.hospital.app.view;

import com.hospital.app.dto.ThongKeDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Giao diện Thống kê rút gọn (Simplified Dashboard).
 * Thiết kế mới:
 * - 01 Toolbar duy nhất chọn Ngày/Tháng/Năm.
 * - 02 Card lớn: Doanh thu Ngày, Doanh thu Tháng (tính từ ngày 1).
 * - 03 Bảng dưới: Top 5 BN, Top 5 BS, Danh sách đang điều trị.
 */
public class OverviewPanel extends JPanel {

    // ─── Màu sắc & Font ──────────────────────────────────────────────────────
    private static final Color BG_MAIN      = new Color(0xF8FAFC);
    private static final Color COL_BLUE     = new Color(0x2563EB);
    private static final Color COL_PURPLE   = new Color(0x7C3AED);
    private static final Color TEXT_MAIN    = new Color(0x1A1A2E);
    private static final DecimalFormat DF_NUM = new DecimalFormat("#,###");

    // ─── Thành phần UI lọc ───────────────────────────────────────────────────
    private JComboBox<String> cmbDay;
    private JComboBox<String> cmbMonth;
    private JComboBox<Integer> cmbYear;
    private JButton btnFilter;

    // ─── Card số liệu ────────────────────────────────────────────────────────
    private JLabel lblDayVal, lblMonthVal;

    // ─── Bảng dữ liệu ────────────────────────────────────────────────────────
    private DefaultTableModel modelTopBN, modelTopBS, modelTreatment;

    public OverviewPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        initComponents();
    }

    private void initComponents() {
        // 1. Toolbar lọc ở trên
        add(buildToolbar(), BorderLayout.NORTH);

        // 2. Nội dung chính ở giữa
        JPanel pnlContent = new JPanel(new BorderLayout(20, 20));
        pnlContent.setBackground(BG_MAIN);
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 2a. Section Cards (Trên)
        pnlContent.add(buildCardSection(), BorderLayout.NORTH);

        // 2b. Section Tables (Dưới)
        pnlContent.add(buildTableSection(), BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE2E8F0)));

        JLabel lblTitle = new JLabel("Thống kê bệnh viện");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(lblTitle);

        p.add(new JSeparator(JSeparator.VERTICAL));

        p.add(new JLabel("Ngày xem:"));
        cmbDay   = dayCombo();
        cmbMonth = monthCombo();
        cmbYear  = yearCombo();
        
        // Fix sizing
        cmbDay.setPreferredSize(new Dimension(60, 30));
        cmbMonth.setPreferredSize(new Dimension(60, 30));
        cmbYear.setPreferredSize(new Dimension(80, 30));
        
        p.add(cmbDay); p.add(cmbMonth); p.add(cmbYear);

        btnFilter = new JButton("Xem thống kê");
        btnFilter.setBackground(COL_BLUE);
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.setOpaque(true);
        btnFilter.setBorderPainted(false);
        btnFilter.setPreferredSize(new Dimension(140, 32));
        btnFilter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.add(btnFilter);

        // Set mặc định ngày hôm nay
        LocalDate now = LocalDate.now();
        cmbDay.setSelectedItem(String.format("%02d", now.getDayOfMonth()));
        cmbMonth.setSelectedItem(String.format("%02d", now.getMonthValue()));
        cmbYear.setSelectedItem(now.getYear());

        return p;
    }

    private JPanel buildCardSection() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
        p.setBackground(BG_MAIN);

        JPanel cardDay   = createStatCard("Doanh thu Ngày (chọn)", COL_BLUE);
        lblDayVal = (JLabel) cardDay.getClientProperty("valLabel");
        
        JPanel cardMonth = createStatCard("Doanh thu Tháng (từ ngày 1)", COL_PURPLE);
        lblMonthVal = (JLabel) cardMonth.getClientProperty("valLabel");

        p.add(cardDay);
        p.add(cardMonth);
        return p;
    }

    private JPanel buildTableSection() {
        JPanel p = new JPanel(new GridLayout(1, 3, 20, 0));
        p.setBackground(BG_MAIN);

        // Bảng Top 5 Bệnh nhân chi nhiều nhất
        modelTopBN = new DefaultTableModel(new String[]{"Tên Bệnh Nhân", "Tổng Chi"}, 0);
        p.add(createTablePanel("Top 5 Bệnh Nhân (Tháng)", modelTopBN));

        // Bảng Top 5 Bác sĩ có lượt tiếp nhận
        modelTopBS = new DefaultTableModel(new String[]{"Tên Bác Sĩ", "Lượt Tiếp Nhận"}, 0);
        p.add(createTablePanel("Top 5 Bác Sĩ (Tháng)", modelTopBS));

        // Bảng Bệnh nhân đang điều trị
        modelTreatment = new DefaultTableModel(new String[]{"Bệnh Nhân", "Bác Sĩ"}, 0);
        p.add(createTablePanel("Bệnh Nhân Đang Điều Trị", modelTreatment));

        return p;
    }

    // ─── Xử lý dữ liệu ───────────────────────────────────────────────────────

    public void updateData(ThongKeDTO dto) {
        if (dto == null) return;

        // Cập nhật Cards
        lblDayVal.setText(formatMoney(dto.getRevenueDay()) + " đ");
        lblMonthVal.setText(formatMoney(dto.getRevenueMonth()) + " đ");

        // Cập nhật Tables
        fillTable(modelTopBN, dto.getTopPatientsMonth(), 
                row -> new Object[]{row[0], formatMoney((BigDecimal) row[1]) + " đ"});
                
        fillTable(modelTopBS, dto.getTopDoctorsMonth(), 
                row -> new Object[]{row[0], row[1]});
                
        fillTable(modelTreatment, dto.getPatientsInTreatment(), 
                row -> new Object[]{row[0], row[1]});

        if (!dto.isHasData()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu trong thời gian này!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void fillTable(DefaultTableModel model, List<Object[]> data, Function<Object[], Object[]> converter) {
        model.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) model.addRow(converter.apply(row));
        }
    }

    // ─── Helpers UI ──────────────────────────────────────────────────────────

    private JPanel createStatCard(String title, Color color) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2E8F0), 1),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTtl = new JLabel(title);
        lblTtl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTtl.setForeground(new Color(0x64748B));
        lblTtl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVal = new JLabel("0 đ");
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(color);
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lblTtl);
        p.add(Box.createVerticalStrut(5));
        p.add(lblVal);
        p.putClientProperty("valLabel", lblVal);
        return p;
    }

    private JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_MAIN);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_MAIN);
        p.add(lbl, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xE2E8F0)));
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JComboBox<String> dayCombo() {
        String[] d = new String[31];
        for (int i = 0; i < 31; i++) d[i] = String.format("%02d", i + 1);
        return new JComboBox<>(d);
    }

    private JComboBox<String> monthCombo() {
        String[] m = new String[12];
        for (int i = 0; i < 12; i++) m[i] = String.format("%02d", i + 1);
        return new JComboBox<>(m);
    }

    private JComboBox<Integer> yearCombo() {
        int cur = LocalDate.now().getYear();
        Integer[] y = new Integer[cur - 2018];
        for (int i = 0; i < y.length; i++) y[i] = cur - i;
        return new JComboBox<>(y);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0";
        return DF_NUM.format(amount);
    }

    public LocalDate getSelectedDate() {
        try {
            int d = Integer.parseInt((String) cmbDay.getSelectedItem());
            int m = Integer.parseInt((String) cmbMonth.getSelectedItem());
            int y = (Integer) cmbYear.getSelectedItem();
            return LocalDate.of(y, m, d);
        } catch (Exception e) { return null; }
    }

    public JButton getBtnFilter() { return btnFilter; }
}
