package com.example.btl_android.Model;

public class SanPham {
    private String ten_san_pham;
    private int gia;
    private String mo_ta;
    private String hinh_anh;
    private String loai;
    private int so_luong_ton_kho;

    public SanPham() {
        // Default constructor required for calls to DataSnapshot.getValue(Phone.class)
    }

    // Getters and setters
    public String getTen_san_pham() { return ten_san_pham; }
    public void setTen_san_pham(String ten_san_pham) { this.ten_san_pham = ten_san_pham; }

    public int getGia() { return gia; }
    public void setGia(int gia) { this.gia = gia; }

    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }

    public String getHinh_anh() { return hinh_anh; }
    public void setHinh_anh(String hinh_anh) { this.hinh_anh = hinh_anh; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public int getSo_luong_ton_kho() { return so_luong_ton_kho; }
    public void setSo_luong_ton_kho(int so_luong_ton_kho) { this.so_luong_ton_kho = so_luong_ton_kho; }
}
