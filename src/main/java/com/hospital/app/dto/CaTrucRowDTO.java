package com.hospital.app.dto;

import java.time.LocalTime;

public class CaTrucRowDTO {
    private String maCa;
    private String tenCa;
    private LocalTime batDau;
    private LocalTime ketThuc;

    public CaTrucRowDTO(String maCa, String tenCa, LocalTime batDau, LocalTime ketThuc) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.batDau = batDau;
        this.ketThuc = ketThuc;
    }

    public String getMaCa() { return maCa; }
    public String getTenCa() { return tenCa; }
    public LocalTime getBatDau() { return batDau; }
    public LocalTime getKetThuc() { return ketThuc; }
}