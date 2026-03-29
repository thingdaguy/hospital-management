package com.hospital.app.dao;

import com.hospital.app.entity.HoaDon;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> findByMaBenhNhan(String maBenhNhan) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT h FROM HoaDon h WHERE h.benhNhan.maBenhNhan = :maBn ORDER BY h.ngayLap DESC";
            TypedQuery<HoaDon> q = em.createQuery(jpql, HoaDon.class);
            q.setParameter("maBn", maBenhNhan);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}
