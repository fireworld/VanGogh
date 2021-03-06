package cc.colorcat.vangogh;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cxx on 16-3-8.
 * xx.ch@outlook.com
 */
final class LogUtils {
    private static final String TAG = "Temp";
    static final int VERBOSE = Log.VERBOSE;
    static final int DEBUG = Log.DEBUG;
    static final int INFO = Log.INFO;
    static final int WARN = Log.WARN;
    static final int ERROR = Log.ERROR;
    private static final int NOTHING = 10;
    private static int level = NOTHING;

    static void init(boolean debug) {
        level = debug ? VERBOSE : NOTHING;
    }

    static void v(String msg) {
        v(TAG, msg);
    }

    static void v(String tag, String msg) {
        if (VERBOSE >= level) {
            Log.v(tag, msg);
        }
    }

    static void d(String msg) {
        d(TAG, msg);
    }

    static void d(String tag, String msg) {
        if (DEBUG >= level) {
            Log.d(tag, msg);
        }
    }

    static void i(String msg) {
        i(TAG, msg);
    }

    static void i(String tag, String msg) {
        if (INFO >= level) {
            Log.i(tag, msg);
        }
    }

    static void w(String msg) {
        w(TAG, msg);
    }

    static void w(String tag, String msg) {
        if (WARN >= level) {
            Log.w(tag, msg);
        }
    }

    static void e(String msg) {
        e(TAG, msg);
    }

    static void e(String tag, String msg) {
        if (ERROR >= level) {
            Log.e(tag, msg);
        }
    }

    static void e(Throwable e) {
        if (ERROR >= level) {
            e.printStackTrace();
        }
    }

    static void ll(String tag, String msg, @Level int level) {
        Log.println(level, tag, msg);
    }

    static void ll(String msg, @Level int level) {
        Log.println(level, TAG, msg);
    }

    private LogUtils() {
        throw new AssertionError("no instance.");
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface Level {
    }
}