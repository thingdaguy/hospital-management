package com.hospital.app.view;

import com.hospital.app.dto.ChiTietDichVuDTO;
import com.hospital.app.dto.ChiTietHoaDonDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Dialog hiển thị chi tiết hóa đơn khám chữa bệnh.
 * Bao gồm thông tin bệnh nhân, bảo hiểm, chi phí từng khoản và tổng thanh toán.
 */
public class ChiTietHoaDonDialog extends JDialog {

    // ─── Hằng số tạo kiểu ───────────────────────────────────────────────────
    private static final Color BG_MAIN = new Color(245, 247, 252);
    private static final Color BG_SECTION = Color.WHITE;
    private static final Color COL_HEADER = new Color(37, 99, 235); // xanh đậm
    private static final Color COL_POSITIVE = new Color(22, 163, 74); // xanh lá — giảm giá
    private static final Color COL_TOTAL = new Color(220, 38, 38); // đỏ — thành tiền
    private static final Color COL_PAID = new Color(22, 163, 74); // thanh toán
    private static final Color COL_UNPAID = new Color(234, 88, 12); // chưa thanh toán
    private static final Color BORDER_CLR = new Color(209, 213, 219);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_TOTAL = new Font("Segoe UI", Font.BOLD, 16);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DefaultTableModel tableModel;

