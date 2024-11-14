package com.example.SanChoi247.payload;

public class JwtAuthenticationResponse {
    private String accessToken; // Token để xác thực người dùng
    private String tokenType = "Bearer"; // Loại token, mặc định là Bearer
    private long expiresIn; // Thời gian token hết hạn (tính bằng milliseconds)
    private String username; // Tên người dùng
    private int uid; // UID của người dùng
    private String avatar; // Avatar của người dùng
    private String name; // Tên hiển thị của người dùng
    private char role; // Vai trò của người dùng (char)

    // Constructor để khởi tạo các thuộc tính
    public JwtAuthenticationResponse(String accessToken, long expiresIn, String username, int uid, String avatar, String name, char role) {
        this.accessToken = accessToken; // Khởi tạo giá trị accessToken
        this.expiresIn = expiresIn; // Khởi tạo giá trị thời gian hết hạn
        this.username = username; // Khởi tạo giá trị username
        this.uid = uid; // Khởi tạo giá trị UID
        this.avatar = avatar; // Khởi tạo giá trị avatar
        this.name = name; // Khởi tạo giá trị name
        this.role = role; // Khởi tạo giá trị role
    }

    // Getter và Setter cho từng thuộc tính
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getRole() {
        return role; // Trả về vai trò của người dùng
    }

    public void setRole(char role) {
        this.role = role; // Đặt vai trò của người dùng
    }
}
