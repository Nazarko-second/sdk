package utils;

import java.util.Base64;

public class Base64Coder {

    public static String encode(String text) {
        String encodedString = Base64.getEncoder().encodeToString(text.getBytes());
//        System.out.println("encodedString" + encodedString);
        return encodedString;
    }

    public static String decode(String text) {
        byte[] decodedBytes = Base64.getDecoder().decode(text.getBytes());
        String decodedString = new String(decodedBytes);
//        System.out.println("decodedString " + decodedString);
        return decodedString;
    }


}
