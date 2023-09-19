package com.confession.comm;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * "PBEWithMD5AndDES"是一种密码基加密（Password-Based Encryption，PBE）算法
 */
public class EncryptionUtil {

    private static final String SECRET_KEY = "wall-zj-hxn";

    private static final StandardPBEStringEncryptor encryptor;

    static {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET_KEY);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
    }

    /**
     *  加密
     * @param input
     * @return
     */
    public static String encrypt(String input) {
        return encryptor.encrypt(input);
    }


    /**
     * 解密
     * @param encryptedText
     * @return
     */
    public static String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }

    public static void main(String[] args) {
        String plainText = "123456";
        String encryptedText = EncryptionUtil.encrypt(plainText);
        System.out.println(encryptedText);
        String decryptedText = EncryptionUtil.decrypt(encryptedText);
        System.out.println(decryptedText);
    }
}
