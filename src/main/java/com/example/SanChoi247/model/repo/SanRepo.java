package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.LoaiSan;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.Size;
import com.example.SanChoi247.model.entity.User;

@Repository
public class SanRepo {
    @Autowired
    LoaiSanRepo loaiSanRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    SizeRepo sizeRepo;

    public ArrayList<San> getAllSan() throws Exception {
        ArrayList<San> sanList = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM san");
        while (rs.next()) {
            int san_id = rs.getInt("san_id");
            int loai_san_id = rs.getInt("loai_san_id");
            LoaiSan loaiSan = loaiSanRepo.getLoaiSanById(loai_san_id);
            String vi_tri_san = rs.getString("vi_tri_san");
            int size_id = rs.getInt("size_id");
            Size size = sizeRepo.getSizeById(size_id);
            int uid = rs.getInt("uid");
            User user = userRepo.getUserById(uid);
            String img = rs.getString("img");
            int is_approve = rs.getInt("is_approve");
            int eyeview = rs.getInt("eyeview");
            San san = new San(san_id, user, loaiSan, vi_tri_san, size, img, is_approve, eyeview);
            sanList.add(san);
        }
        rs.close();
        stm.close();
        con.close();
        return sanList;
    }

    public void addNewSan(San san) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO san (uid, loai_san_id, vi_tri_san, size_id, img, is_approve) values(?,?,?,?,?,?)");
        ps.setInt(1, san.getUser().getUid());
        ps.setInt(2, san.getLoaiSan().getLoai_san_id());
        ps.setString(3, san.getVi_tri_san());
        ps.setInt(4, san.getSize().getSize_id());
        ps.setString(5, san.getImg());
        ps.setInt(6, san.getIs_approve());
        ps.executeUpdate();
        ps.close();
    }

    public San getSanById(int san_id) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("select * from san where san_id = ?");
        ps.setInt(1, san_id);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        int san_id1 = rs.getInt("san_id");
        int loai_san_id = rs.getInt("loai_san_id");
        LoaiSan loaiSan = loaiSanRepo.getLoaiSanById(loai_san_id);
        String vi_tri_san = rs.getString("vi_tri_san");
        int size_id = rs.getInt("size_id");
        Size size = sizeRepo.getSizeById(size_id);
        int uid = rs.getInt("uid");
        User user = userRepo.getUserById(uid);
        String img = rs.getString("img");
        int is_approve = rs.getInt("is_approve");
        int eyeview = rs.getInt("eyeview");
        San san = new San(san_id1, user, loaiSan, vi_tri_san, size, img, is_approve, eyeview);
        return san;
    }

    public San getSanByName(String name) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("select * from san where name = ?");
        ps.setString(1, name);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        int san_id = rs.getInt("san_id");
        int loai_san_id = rs.getInt("loai_san_id");
        LoaiSan loaiSan = loaiSanRepo.getLoaiSanById(loai_san_id);
        String vi_tri_san = rs.getString("vi_tri_san");
        int size_id = rs.getInt("size_id");
        Size size = sizeRepo.getSizeById(size_id);
        int uid = rs.getInt("uid");
        User user = userRepo.getUserById(uid);
        String img = rs.getString("img");
        int is_approve = rs.getInt("is_approve");
        int eyeview = rs.getInt("eyeview");
        San san = new San(san_id, user, loaiSan, vi_tri_san, size, img, is_approve, eyeview);
        return san;
    }

    public ArrayList<San> getAllSanByChuSanId(int uid) throws Exception {
        ArrayList<San> sanList = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("SELECT * FROM san WHERE uid = ?");
        ps.setInt(1, uid);
        ResultSet rs = ps.executeQuery(); // Sử dụng executeQuery() để trả về ResultSet

        // Duyệt qua toàn bộ kết quả trả về
        while (rs.next()) {
            int san_id = rs.getInt("san_id");
            int loai_san_id = rs.getInt("loai_san_id");
            LoaiSan loaiSan = loaiSanRepo.getLoaiSanById(loai_san_id); // Lấy thông tin Loại Sân từ Repo
            String vi_tri_san = rs.getString("vi_tri_san");
            int size_id = rs.getInt("size_id");
            Size size = sizeRepo.getSizeById(size_id); // Lấy thông tin Size từ Repo
            int uid1 = rs.getInt("uid");
            User user = userRepo.getUserById(uid1); // Lấy thông tin User từ Repo
            String img = rs.getString("img");
            int is_approve = rs.getInt("is_approve");
            int eyeview = rs.getInt("eyeview");

            // Tạo đối tượng San với các dữ liệu vừa lấy
            San san = new San(san_id, user, loaiSan, vi_tri_san, size, img, is_approve, eyeview);

            // Thêm đối tượng San vào danh sách
            sanList.add(san);
        }

        // Đóng kết nối và tài nguyên
        rs.close();
        ps.close();
        con.close();

        return sanList;
    }

    public void save(San san) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET loai_san_id =?, vi_tri_san = ?, size_id = ?, img = ? WHERE san_id = ?");
        ps.setInt(1, san.getLoaiSan().getLoai_san_id());
        ps.setString(2, san.getVi_tri_san());
        ps.setInt(3, san.getSize().getSize_id());
        ps.setString(4, san.getImg());
        ps.executeUpdate();
        ps.close();
        con.close();
    }
}