package com.ArcaneScrolls.config;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static SecretKey generateEncryptionKey() {
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static byte[] encryptEncryptionKey(SecretKey encryptionKey, String userPassword) throws Exception {
        byte[] salt = generateSalt();
        SecretKey secretKey = generateSecretKey(userPassword, salt);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, generateIV());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] encryptedKeyBytes = cipher.doFinal(encryptionKey.getEncoded());

        return concatenate(salt, gcmParameterSpec.getIV(), encryptedKeyBytes);
    }

    public static SecretKey decryptEncryptionKey(byte[] encryptedEncryptionKey, String userPassword) throws Exception {
        byte[] salt = Arrays.copyOfRange(encryptedEncryptionKey, 0, 16);
        byte[] iv = Arrays.copyOfRange(encryptedEncryptionKey, 16, 28);
        byte[] encryptedKeyBytes = Arrays.copyOfRange(encryptedEncryptionKey, 28, encryptedEncryptionKey.length);

        SecretKey secretKey = generateSecretKey(userPassword, salt);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);

        return new SecretKeySpec(decryptedKeyBytes, ALGORITHM);
    }

    public static String encrypt(String data, SecretKey encryptionKey) throws Exception {
        byte[] iv = generateIV();

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, gcmParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(concatenate(iv, encryptedBytes));
    }

    public static String decrypt(String encryptedData, SecretKey encryptionKey) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] iv = Arrays.copyOfRange(decodedBytes, 0, GCM_IV_LENGTH);
        byte[] encryptedBytes = Arrays.copyOfRange(decodedBytes, GCM_IV_LENGTH, decodedBytes.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, gcmParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }

    private static SecretKey generateSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

        Arrays.fill(spec.getPassword(), '\0');

        return secretKeySpec;
    }

    private static byte[] concatenate(byte[]... arrays) {
        int length = Arrays.stream(arrays).mapToInt(arr -> arr.length).sum();
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static String encodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decodeFromString(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
}