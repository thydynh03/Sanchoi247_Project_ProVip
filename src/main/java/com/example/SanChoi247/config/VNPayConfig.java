package com.example.SanChoi247.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class VNPayConfig {
    private static final String vnpHashSecret = "6FIZ3WHDN6SL2OTC3NGZ71FGW6HGCPKN";
    private static final String vnpTmnCode = "PA826OPP";
    private static final String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String vnpReturnUrl = "http://localhost:8080/vnpay_return";

    public static String generateVnpayUrl(String orderId, String totalPrice) throws Exception {
        String vnp_TxnRef = VNPayConfig.getRandomAlphanumericString(64);
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        // vnpParams.put("vnp_BankCode", "NCB");
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_IpAddr", "192.168.1.15");
        // Convert totalPrice to a double to handle decimal values and multiply by 100
        // for VND conversion
        double price = Double.parseDouble(totalPrice) * 100;
        vnpParams.put("vnp_Amount", String.valueOf((long) price)); // Convert to long to avoid decimals
        System.out.println("Amount: " + price);

        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", URLEncoder.encode("Thanh toan san bong: " + orderId, "UTF-8"));

        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Sort the parameters and build the query string
        // Sort the parameters and build the query string
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && value.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, "UTF-8")).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, "UTF-8")).append('&');
            }
        }
        hashData.setLength(hashData.length() - 1); // Remove the last '&'

        query.setLength(query.length() - 1);
        System.out.println("Create Date: " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Create the HMAC SHA512 hash
        String vnpSecureHash = hmacSHA512(vnpHashSecret, hashData.toString());

        String paymentUrl = vnpUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnpSecureHash;
        vnpParams.put("vnp_OrderInfo", "Thanh toan san bong: " + orderId);

        return paymentUrl;
    }

    public static String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] hashBytes = hmac512.doFinal(data.getBytes("UTF-8"));
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    public static String getRandomAlphanumericString(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}