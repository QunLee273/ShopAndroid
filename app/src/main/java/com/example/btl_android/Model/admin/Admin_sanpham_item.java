package com.example.btl_android.Model.admin;
import java.io.Serializable;

import com.google.firebase.database.PropertyName;

public class Admin_sanpham_item implements Serializable {
    private String uid;
    private String maSanPham;
    private String tenSanPham;
    private int gia;
    private String moTa;
    private String nhaCungCap;
    private int soLuongTonKho;
    private String hinhAnh;
    private String loai;


    public Admin_sanpham_item() {
    }

    // Getter và Setter cho UID
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @PropertyName("ma_san_pham")
    public String getMaSanPham() {
        return maSanPham;
    }

    @PropertyName("ma_san_pham")
    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    @PropertyName("ten_san_pham")
    public String getTenSanPham() {
        return tenSanPham;
    }

    @PropertyName("ten_san_pham")
    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    @PropertyName("gia")
    public int getGia() {
        return gia;
    }

    @PropertyName("gia")
    public void setGia(int gia) {
        this.gia = gia;
    }

    @PropertyName("mo_ta")
    public String getMoTa() {
        return moTa;
    }

    @PropertyName("mo_ta")
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @PropertyName("nha_cung_cap")
    public String getNhaCungCap() {
        return nhaCungCap;
    }

    @PropertyName("nha_cung_cap")
    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    @PropertyName("so_luong_ton_kho")
    public int getSoLuongTonKho() {
        return soLuongTonKho;
    }

    @PropertyName("so_luong_ton_kho")
    public void setSoLuongTonKho(int soLuongTonKho) {
        this.soLuongTonKho = soLuongTonKho;
    }

    @PropertyName("hinh_anh")
    public String getHinhAnh() {
        return hinhAnh;
    }

    @PropertyName("hinh_anh")
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    @PropertyName("loai")
    public String getLoai() {
        return loai;
    }

    @PropertyName("loai")
    public void setLoai(String loai) {
        this.loai = loai;
    }
}
