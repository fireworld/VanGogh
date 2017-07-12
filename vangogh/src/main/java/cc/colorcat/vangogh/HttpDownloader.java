package cc.colorcat.vangogh;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

class HttpDownloader implements Downloader {
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private HttpURLConnection conn;

    @Override
    public Result load(VanGogh vanGogh, Task task) throws IOException {
        Uri uri = task.uri();
        String scheme = uri.getScheme();
        if (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme)) {
            conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                long contentLength = conn.getContentLength();
                if (is != null && contentLength > 0) {
                    return new Result(is, contentLength, LoadedFrom.NETWORK);
                }
            }
            throw new IOException("network error, code = " + code + ", msg = " + conn.getResponseMessage());
        }
        return null;
    }

    @Override
    public void shutDown() {
        if (conn != null) {
            conn.disconnect();
        }
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Downloader clone() {
        return new HttpDownloader();
    }
}
