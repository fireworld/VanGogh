package cc.colorcat.vangogh;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public class DefaultDownloader implements Downloader {
    private HttpURLConnection conn;

    @Override
    public Response load(String url) throws IOException {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int code = conn.getResponseCode();
        if (code == 200) {
            InputStream is = conn.getInputStream();
            long contentLength = conn.getContentLength();
            if (is != null) {
                return new Response(is, contentLength);
            }
        }
        return null;
    }

    @Override
    public void shutdown() {
        conn.disconnect();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Downloader clone() {
        return new DefaultDownloader();
    }
}
