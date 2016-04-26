package x.douban.utils;

import android.util.Log;

/**
 * Created by Peter on 16/4/26.
 */
public class L {
    public final static String TAG = "douban";
    public static final void dbg(String msg) {
        Log.d(TAG, "" + msg);
    }
    public static final void info(String msg) {
        Log.i(TAG, "" + msg);
    }
    public static final void warning(String msg) {
        Log.w(TAG, "" + msg);
    }
    public static final void error(String msg) {
        Log.e(TAG, "" + msg);
    }
}
