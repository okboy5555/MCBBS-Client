package net.mcbbs.client.fixer.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author InitAuther97 yinyangshi Yaossg
 */
public final class IOUtils {
    /**
     * Utility used to get a {@code java.net.URLConnection} to the specified address.
     *
     * @param path target to connect
     * @return A connection to the specified address.
     * @throws IOException if giving a invalid path or failed to open a connection or connection timed out.
     */
    public static URLConnection connect(String path) throws IOException {
        URL url = new URL(path);
        return url.openConnection();
    }

    /**
     * Utility used to get a {@code java.io.InputStream} linked to the specified address
     *
     * @param url target to connect
     * @return A {@code java.io.InputStream} that reads the data from the specified address
     * @throws IOException if giving a invalid path or failed to open a connection or a stream or connection timed out.
     */
    public static InputStream from(String url) throws IOException {
        return connect(url).getInputStream();
    }

    /**
     * Utility used to get a {@code java.io.OutputStream} linked to the specified address
     *
     * @param url target to connect
     * @return A {@code java.io.InputStream} that reads the data from the specified address
     * @throws IOException if giving a invalid path or failed to open a connection or a stream or connection timed out.
     */
    public static OutputStream to(String url) throws IOException {
        return connect(url).getOutputStream();
    }

    /**
     * Utility used to get a {@code net.mcbbs.client.fixer.util.IOUtils.IOStream} from the specified address
     *
     * @param url target to connect
     * @return A {@code net.mcbbs.client.fixer.util.IOUtils.IOStream} that contains a {@code java.io.InputStream} and a {@code java.io.OutputStream} linked to the specified address
     * @throws IOException if giving a invalid path,failed to open a connection or a stream or connection timed out.
     */
    public static IOStream ioStream(String url) throws IOException {
        return new IOStream(from(url), to(url));
    }

    /**
     * Utility used to get a {@code net.mcbbs.client.fixer.util.IOUtils.IOStream} from the specified address
     *
     * @param in  target that {@code java.io.InputStream} connects to
     * @param out target that {@code java.io.OutputStream} connects to
     * @return A {@code net.mcbbs.client.fixer.util.IOUtils.IOStream} that contains a {@code java.io.InputStream} and a {@code java.io.OutputStream} linked to the specified address
     * @throws IOException if giving a invalid path,failed to open a connection or a stream or connection timed out.
     */
    public static IOStream ioStream(String in, String out) throws IOException {
        return new IOStream(from(in), to(out));
    }

    /**
     * Utility used to read all datas from a stream synchronously.
     *
     * @param in a {@code java.io.InputStream}
     * @return Data from the specified stream.
     * @throws IOException if failed to read data or the connection timed out
     */
    public static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        //max temp: 32kb
        byte[] temp = new byte[32 * 1024];
        int len;
        while (-1 != (len = in.read(temp))) {
            bytesOut.write(temp, 0, len);
        }
        return bytesOut.toByteArray();
    }

    /**
     * Utility used to read all datas from a url synchronously.
     *
     * @param url target to read data.
     * @return Data from the specified url.
     * @throws IOException if failed to open a stream,read data or the connection timed out
     */
    public static byte[] readFullyFrom(String url) throws IOException {
        try (InputStream is = from(url)) {
            return readFully(is);
        }
    }

    /**
     * Utility used to write all data to a specified {@code java.io.OutputStream}
     *
     * @param outputStream target to write to
     * @param data         Data to write
     * @return if write successfully
     * @throws IOException if failed to write data or the connection timed out
     */
    public static boolean writeAllAndClose(OutputStream outputStream, byte[] data) throws IOException {
        try (Closeable ignore = outputStream) {
            outputStream.write(data);
            outputStream.flush();
        }
        return true;
    }

    /**
     * Utility used to bind a {@code java.io.InputStream} and a {@code java.io.OutputStream} to a utility {@code net.mcbbs.client.fixer.util.IOUtils.IOStream}.
     *
     * @param out a {@code java.io.OutputStream}
     * @param in  a {@code java.io.InputStream}
     * @return a {@code net.mcbbs.client.fixer.util.IOUtils.IOStream} with two streams bound together
     */
    public static IOStream bindStream(OutputStream out, InputStream in) {
        return new IOStream(in, out);
    }
}