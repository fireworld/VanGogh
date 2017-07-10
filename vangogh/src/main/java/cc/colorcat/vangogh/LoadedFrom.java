package cc.colorcat.vangogh;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */

public enum LoadedFrom {
    MEMORY(Color.GREEN),
    DISK(Color.BLUE),
    NETWORK(Color.RED);

    final int debugColor;

    private LoadedFrom(@ColorInt int debugColor) {
        this.debugColor = debugColor;
    }
}
