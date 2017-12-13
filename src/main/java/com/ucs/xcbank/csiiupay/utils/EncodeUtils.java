package com.ucs.xcbank.csiiupay.utils;


import java.io.IOException;

public class EncodeUtils {
    public  static String base64UrlEncode(byte[] input){
        String   output = new sun.misc.BASE64Encoder().encode(input);
        output = output.split("=")[0]; // Remove any trailing '='s
        output = output.replace('+', '-'); // 62nd char of encoding
        output = output.replace('/', '_'); // 63rd char of encoding
        return output;
    }

    public static byte[] base64UrlDecode(String input) throws Exception
    {
        String output = input;
        output = output.replace('-', '+'); // 62nd char of encoding
        output = output.replace('_', '/'); // 63rd char of encoding
        switch (output.length() % 4) // Pad with trailing '='s
        {
            case 0: break; // No pad chars in this case
            case 2: output += "=="; break; // Two pad chars
            case 3: output += "="; break;  // One pad char
            default: throw new Exception("Illegal base64url string!");
        }
        byte[] converted = null;
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        converted = decoder.decodeBuffer(output);
        return converted;
    }
}
