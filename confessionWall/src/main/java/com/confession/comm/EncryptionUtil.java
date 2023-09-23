package com.confession.comm;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * "PBEWithMD5AndDES"是一种密码基加密（Password-Based Encryption，PBE）算法
 */
public class EncryptionUtil {

    private static final String SECRET_KEY = "ThisIsASecretKey";

    private static final StandardPBEStringEncryptor encryptor;

    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
    }


    private static final String INITIALIZATION_VECTOR = "ThisIsASecretKey";  // 请替换为你自己的初始化向量

    public static String encrypt(String data) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(INITIALIZATION_VECTOR.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(INITIALIZATION_VECTOR.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decodedData), StandardCharsets.UTF_8);
    }



    public static void main(String[] args) throws Exception {
        String plainText = "222";
        String encryptedText = EncryptionUtil.encrypt(plainText);
        System.out.println(encryptedText);
        String decryptedText = EncryptionUtil.decrypt(encryptedText);
        System.out.println(decryptedText);
    }
}
