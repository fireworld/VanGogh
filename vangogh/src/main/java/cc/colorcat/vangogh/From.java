package cc.colorcat.vangogh;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */

public enum From {
    MEMORY(Color.GREEN, From.POLICY_MEMORY),
    DISK(Color.BLUE, From.POLICY_DISK),
    NETWORK(Color.RED, From.POLICY_NETWORK),
    ANY(Color.TRANSPARENT, From.POLICY_MEMORY | From.POLICY_DISK | From.POLICY_NETWORK);

    final int debugColor;
    public final int policy;

    private static final int POLICY_MEMORY = 1;
    private static final int POLICY_DISK = 1 << 1;
    private static final int POLICY_NETWORK = 1 << 2;

    public static void checkFromPolicy(int fromPolicy) {
        if ((fromPolicy & From.ANY.policy) == 0) {
            throw new IllegalArgumentException("illegal fromPolicy = " + fromPolicy);
        }
    }

    From(@ColorInt int debugColor, int policy) {
        this.debugColor = debugColor;
        this.policy = policy;
    }
}
