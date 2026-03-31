package com.hospital.app.view;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog Form dành cho việc thêm Admin mới.
 */
public class AdminDialog extends JDialog {

    private final JTextField txtId = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JTextField txtFullName = new JTextField(20);
    
    private final JButton btnSave = new JButton("Lưu Admin");
    private final JButton btnCancel = new JButton("Hủy");

    public AdminDialog(Frame owner) {
        super(owner, "Thêm Quản Trị Viên mới (ADMIN)", true);
        setLayout(new BorderLayout(10, 10));
        setSize(400, 300);
        setLocationRelativeTo(owner);

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlForm.add(new JLabel("Mã TK:"));
        pnlForm.add(txtId);

        pnlForm.add(new JLabel("Tên đăng nhập:"));
        pnlForm.add(txtUsername);

        pnlForm.add(new JLabel("Mật khẩu:"));
        pnlForm.add(txtPassword);

        pnlForm.add(new JLabel("Họ tên:"));
        pnlForm.add(txtFullName);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);

        add(pnlForm, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        // Đóng dialog khi nhấn nút Hủy
        btnCancel.addActionListener(e -> dispose());
    }

    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JTextField getTxtFullName() { return txtFullName; }
    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
}
