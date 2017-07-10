package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public class Dispatcher {
    private Handler handler = new Handler(Looper.getMainLooper());

    private ExecutorService executor;
    private int maxRunning;
    private Queue<Request> requestQueue = new LinkedList<>();
    private Set<String> running;

    private Cache<Bitmap> memoryCache;
    private DiskCache diskCache;

    private Downloader downloader;

    public boolean enqueue(Request request) {
        if (!requestQueue.contains(request) && requestQueue.add(request)) {
            promoteRequest();
            return true;
        }
        return false;
    }

    private void promoteRequest() {

    }

    private class AsyncCall implements Runnable {
        private Request request;

        @Override
        public void run() {
            final String key = request.key();
            final String url = request.url();
            VanGogh.LoadedFrom from;
            Bitmap bitmap = memoryCache.get(key);
            if (bitmap == null) {
                bitmap = fromDisk(key);
                if (bitmap == null) {
                    bitmap = fromNetwork(key, url);
                    if (bitmap != null) {
                        from = VanGogh.LoadedFrom.NETWORK;
                    } else {
                        from = VanGogh.LoadedFrom.DISK;
                    }
                } else {
                    from = VanGogh.LoadedFrom.DISK;
                }
                if (bitmap != null) {
                    memoryCache.save(key, bitmap);
                }
            } else {
                from = VanGogh.LoadedFrom.MEMORY;
            }
            if (bitmap != null) {
                request.deliver(bitmap, from, null);
            }
        }


        private Bitmap fromDisk(String key) {
            InputStream is = null;
            try {
                DiskCache.Snapshot snapshot = diskCache.getSnapshot(key);
                is = snapshot.getInputStream();
                if (is != null) {
                    return Utils.decodeStream(is);
                }
            } finally {
                Utils.close(is);
            }
            return null;
        }

        private Bitmap fromNetwork(String key, String url) {
            InputStream is = null;
            OutputStream os = null;
            Downloader loader = downloader.clone();
            try {
                Downloader.Response response = loader.load(url);
                if (response != null) {
                    is = response.stream;
                    DiskCache.Snapshot snapshot = diskCache.getSnapshot(key);
                    os = snapshot.getOutputStream();
                    Utils.justDump(is, os);
                    return fromDisk(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Utils.close(is);
                Utils.close(os);
                loader.shutdown();
            }
            return null;
        }
    }
}
