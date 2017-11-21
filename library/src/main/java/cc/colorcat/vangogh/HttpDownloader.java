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
    private HttpURLConnection conn;

    @Override
    public Result load(VanGogh vanGogh, Task task) throws IOException {
        Uri uri = task.uri();
        conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(task.connectTimeOut());
        conn.setReadTimeout(task.readTimeOut());
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            if (is != null) {
                long contentLength = conn.getContentLength();
                if (contentLength > 0) {
                    return new Result(is, contentLength, From.NETWORK);
                } else {
                    return new Result(is, From.NETWORK);
                }
            }
        }
        throw new IOException("network error, code = " + code + ", msg = " + conn.getResponseMessage());
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
