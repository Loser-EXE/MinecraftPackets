package com.loserexe.utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class MinecraftUUID {
    public static void writeBytes(DataOutputStream out, String uuid) throws IOException {
        String UUID = uuid.replaceAll("-", "");
        int[] UuidToHex = hexStringToByteArray(UUID);

        for (int i : UuidToHex) {
            out.writeByte(i);
        }
    }

    private static int[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        int[] data = new int[len/2];
        for (int i = 0; i < len; i+=2) {
            data[i/2] = ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }
}
