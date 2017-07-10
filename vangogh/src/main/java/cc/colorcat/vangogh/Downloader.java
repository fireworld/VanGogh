package cc.colorcat.vangogh;

import java.io.IOException;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

public interface Downloader extends Cloneable {

    Result load(VanGogh vanGogh, Request request) throws IOException;

    void shutDown();

    Downloader clone();
}
