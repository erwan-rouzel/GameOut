package fr.ecp.sio.gameout.remote;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Created by erwan on 11/11/2015.
 */
public class GameoutUtils {
    public static byte[] stringToBytes(String string) {
        return string.getBytes(Charset.forName("UTF-8"));
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static String charsToString(char[] chars) {
        return new String(chars);
    }

    public static int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
        byte[] buffer = {b1, b2, b3, b4};
        ByteBuffer wrapped = ByteBuffer.wrap(buffer); // big-endian by default
        return wrapped.getInt();
    }

    public static short bytesToShort(byte b1, byte b2) {
        byte[] buffer = {b1, b2};
        ByteBuffer wrapped = ByteBuffer.wrap(buffer); // big-endian by default
        return wrapped.getShort();
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putLong(x);
        return buffer.array();
    }

    public static byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putShort(x);
        return buffer.array();
    }

    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putInt(x);
        return buffer.array();
    }
}