    /**
     * Khởi tạo dialog chi tiết hóa đơn.
     *
     * @param parent Cửa sổ cha để căn giữa dialog
     * @param dto    DTO chứa đầy đủ thông tin hóa đơn
     */
    public ChiTietHoaDonDialog(JFrame parent, String ignored, ChiTietHoaDonDTO dto) {
        super(parent, "Chi Tiết Hóa Đơn", true);
        setSize(580, 680);
        setMinimumSize(new Dimension(520, 580));
        setLocationRelativeTo(parent);
        setResizable(true);

        // Cột bảng dịch vụ
        String[] cols = { "Tên Dịch Vụ", "Đơn Giá" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_MAIN);

        root.add(buildHeader(dto), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG_MAIN);
        body.setBorder(new EmptyBorder(10, 14, 10, 14));

        body.add(buildPatientSection(dto));
        body.add(Box.createVerticalStrut(10));
        body.add(buildCostSection(dto));
        body.add(Box.createVerticalStrut(10));
        body.add(buildServiceTable(dto));
        body.add(Box.createVerticalStrut(10));
        body.add(buildSummarySection(dto));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ─── Tiêu đề ─────────────────────────────────────────────────────────────

    /**
     * Xây dựng phần tiêu đề có màu nền xanh, hiển thị mã hóa đơn và trạng thái.
     */
    private JPanel buildHeader(ChiTietHoaDonDTO dto) {
        JPanel pnl = new JPanel(new BorderLayout(10, 0));
        pnl.setBackground(COL_HEADER);
        pnl.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel("HÓA ĐƠN THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        // Phần phải: mã hóa đơn + badge trạng thái
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JLabel lblMa = new JLabel(dto.getMaHoaDon() != null ? dto.getMaHoaDon() : "");
        lblMa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMa.setForeground(Color.WHITE);

        // Badge trạng thái
        String tt = dto.getTrangThaiHoaDon() != null ? dto.getTrangThaiHoaDon() : "";
        JLabel lblStatus = new JLabel(" " + tt + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                // Bo góc
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(Color.WHITE);
        boolean paid = "Đã thanh toán".equalsIgnoreCase(tt);
        lblStatus.setBackground(paid ? COL_PAID : COL_UNPAID);
        lblStatus.setOpaque(false);
        lblStatus.setBorder(new EmptyBorder(2, 6, 2, 6));

        right.add(lblMa);
        right.add(lblStatus);

        pnl.add(lblTitle, BorderLayout.WEST);
        pnl.add(right, BorderLayout.EAST);
        return pnl;
    }

    // ─── Thông tin bệnh nhân ─────────────────────────────────────────────────

    /**
     * Xây dựng thẻ thông tin bệnh nhân (mã BN, tên, ngày sinh, số BHYT).
     */
    private JPanel buildPatientSection(ChiTietHoaDonDTO dto) {
        JPanel card = createCard("THÔNG TIN BỆNH NHÂN");

        String ngaySinhStr = dto.getNgaySinh() != null ? dto.getNgaySinh().format(DATE_FMT) : "—";

        // Icon khác biệt khi có/không BHYT
        String bhytDisplay = dto.getSoTheBHYT() != null
                ? ("  " + dto.getSoTheBHYT())
                : "  Không có";

        addInfoRow(card, "Mã bệnh nhân", dto.getMaBenhNhan());
        addInfoRow(card, "Họ và tên", dto.getTenBenhNhan());
        addInfoRow(card, "Ngày sinh", ngaySinhStr);
        addInfoRow(card, "Số thẻ BHYT", bhytDisplay);

        return card;
    }

    // ─── Bảng dịch vụ ────────────────────────────────────────────────────────

    /**
     * Xây dựng bảng danh sách dịch vụ đã sử dụng trong lượt điều trị.
     */
    private JPanel buildServiceTable(ChiTietHoaDonDTO dto) {
        JPanel card = createCard("DỊCH VỤ ĐÃ SỬ DỤNG");

        // Nạp dữ liệu vào model
        if (dto.getDanhSachDichVu() != null) {
            for (ChiTietDichVuDTO d : dto.getDanhSachDichVu()) {
                String amt = d.getDonGia() != null
                        ? String.format("%,.0f đ", d.getDonGia())
                        : "0 đ";
                tableModel.addRow(new Object[] { d.getTenDichVu(), amt });
            }
        }

        JTable table = new JTable(tableModel);
        table.setFont(FONT_BODY);
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(BG_SECTION);
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(new Color(239, 246, 255));
        table.getTableHeader().setForeground(COL_HEADER);

        // Căn phải cột tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setMaxWidth(160);

        // Màu xen kẽ dòng
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBackground(sel ? new Color(219, 234, 254)
                        : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
                setFont(FONT_BODY);
                if (col == 1)
                    setHorizontalAlignment(JLabel.RIGHT);
                else
                    setHorizontalAlignment(JLabel.LEFT);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        sp.setPreferredSize(new Dimension(0, 110));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sp);
        return card;
    }

    // ─── Chi phí từng khoản ───────────────────────────────────────────────────

    /**
     * Xây dựng bảng chi phí tập hợp: tiền thuốc, tiền dịch vụ, tiền phòng.
     */
    private JPanel buildCostSection(ChiTietHoaDonDTO dto) {
        JPanel card = createCard("CHI PHÍ KHÁM CHỮA BỆNH");

        addCostRow(card, "Tiền thuốc",
                formatVND(dto.getTongTienThuoc()), false, false);
        addCostRow(card, "Tiền dịch vụ",
                formatVND(dto.getTongTienDichVu()), false, false);

        // Tiền phòng — hiển thị số ngày
        String phongLabel = dto.getSoNgayPhong() > 0
                ? "Tiền phòng (" + dto.getSoNgayPhong() + " ngày)"
                : "Tiền phòng";
        addCostRow(card, phongLabel,
                formatVND(dto.getTongTienPhong()), false, false);

        // Đường kẻ ngăn cách
        card.add(buildDivider());

        // Tổng gốc trước giảm giá
        java.math.BigDecimal rawTotal = dto.getTongTienThuoc()
                .add(dto.getTongTienDichVu())
                .add(dto.getTongTienPhong());
        addCostRow(card, "Tổng cộng", formatVND(rawTotal), true, false);

        return card;
    }

    // ─── Tổng kết thanh toán ─────────────────────────────────────────────────

    /**
     * Xây dựng phần tổng kết: BHYT chi trả, thành tiền cuối và trạng thái hóa đơn.
     */
    private JPanel buildSummarySection(ChiTietHoaDonDTO dto) {
        JPanel card = createCard("THANH TOÁN");

        // BHYT chi trả (màu xanh lá, âm)
        JPanel rowBhyt = new JPanel(new BorderLayout());
        rowBhyt.setBackground(BG_SECTION);
        rowBhyt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        rowBhyt.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowBhyt.setBorder(new EmptyBorder(2, 0, 2, 0));
        JLabel lbh = new JLabel("BHYT chi trả");
        lbh.setFont(FONT_BODY);
        JLabel lbhVal = new JLabel("- " + formatVND(dto.getGiamGiaBHYT()));
        lbhVal.setFont(FONT_LABEL);
        lbhVal.setForeground(COL_POSITIVE);
        rowBhyt.add(lbh, BorderLayout.WEST);
        rowBhyt.add(lbhVal, BorderLayout.EAST);
        card.add(rowBhyt);
        card.add(buildDivider());

        // Thành tiền (nổi bật)
        JPanel rowTotal = new JPanel(new BorderLayout());
        rowTotal.setBackground(new Color(239, 246, 255));
        rowTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowTotal.setBorder(new EmptyBorder(6, 8, 6, 8));
        JLabel ltl = new JLabel("THÀNH TIỀN");
        ltl.setFont(FONT_TOTAL);
        ltl.setForeground(new Color(30, 58, 138));
        JLabel ltlVal = new JLabel(formatVND(dto.getTongTienHoaDon()));
        ltlVal.setFont(FONT_TOTAL);
        ltlVal.setForeground(COL_TOTAL);
        rowTotal.add(ltl, BorderLayout.WEST);
        rowTotal.add(ltlVal, BorderLayout.EAST);
        card.add(rowTotal);

        return card;
    }

    // ─── Footer ───────────────────────────────────────────────────────────────

    /**
     * Panel nút đóng ở phía dưới dialog.
     */
    private JPanel buildFooter() {
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(FONT_LABEL);
        btnClose.setBackground(COL_HEADER);
        btnClose.setForeground(Color.WHITE);
        // Bắt buộc trên Windows để màu nền nút hiển thị đúng
        btnClose.setOpaque(true);
        btnClose.setContentAreaFilled(true);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(new EmptyBorder(7, 24, 7, 24));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        // Hiệu ứng hover
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnClose.setBackground(COL_HEADER.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnClose.setBackground(COL_HEADER);
            }
        });

        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        pnl.add(btnClose);
        return pnl;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Tạo một panel dạng "card" với tiêu đề section.
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_SECTION);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(10, 12, 10, 12)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COL_HEADER);
        lblTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTitle);
        return card;
    }

    /**
     * Thêm một hàng label–value vào panel thông tin bệnh nhân.
     */
    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(BG_SECTION);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel lbl = new JLabel(label + " :");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(new Color(107, 114, 128));
        lbl.setPreferredSize(new Dimension(140, 22));

        JLabel val = new JLabel(value != null ? value : "—");
        val.setFont(FONT_LABEL);
        val.setForeground(new Color(17, 24, 39));

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        panel.add(row);
    }

    /**
     * Thêm một hàng chi phí với tuỳ chọn in đậm vào panel.
     *
     * @param bold true để in đậm cả hai nhãn
     * @param red  true để tô đỏ phần giá trị
     */
    private void addCostRow(JPanel panel, String label, String value, boolean bold, boolean red) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG_SECTION);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(bold ? FONT_LABEL : FONT_BODY);

        JLabel val = new JLabel(value);
        val.setFont(bold ? FONT_LABEL : FONT_BODY);
        if (red)
            val.setForeground(COL_TOTAL);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        panel.add(row);
    }

    /**
     * Tạo đường kẻ ngang phân cách mảnh.
     */
    private JPanel buildDivider() {
        JPanel div = new JPanel();
        div.setBackground(BORDER_CLR);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setPreferredSize(new Dimension(0, 1));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);
        div.setBorder(new EmptyBorder(4, 0, 4, 0));
        return div;
    }

    /**
     * Định dạng số tiền về dạng "X.XXX.XXX đ".
     */
    private String formatVND(java.math.BigDecimal amount) {
        if (amount == null)
            return "0 đ";
        return String.format("%,.0f đ", amount);
    }
}
