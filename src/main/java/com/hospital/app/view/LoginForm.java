package com.hospital.app.view;

import com.hospital.app.controller.MainFormController;
import com.hospital.app.entity.TaiKhoan;
import com.hospital.app.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * LoginForm — giao dien dang nhap (icon '+' ben trai, form ben phai).
 */
public class LoginForm extends JFrame {

    private final JTextField txtUsername = new JTextField(22);
    private final JPasswordField txtPassword = new JPasswordField(22);
    private final JLabel lblError = new JLabel(" ");

    private final AuthService authService = new AuthService();

    public LoginForm() {
        super("Đăng nhập - Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 560);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Chia 2 vung: trai (icon) / phai (login form)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(0.35);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(new Color(245, 250, 255));
        left.add(new PlusIconPanel(), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        right.add(Box.createVerticalStrut(30));
        JLabel header1 = new JLabel("Chào mừng đến Bệnh viện");
        header1.setFont(new Font("SansSerif", Font.BOLD, 24));
        header1.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(header1);

        JLabel header2 = new JLabel("Đăng nhập để tiếp tục");
        header2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        header2.setForeground(new Color(60, 60, 60));
        header2.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(header2);

        right.add(Box.createVerticalStrut(35));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Tên đăng nhập:"), c);

        c.gridx = 1;
        c.gridy = 0;
        form.add(txtUsername, c);

        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Mật khẩu:"), c);

        c.gridx = 1;
        c.gridy = 1;
        txtPassword.setEchoChar('*');
        form.add(txtPassword, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        lblError.setForeground(new Color(200, 0, 0));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setText(" ");
        form.add(lblError, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;

        JButton btnLogin = new JButton("Đăng nhập (ADMIN)");
        btnLogin.setPreferredSize(new Dimension(220, 38));
        form.add(btnLogin, c);

        right.add(form);
        right.add(Box.createVerticalGlue());

        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);
        add(splitPane, BorderLayout.CENTER);

        // Cho phép nhấn Enter để đăng nhập
        getRootPane().setDefaultButton(btnLogin);
        txtPassword.addActionListener(e -> btnLogin.doClick());

        btnLogin.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        lblError.setText(" ");
        String username = txtUsername.getText();
        String rawPassword = new String(txtPassword.getPassword());

        try {
            TaiKhoan tk = authService.loginAdmin(username, rawPassword);
            if (tk != null) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    MainForm frame = new MainForm();
                    new MainFormController(frame);
                    frame.setVisible(true);
                });
            } else {
                lblError.setText("Đăng nhập thất bại. Kiểm tra tài khoản/mật khẩu và vai trò ADMIN.");
            }
        } catch (Exception ex) {
            lblError.setText("Lỗi: " + ex.getMessage());
        }
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

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 60;
            int cx = w / 2;
            int cy = h / 2 - 20;

            // Vòng tròn nền
            int r = size / 2;
            g2.setColor(new Color(0, 156, 120));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);

            // Khung tròn trắng
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(6));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            // Dấu '+' (nét dày)
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int plusLen = (int) (r * 0.45);
            g2.drawLine(cx - plusLen, cy, cx + plusLen, cy); // ngang
            g2.drawLine(cx, cy - plusLen, cx, cy + plusLen); // dọc

            // Chữ dưới icon
            g2.setColor(new Color(20, 70, 120));
            String text = "BỆNH VIỆN";
            Font font = new Font("SansSerif", Font.BOLD, 22);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics(font);
            int tw = fm.stringWidth(text);
            g2.drawString(text, cx - tw / 2, cy + r + 35);
        }
    }
}

