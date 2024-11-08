package com.example.SanChoi247.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class San {

    private int san_id;

    public San(LoaiSan loaiSan, String vi_tri_san, Size size, String img) {
        this.loaiSan = loaiSan;
        this.vi_tri_san = vi_tri_san;
        this.size = size;
        this.img = img;
    }

    private User user;
    private LoaiSan loaiSan;
    private String vi_tri_san;
    private Size size;
    private String img;
    private int is_approve;// 0. pending 1. Approved 2. Hide 3. Rejected 4.Request PayMent 5. Resolved\
    private int eyeview;
}
