package com.example.SanChoi247.model.entity;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class User {
    // uid int auto_increment primary key,
    // name varchar(64),
    // dob date,
    // gender varchar(1),
    // phone varchar(16),
    // email varchar(50),
    // is_active tinyint,
    // website text,
    // username varchar(64),
    // password varchar(128),
    // avatar text,
    // role varchar(1)
    private int uid;
    private String name;
    private Date dob;
    private char gender;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String avatar;
    // thuộc tính của owner
    private String ten_san;
    private String address;
    private String img_san1;
    private String img_san2;
    private String img_san3;
    private String img_san4;
    private String img_san5;
    private int status; // 0.mặc định của user, 1.đang chờ duyệt lên owner, 2.được duyệt lên owner,
                        // 3.đang chờ duyệt đăng sân, 4.được duyệt tất cả
    private char role; // C là owner, U là User, A là admin

    public User(String name, Date dob, char gender, String phone, String email, String username, String password,
            String avatar, char role) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
    }

    public User(String name, Date dob, char gender, String phone, String email) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String name, Date dob, char gender, String phone, String email, String username, String password) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User orElseThrow(Object userNotFound) {
        return null;
    }

    public boolean isVerified() {
        return role != UserRole.UNVERIFIED_USER.getValue() && role != UserRole.UNVERIFIED_CHUSAN.getValue();
    }

    public UserRole getUserRole() {
        return UserRole.fromValue(role);
    }

    @Getter
    public enum UserRole {

        ADMIN('A'),

        ChuSan('C'),
        UNVERIFIED_CHUSAN('c'),
        BANNED_CHUSAN('p'),

        USER('U'),
        UNVERIFIED_USER('u'),
        BANNED_USER('b');

        private final char value;

        UserRole(char value) {
            this.value = value;
        }

        public static UserRole fromValue(char value) {
            for (UserRole role : UserRole.values()) {
                if (role.getValue() == value) {
                    return role;
                }
            }
            return null;
        }
    }
}
