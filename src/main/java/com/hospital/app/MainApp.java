package com.hospital.app;

import com.hospital.app.controller.MainFormController;
import com.hospital.app.util.JpaUtil;
import com.hospital.app.view.MainForm;
import com.hospital.app.util.EnvLoader;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
/**
 * Điểm vào ứng dụng Swing — khởi tạo MainForm và Controller (MVC).
 */
public final class MainApp {

    private MainApp() {
    }

    public static void main(String[] args) {
        EnvLoader.load();
        System.out.println("DB_URL=" + System.getProperty("DB_URL"));
        System.out.println("DB_PASSWORD=" + System.getProperty("DB_PASSWORD"));
        System.out.println("DB_USER=" + System.getProperty("DB_USER"));
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // giữ LAF mặc định nếu không set được
            }
            MainForm frame = new MainForm();
            new MainFormController(frame);
            Runtime.getRuntime().addShutdownHook(new Thread(JpaUtil::shutdown));
            frame.setVisible(true);
        });
    }
}
