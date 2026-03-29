package com.hospital.app.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Quản lý {@link EntityManagerFactory} (JPA SE) — dùng chung cho tầng DAO.
 */
public final class JpaUtil {

    private static final String PU = "hospitalPU";
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PU);

    private JpaUtil() {
    }

    /** Tạo {@link EntityManager} mới cho một unit of work (gọi close() khi xong). */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /** Đóng factory khi thoát ứng dụng (gọi một lần từ MainApp). */
    public static void shutdown() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}
