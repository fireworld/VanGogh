package cc.colorcat.vangoghdemo;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by cxx on 15/12/1.
 * xx.ch@outlook.com
 */
public class Utils {

    static String readStream(String httpUrl, String charsetName) throws IOException {
        InputStream is = new URL(httpUrl).openStream();
        InputStreamReader reader = new InputStreamReader(is, charsetName);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            builder.append(line);
        }
        is.close();
        reader.close();
        br.close();
        return builder.toString();
    }

    static String readString(String httpUrl) throws IOException {
        BufferedReader reader = null;
        try {
            InputStream is = new URL(httpUrl).openStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            for (int length = reader.read(buffer); length != -1; length = reader.read(buffer)) {
                sb.append(buffer, 0, length);
            }
            return sb.toString();
        } finally {
            close(reader);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
