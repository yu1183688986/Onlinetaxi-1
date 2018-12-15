package com.minicreate.online_taxi.utils;

public class BytesUtil {
    public static String BytestoHexStringPrintf(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b) + " ");
        }

        return builder.toString();
    }

    public static String BytestoHexStringPrintf(byte[] bytes, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(String.format("%02x", bytes[i]) + " ");
        }

        return builder.toString();
    }
}
