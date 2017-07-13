package cc.colorcat.vangogh;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */

public enum LoadedFrom {
    ANY(Color.TRANSPARENT, LoadedFrom.POLICY_MEMORY | LoadedFrom.POLICY_DISK | LoadedFrom.POLICY_NETWORK),
    MEMORY(Color.GREEN, LoadedFrom.POLICY_MEMORY),
    DISK(Color.BLUE, LoadedFrom.POLICY_DISK),
    NETWORK(Color.RED, LoadedFrom.POLICY_NETWORK);

    final int debugColor;
    final int policy;

    private static final int POLICY_MEMORY = 0x0001;
    private static final int POLICY_DISK = 0x0002;
    private static final int POLICY_NETWORK = 0x0004;

    private LoadedFrom(@ColorInt int debugColor, int policy) {
        this.debugColor = debugColor;
        this.policy = policy;
    }
}
