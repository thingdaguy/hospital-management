package com.hospital.app.dao;

import com.hospital.app.entity.CaTruc;
import com.hospital.app.util.JpaUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CaTrucDAO {

    public List<CaTruc> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<CaTruc> q = em.createQuery(
                    "SELECT c FROM CaTruc c", CaTruc.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    public CaTruc findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(CaTruc.class, id);
        } finally {
            em.close();
        }
    }
    public void save(CaTruc ct) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ct);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void update(CaTruc ct) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(ct);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CaTruc ct = em.find(CaTruc.class, id);
            if (ct != null) em.remove(ct);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}