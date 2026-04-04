package com.hospital.app.dao;

import com.hospital.app.entity.ChiTietDonThuoc;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;

/**
 * DAO Chi tiết đơn thuốc.
 */
public class ChiTietDonThuocDAO {

    public void save(ChiTietDonThuoc chiTiet) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(chiTiet);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
