package cc.colorcat.vangogh;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public interface Downloader extends Cloneable {

    Response load(String url) throws IOException;

    void shutdown();

    Downloader clone();

    class Response {
        final InputStream stream;
        final long contentLength;

        public Response(InputStream stream, long contentLength) {
            if (stream == null) {
                throw new IllegalArgumentException("Stream may not be null.");
            }
            this.stream = stream;
            this.contentLength = contentLength;
        }
    }
}
