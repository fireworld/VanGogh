package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
public class Task {
    private Uri uri;
    private String key;
    private int maxWidth = -1;
    private int maxHeight = -1;
    private Bitmap.Config config;
    private LoadedFrom reqFrom = LoadedFrom.ANY;
    private AtomicInteger executedCount = new AtomicInteger(0);

    Task(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null || key == null");
        }
        this.uri = uri;
        this.key = Utils.md5(uri.toString());
    }

    Task(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }
        this.uri = Uri.parse(url);
        this.key = Utils.md5(url);
    }

    void setFrom(LoadedFrom from) {
        reqFrom = from;
    }

    LoadedFrom from() {
        return reqFrom;
    }

    public Uri getUri() {
        return uri;
    }

    public String getKey() {
        return key;
    }

    int getAndIncrementCount() {
        return executedCount.getAndIncrement();
    }

    void setMaxSize(int width, int height) {
        this.maxWidth = width;
        this.maxHeight = height;
    }

    public boolean hasSize() {
        return maxWidth > 0 && maxHeight > 0;
    }

    public int maxWidth() {
        return maxWidth;
    }

    public int maxHeight() {
        return maxHeight;
    }

    void config(Bitmap.Config config) {
        this.config = config;
    }

    public Bitmap.Config config() {
        return this.config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (!key.equals(task.key)) return false;
        return reqFrom == task.reqFrom;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + reqFrom.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "uri=" + uri +
                ", key='" + key + '\'' +
                ", reqFrom=" + reqFrom +
                ", executedCount=" + executedCount +
                '}';
    }
}
