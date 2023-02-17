package tools;

import java.security.SecureRandom;

public class Tools {
    public String chars;

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(this.chars.length());
            sb.append(this.chars.charAt(randomIndex));
        }
        return sb.toString();
    }
}
