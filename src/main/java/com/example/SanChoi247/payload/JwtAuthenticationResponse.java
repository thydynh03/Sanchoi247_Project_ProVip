package com.example.SanChoi247.payload;


public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn; // Thời gian token hết hạn (tính bằng milliseconds)
    private String username; // Thêm trường để lưu username
    private int uid; // Thêm trường để lưu UID
    private String avatar; // Thêm trường để lưu avatar
    private String name; // Thêm trường để lưu tên người dùng

    public JwtAuthenticationResponse(String accessToken, long expiresIn, String username, int uid, String avatar, String name) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn; // Khởi tạo giá trị thời gian hết hạn
        this.username = username; // Khởi tạo giá trị username
        this.uid = uid; // Khởi tạo giá trị UID
        this.avatar = avatar; // Khởi tạo giá trị avatar
        this.name = name; // Khởi tạo giá trị name
    }

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

}
