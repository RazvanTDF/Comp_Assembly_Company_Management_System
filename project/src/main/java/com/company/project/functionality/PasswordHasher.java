package com.company.project.functionality;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Obține un obiect MessageDigest pentru algoritmul SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Convertiți parola într-un șir de octeți și aplicați funcția de hash
            byte[] hashBytes = digest.digest(password.getBytes());
            // Convertiți rezultatul în format hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Gestionare erori
            e.printStackTrace();
            return null;
        }
    }
}
