package com.hospital.app.view;

import com.hospital.app.controller.MainFormController;
import com.hospital.app.entity.TaiKhoan;
import com.hospital.app.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * LoginForm — giao diện đăng nhập, icon bên trái, form bên phải.
 */
public class LoginForm extends JFrame {

    private final JTextField    txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JLabel         lblError    = new JLabel(" ");

    private final AuthService authService = new AuthService();

    public LoginForm() {
        super("Hệ thống Quản lý Khám Chữa Bệnh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── Trái: panel icon ────────────────────────────────────────────────
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(new Color(245, 250, 255));
        left.add(new PlusIconPanel(), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(320, 0));

        // ── Phải: panel form ────────────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        // Panel con dọc chứa tiêu đề + form — đặt vào giữa bằng GridBagLayout
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Tiêu đề
        JLabel header1 = new JLabel("Hệ thống Quản lý Khám Chữa Bệnh", JLabel.CENTER);
        header1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header1.setForeground(new Color(0x1A73C8));
        header1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel header2 = new JLabel("Đăng nhập để tiếp tục", JLabel.CENTER);
        header2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        header2.setForeground(new Color(100, 100, 100));
        header2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form inputs
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);

        // Label tên đăng nhập
        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.EAST; gc.fill = GridBagConstraints.NONE; gc.weightx = 0;
        form.add(makeLabel("Tên đăng nhập:"), gc);

        // Field tên đăng nhập
        gc.gridx = 1; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        styleField(txtUsername);
        form.add(txtUsername, gc);

        // Label mật khẩu
        gc.gridx = 0; gc.gridy = 1; gc.anchor = GridBagConstraints.EAST; gc.fill = GridBagConstraints.NONE; gc.weightx = 0;
        form.add(makeLabel("Mật khẩu:"), gc);

        // Field mật khẩu
        gc.gridx = 1; gc.gridy = 1; gc.anchor = GridBagConstraints.WEST; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        txtPassword.setEchoChar('●');
        styleField(txtPassword);
        form.add(txtPassword, gc);

        // Error label
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        lblError.setForeground(new Color(200, 0, 0));
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        form.add(lblError, gc);

        // Nút đăng nhập
        JButton btnLogin = new JButton("  Đăng nhập  ");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(0x1A73C8));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setContentAreaFilled(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(200, 36));
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btnLogin.setBackground(new Color(0x1558A0)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btnLogin.setBackground(new Color(0x1A73C8)); }
        });

        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER; gc.fill = GridBagConstraints.NONE;
        form.add(btnLogin, gc);

        // Ghép inner panel
        inner.add(header1);
        inner.add(Box.createVerticalStrut(4));
        inner.add(header2);
        inner.add(Box.createVerticalStrut(28));
        inner.add(form);

        // Đặt inner vào giữa right bằng GridBagLayout mặc định (CENTER)
        right.add(inner);

        // Enter = đăng nhập
        getRootPane().setDefaultButton(btnLogin);
        txtPassword.addActionListener(e -> btnLogin.doClick());
        btnLogin.addActionListener(e -> doLogin());

        // ── Chia 2 vùng ─────────────────────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerSize(0);
        split.setEnabled(false);
        add(split, BorderLayout.CENTER);
    }

    private void doLogin() {
        lblError.setText(" ");
        String username  = txtUsername.getText();
        String rawPwd    = new String(txtPassword.getPassword());
        try {
            TaiKhoan tk = authService.loginAdmin(username, rawPwd);
            if (tk != null) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        dispose();
                        MainForm frame = new MainForm();
                        new MainFormController(frame);
                        frame.setVisible(true);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Lỗi khi nạp giao diện: " + t.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                });
            } else {
                lblError.setText("Đăng nhập thất bại. Kiểm tra tài khoản / mật khẩu.");
            }
        } catch (Exception ex) {
            lblError.setText("Lỗi: " + ex.getMessage());
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private void styleField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8D8EE), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    /**
     * Panel vẽ icon '+' theo phong cách đơn giản.
     */
    private static class PlusIconPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - 60;
            int cx = w / 2, cy = h / 2 - 20;
            int r = size / 2;

            // Vòng tròn nền
            g2.setColor(new Color(0, 156, 120));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);

            // Khung tròn trắng
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(6));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            // Dấu '+'
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int plusLen = (int) (r * 0.45);
            g2.drawLine(cx - plusLen, cy, cx + plusLen, cy);
            g2.drawLine(cx, cy - plusLen, cx, cy + plusLen);

            // Tên hệ thống dưới icon
            g2.setColor(new Color(20, 70, 120));
            String text = "HealthSphere";
            Font font = new Font("Segoe UI", Font.BOLD, 22);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics(font);
            g2.drawString(text, cx - fm.stringWidth(text) / 2, cy + r + 35);
        }
    }
}
