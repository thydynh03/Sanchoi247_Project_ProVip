package com.example.SanChoi247.model.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.Size;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

@Repository
public class SizeRepo {
    @Autowired
    LoaiSanRepo loaiSanRepo;
    public ArrayList<Size> getAllSize() throws Exception{
        ArrayList<Size> sizeList = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("select * from size");
        while (rs.next()) {
            int size_id = rs.getInt("size_id");
            String size = rs.getString("size");
            Size size2 = new Size(size_id, size);
            sizeList.add(size2);
        }
        return sizeList;
    }

    public Size getSizeById(int size_id) throws Exception{
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("select * from size where size_id = ?");
        ps.setInt(1, size_id);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        int size_id1 = rs.getInt("size_id");
        String size = rs.getString("size");
        Size size2 = new Size(size_id1, size);
        return size2;
    }
}
