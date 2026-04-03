package com.hospital.app.dao;

import com.hospital.app.entity.HoaDon;
import com.hospital.app.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import com.hospital.app.entity.LuotDieuTri;


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

    public HoaDon findById(String maHoaDon) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(HoaDon.class, maHoaDon);
        } finally {
            em.close();
        }
    }

    public List<LuotDieuTri> findLuotDieuTriForInvoice(String maBenhNhan, LocalDate ngayLap) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT l FROM LuotDieuTri l WHERE l.benhNhan.maBenhNhan = :maBn AND l.ngayDieuTri = :ngayLap";
            TypedQuery<LuotDieuTri> q = em.createQuery(jpql, LuotDieuTri.class);
            q.setParameter("maBn", maBenhNhan);
            q.setParameter("ngayLap", ngayLap);
            List<LuotDieuTri> list = q.getResultList();
            
            for (LuotDieuTri l : list) {
                l.getChiTietDichVus().size();
                for (var cd : l.getChiTietDichVus()) cd.getDichVu().getTenDichVu();
                
                l.getDonThuocs().size();
                for (var dt : l.getDonThuocs()) {
                    dt.getChiTietDonThuocs().size();
                    for (var ctdt : dt.getChiTietDonThuocs()) ctdt.getThuoc().getTenThuoc();
                }
            }
            return list;
        } finally {
            em.close();
        }
    }
}
