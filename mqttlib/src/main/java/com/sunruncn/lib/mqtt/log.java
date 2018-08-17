package com.sunruncn.lib.mqtt;

import android.util.Log;

/**
 * Created by admin on 2015/4/21.
 */
public class log {
    final static String COMMON_TAG = "ico_";
    /**
     * 日志等级,从e到v依次为1到5，若输出全关则设置0
     * out等同i，err等同e
     */
    final static int LEVEL = 0;

    /**
     * 将一个字符串数组根据某个字符串连接
     *
     * @param texts
     * @param str
     */
    public static String concat(String[] texts, String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            String tmp = texts[i];
            sb.append(tmp);
            if (i < texts.length - 1) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    public static void v(String msg, String... tags) {
        if (LEVEL < 5) {
            return;
        }
        String tag = COMMON_TAG + "v_" + concat(tags, "_");
        Log.v(tag, msg);
    }

    public static void d(String msg, String... tags) {
        if (LEVEL < 4) {
            return;
        }
        String tag = COMMON_TAG + "d_" + concat(tags, "_");
        Log.d(tag, msg);
    }

    public static void i(String msg, String... tags) {
        if (LEVEL < 3) {
            return;
        }
        String tag = COMMON_TAG + "i_" + concat(tags, "_");
        Log.i(tag, msg);
    }

    public static void w(String msg, String... tags) {
        if (LEVEL < 2) {
            return;
        }
        String tag = COMMON_TAG + "w_" + concat(tags, "_");
        Log.w(tag, msg);
    }

    public static void e(String msg, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        String tag = COMMON_TAG + "e_" + concat(tags, "_");
        Log.e(tag, msg);
    }


    public static void out(String msg, String... tags) {
        if (LEVEL < 3) {
            return;
        }
        String tag = COMMON_TAG + concat(tags, "_");
        System.out.println(tag + "," + msg);
    }

    public static void err(String msg, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        String tag = COMMON_TAG + concat(tags, "_");
        System.err.println(tag + "," + msg);
    }
}
