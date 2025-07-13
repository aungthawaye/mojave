
package io.mojaloop.common.component.handy;

import java.security.SecureRandom;

public class RandomString {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String NUMBERS = "0123456789";

    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    public static String generate(int length) {

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // Add at least one character from each set
        sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        sb.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Add remaining characters randomly
        for (int i = 3; i < length; i++) {
            String allChars = LETTERS + NUMBERS + SPECIAL_CHARS;
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the characters to make the order random
        return RandomString.shuffle(sb.toString());
    }

    private static String shuffle(String string) {

        char[] charArray = string.toCharArray();
        SecureRandom random = new SecureRandom();

        for (int i = charArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = charArray[index];
            charArray[index] = charArray[i];
            charArray[i] = temp;
        }

        return new String(charArray);
    }
}
