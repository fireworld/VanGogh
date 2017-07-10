package cc.colorcat.vangogh;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */

public class VanGogh {
    private static VanGogh singleInstance;
    private Dispatcher dispatcher;


    /**
     * Describes where the image was loaded from.
     */
    public enum LoadedFrom {
        MEMORY(Color.GREEN),
        DISK(Color.BLUE),
        NETWORK(Color.RED);

        @ColorInt
        final int debugColor;

        private LoadedFrom(@ColorInt int debugColor) {
            this.debugColor = debugColor;
        }
    }
}
