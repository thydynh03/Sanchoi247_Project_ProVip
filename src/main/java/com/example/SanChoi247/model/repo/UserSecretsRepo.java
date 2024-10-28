// package com.example.SanChoi247.model.repo;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.util.Base64;

// import com.example.SanChoi247.model.dto.UserSecretInfo;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Repository;

// import javax.crypto.Cipher;
// import javax.crypto.spec.SecretKeySpec;

// @Repository
// public class UserSecretsRepo {
//     private static final String INSERT_SECRET_KEY_SQL = "INSERT INTO userSecret(uid, secret_key, created_at) VALUES (?, ?, ?)";
//     private static final String SELECT_SECRET_KEY_SQL = "SELECT secret_key FROM userSecret WHERE uid = ?";
//     private static final String DELETE_SECRET_KEY_SQL = "DELETE FROM userSecret WHERE uid = ?";
//     private static final String FIND_BY_ID_SQL = "SELECT * FROM userSecret WHERE uid = ?";
//     @Value("${ENCRYPTION_KEY}")
//     @Autowired
//     private String ENCRYPTION_KEY;

//     public UserSecretInfo findByUserId(int uid) throws Exception {
//         Class.forName(Baseconnection.nameClass);
//         try (Connection con = DriverManager.getConnection(Baseconnection.url,
//                 Baseconnection.username,
//                 Baseconnection.password)) {
//             PreparedStatement ps = con.prepareStatement(FIND_BY_ID_SQL);
//             ps.setInt(1, uid);

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     UserSecretInfo userSecretInfo = new UserSecretInfo();
//                     userSecretInfo.setUid(rs.getInt("uid"));
//                     userSecretInfo.setSecretKey(decrypt(rs.getString("secret_key")));
//                     userSecretInfo.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                     return userSecretInfo;
//                 }
//             }
//         }

//         return null;
//     }

//     public void insertSecretKey(int uid, String secretKey) throws Exception {

//         // Encrypt the secret key before storing it
//         String encryptedSecretKey = encrypt(secretKey);
//         System.out.println(encryptedSecretKey.length());
//         Class.forName(Baseconnection.nameClass);
//         try (Connection con = DriverManager.getConnection(Baseconnection.url,
//                 Baseconnection.username,
//                 Baseconnection.password);
//                 PreparedStatement ps = con.prepareStatement(INSERT_SECRET_KEY_SQL)) {
//             ps.setInt(1, uid);
//             ps.setString(2, encryptedSecretKey);
//             ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));

//             ps.executeUpdate();
//         }
//     }

//     public String getSecretKey(int uid) throws Exception {
//         Class.forName(Baseconnection.nameClass);
//         try (Connection con = DriverManager.getConnection(Baseconnection.url,
//                 Baseconnection.username,
//                 Baseconnection.password);
//                 PreparedStatement ps = con.prepareStatement(SELECT_SECRET_KEY_SQL)) {

//             ps.setInt(1, uid);

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     // Decrypt the secret key before returning it
//                     return decrypt(rs.getString("secret_key"));
//                 }
//             }
//         }
//         return null;
//     }

//     public void deleteSecretKey(int uid) throws Exception {
//         Class.forName(Baseconnection.nameClass);
//         try (Connection con = DriverManager.getConnection(Baseconnection.url,
//                 Baseconnection.username,
//                 Baseconnection.password);
//                 PreparedStatement ps = con.prepareStatement(DELETE_SECRET_KEY_SQL)) {
//             ps.setInt(1, uid);
//             ps.executeUpdate();
//         }
//     }

//     private String encrypt(String secretKey) throws Exception {
//         SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
//         Cipher cipher = Cipher.getInstance("AES");
//         cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//         byte[] encrypted = cipher.doFinal(secretKey.getBytes());
//         return Base64.getEncoder().encodeToString(encrypted);
//     }

//     private String decrypt(String encryptedSecretKey) throws Exception {
//         SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
//         Cipher cipher = Cipher.getInstance("AES");
//         cipher.init(Cipher.DECRYPT_MODE, keySpec);
//         byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedSecretKey));
//         return new String(decrypted);
//     }
// }
