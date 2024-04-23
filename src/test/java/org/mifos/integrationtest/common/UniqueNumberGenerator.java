package org.mifos.integrationtest.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@SuppressWarnings("HideUtilityClassConstructor")
public class UniqueNumberGenerator {

    public static String generateUniqueNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }

        StringBuilder sb = new StringBuilder();

        while (sb.length() < length) {
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();

            String hashedString = hashString(randomUUIDString);

            StringBuilder numericStringBuilder = new StringBuilder();
            for (char c : hashedString.toCharArray()) {
                if (Character.isDigit(c)) {
                    numericStringBuilder.append(c);
                }
            }

            sb.append(numericStringBuilder);
        }

        return sb.substring(0, length);
    }

    private static String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occurred while hashing the string", e);
        }
    }
}
