package Authentication;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class DES {
    static IvParameterSpec iv;

    public static byte[] encrypt(String toEncrypt, String key) throws Exception {
        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(sr);
        SecretKey sk = kg.generateKey();
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivspec = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(ivspec);
        iv = new IvParameterSpec(ivspec);
        cipher.init(Cipher.ENCRYPT_MODE, sk, iv);
        byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());

        return encrypted;
    }

    public static String decrypt(byte[] toDecrypt, String key) throws Exception {
        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(sr);
        SecretKey sk = kg.generateKey();
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sk, iv);
        byte[] decrypted = cipher.doFinal(toDecrypt);

        return new String(decrypted);
    }
}
