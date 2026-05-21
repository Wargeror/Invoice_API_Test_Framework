package api.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Utils {
    /**
     * Generates a random string of specified length
     * @param length length of the string
     * @return random string
     */
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return result.toString();
    }

    /**
     * Generates a random numeric string of specified length
     * @param length length of the string
     * @return random numeric string
     */
    public static String randomNumeric(int length) {
        String chars = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a valid Bulgarian EGN (Unified Civil Number)
     * @return A string representing a valid 10-digit EGN.
     */
    public static String generateValidEGN() {
        Random random = new Random();
        // Generate year (70-99 for 1900s, 00-23 for 2000s)
        int year = random.nextInt(54) + 70;
        if (year > 99) year -= 100;
        // Generate month (1-12 for 1900s, 41-52 for 2000s)
        int month = random.nextInt(12) + 1;
        if (year < 24) month += 40;
        // Generate day (1-28 to keep it simple)
        int day = random.nextInt(28) + 1;
        // Generate a random 3-digit region/birth number
        int region = random.nextInt(999);

        String partialEgn = String.format("%02d%02d%02d%03d", year, month, day, region);
        int[] weights = {2, 4, 8, 5, 10, 9, 7, 3, 6};
        int checksum = 0;
        for (int i = 0; i < 9; i++) {
            checksum += (partialEgn.charAt(i) - '0') * weights[i];
        }
        int lastDigit = checksum % 11;
        if (lastDigit == 10) {
            lastDigit = 0;
        }
        return partialEgn + lastDigit;
    }

    /**
     * Generates a valid 9-digit Bulgarian Bulstat number.
     * @return A string representing a valid 9-digit Bulstat.
     */
    public static String generateValidBulstat9() {
        String bulstat = randomNumeric(8);
        int[] weights = {1, 2, 3, 4, 5, 6, 7, 8};
        int checksum = 0;
        for (int i = 0; i < 8; i++) {
            checksum += (bulstat.charAt(i) - '0') * weights[i];
        }
        int remainder = checksum % 11;
        if (remainder == 10) {
            // If remainder is 10, recalculate with different weights
            int[] weights2 = {3, 4, 5, 6, 7, 8, 9, 10};
            checksum = 0;
            for (int i = 0; i < 8; i++) {
                checksum += (bulstat.charAt(i) - '0') * weights2[i];
            }
            remainder = checksum % 11;
            if (remainder == 10) {
                remainder = 0;
            }
        }
        return bulstat + remainder;
    }
    
    /**
     * Generates a valid Bulgarian VAT number.
     * @return A string representing a valid VAT number (BG + 9-digit Bulstat).
     */
    public static String generateValidVAT() {
        return "BG" + generateValidBulstat9();
    }
}
