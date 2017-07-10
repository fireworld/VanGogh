package cc.colorcat.vangogh;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

public class HttpDownloader implements Downloader {
    private HttpURLConnection conn;

    @Override
    public Result load(VanGogh vanGogh, Request request) throws IOException {
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
