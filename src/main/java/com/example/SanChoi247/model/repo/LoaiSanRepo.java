package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.LoaiSan;

@Repository
public class LoaiSanRepo {
    public ArrayList<LoaiSan> getAllLoaiSan() throws Exception{
        ArrayList<LoaiSan> loaiSans = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("select * from LoaiSan");
        while (rs.next()) {
            int loai_san_id = rs.getInt("loai_san_id");
            String loai_san_type = rs.getString("loai_san_type");
            LoaiSan loaiSan = new LoaiSan(loai_san_id, loai_san_type);
            loaiSans.add(loaiSan);
        }
        return loaiSans;
    }

    public LoaiSan getLoaiSanById(int loai_san_id) throws Exception{
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("select * from LoaiSan where loai_san_id = ?");
        ps.setInt(1, loai_san_id);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        int loai_san_id1 = rs.getInt("loai_san_id");
        String loai_san_type = rs.getString("loai_san_type");
        LoaiSan loaiSan = new LoaiSan(loai_san_id1, loai_san_type);
        return loaiSan;
    }
}
