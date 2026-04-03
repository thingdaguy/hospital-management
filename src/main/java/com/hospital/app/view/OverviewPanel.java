package com.hospital.app.view;

import com.hospital.app.dto.ThongKeDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * OverviewPanel — Hiển thị thống kê tổng quan của bệnh viện.
 */
public class OverviewPanel extends JPanel {

    private final JLabel lblTotalPatientsValue = new JLabel("0");
    private final JLabel lblTotalDoctorsValue = new JLabel("0");
    
    private final DefaultTableModel tableModelTopPatients;
    private final DefaultTableModel tableModelTopDoctors;
    
    private final JButton btnRefresh = new JButton("Tải lại thống kê");
    private final JButton btnAddAdmin = new JButton("Thêm ADMIN mới");

    public OverviewPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Tầng trên: Các thẻ tóm tắt (Cards)
        JPanel pnlCards = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCards.add(createSummaryCard("Tổng số Bệnh Nhân", lblTotalPatientsValue, new Color(52, 152, 219)));
        pnlCards.add(createSummaryCard("Tổng số Bác Sĩ", lblTotalDoctorsValue, new Color(46, 204, 113)));
        
        // 2. Tầng giữa: 2 bảng xếp hạng
        JPanel pnlTables = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Bảng Top bệnh nhân chi trả nhiều nhất
        String[] colsPatients = {"Tên Bệnh Nhân", "Tổng Tiền Hóa Đơn (VNĐ)"};
        tableModelTopPatients = new DefaultTableModel(colsPatients, 0);
        pnlTables.add(createTablePanel("Bệnh nhân chi trả nhiều nhất", tableModelTopPatients));
        
        // Bảng Top bác sĩ nhiều bệnh nhân nhất
        String[] colsDoctors = {"Tên Bác Sĩ", "Số Lượng Bệnh Nhân"};
        tableModelTopDoctors = new DefaultTableModel(colsDoctors, 0);
        pnlTables.add(createTablePanel("Bác sĩ có nhiều bệnh nhân nhất", tableModelTopDoctors));

        // 3. Tầng dưới: Các nút điều khiển
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnAddAdmin);

        add(pnlCards, BorderLayout.NORTH);
        add(pnlTables, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)

        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTitle, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setEnabled(false); // Chỉ xem
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }

    public void updateData(ThongKeDTO dto) {
        if (dto == null) return;
        
        lblTotalPatientsValue.setText(String.valueOf(dto.getTotalPatients()));
        lblTotalDoctorsValue.setText(String.valueOf(dto.getTotalDoctors()));
        
        // Cập nhật bảng bệnh nhân
        tableModelTopPatients.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,###");
        if (dto.getTopExpensivePatients() != null) {
            for (Object[] row : dto.getTopExpensivePatients()) {
                tableModelTopPatients.addRow(new Object[] { row[0], df.format(row[1]) });
            }
        }
        
        // Cập nhật bảng bác sĩ
        tableModelTopDoctors.setRowCount(0);
        if (dto.getTopDoctorsByPatients() != null) {
            for (Object[] row : dto.getTopDoctorsByPatients()) {
                tableModelTopDoctors.addRow(new Object[] { row[0], row[1] });
            }
        }
    }

    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnAddAdmin() { return btnAddAdmin; }
}
