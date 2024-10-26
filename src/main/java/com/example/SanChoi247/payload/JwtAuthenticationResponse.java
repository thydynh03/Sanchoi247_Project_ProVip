package com.example.SanChoi247.payload;

public class JwtAuthenticationResponse {
    private String accessToken; // Token để xác thực người dùng
    private String tokenType = "Bearer"; // Loại token, mặc định là Bearer
    private long expiresIn; // Thời gian token hết hạn (tính bằng milliseconds)
    private String username; // Tên người dùng
    private int uid; // UID của người dùng
    private String avatar; // Avatar của người dùng
    private String name; // Tên hiển thị của người dùng

    // Constructor để khởi tạo các thuộc tính
    public JwtAuthenticationResponse(String accessToken, long expiresIn, String username, int uid, String avatar, String name) {
        this.accessToken = accessToken; // Khởi tạo giá trị accessToken
        this.expiresIn = expiresIn; // Khởi tạo giá trị thời gian hết hạn
        this.username = username; // Khởi tạo giá trị username
        this.uid = uid; // Khởi tạo giá trị UID
        this.avatar = avatar; // Khởi tạo giá trị avatar
        this.name = name; // Khởi tạo giá trị name
    }

    // Getter và Setter cho từng thuộc tính
    public String getAccessToken() {
        return accessToken; // Trả về accessToken
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken; // Đặt giá trị accessToken
    }

    public String getTokenType() {
        return tokenType; // Trả về loại token
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType; // Đặt loại token
    }

    public long getExpiresIn() {
        return expiresIn; // Trả về thời gian hết hạn
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn; // Đặt thời gian hết hạn
    }

    public String getUsername() {
        return username; // Trả về tên người dùng
    }

    public void setUsername(String username) {
        this.username = username; // Đặt tên người dùng
    }

    public int getUid() {
        return uid; // Trả về UID
    }

    public void setUid(int uid) {
        this.uid = uid; // Đặt UID
    }

    public String getAvatar() {
        return avatar; // Trả về avatar
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar; // Đặt avatar
    }

    public String getName() {
        return name; // Trả về tên hiển thị
    }

    public void setName(String name) {
        this.name = name; // Đặt tên hiển thị
    }
}
