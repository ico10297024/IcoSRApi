package com.sunruncn.ble;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 公共的工具类
 * 字节序为小段字节序（如int123456789 byte 3 2 1 0 分别对应12 34 56 78）
 */
public class Common {

    /**
     * 判断是否启用了download服务
     *
     * @param context
     * @return
     */
    public static boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 启用download服务
     *
     * @param context
     */
    public static void openDownload(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }


    /**
     * dip转换px
     *
     * @param context
     * @param dpValue
     * @return float
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    /**
     * px转换dip
     *
     * @param context
     * @param pxValue
     * @return float
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 获得手机的mac地址
     * 该方法在WIFI没有开启状态下也可以获取
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 判断用户触摸位置是否为控件所在的位置
     *
     * @param v  要判断的控件
     * @param ev 触摸事件的信息
     * @return
     */
    public static boolean isTouch(View v, MotionEvent ev) {
        int[] location = {0, 0};
        v.getLocationInWindow(location);
        if (ev.getRawX() > location[0] && ev.getRawX() < location[0] + v.getWidth() && ev.getRawY() > location[1] && ev.getRawY() < location[1] + v.getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前屏幕的开关状态
     *
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    /**
     * 获取当前手机屏幕的宽度，px值
     *
     * @param context
     * @return int
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取当前手机屏幕的高度，px值
     *
     * @param context
     * @return int
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取当前app的版本名称
     *
     * @param context
     * @return String
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    }

    /**
     * 获取当前app的版本代号
     *
     * @param context
     * @return int
     */
    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    }


    /**
     * 获取当前手机顶部状态栏的高度
     *
     * @param activity
     * @return int
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获取当前手机顶部状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取当前手机底部导航栏的高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 判断当前手机是否存在底部导航栏
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }


    /**
     * 生成一个小于等于X,大于0的值
     *
     * @param x
     * @return
     */
    public static int random(int x) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        return (random.nextInt(x) + 1);
    }

    /**
     * 判断程序是否在后台
     * 需要在onStop中
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        /*ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    log.w(String.format("当前程序在后台（%s）", appProcess.processName), Common.class.getSimpleName(), "isBackground");
                    return true;
                } else {
                    log.w(String.format("当前程序在前台（%s）", appProcess.processName), Common.class.getSimpleName(), "isBackground");
                    return false;
                }
            }
        }
        return false;*/
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                log.w("后台");
                return true;
            }
        }
//        log.w("前台");
        return false;
    }

    /**
     * 查询当前是否有网络，通过判断当前是否有连接数据网络或者wifi热点
     *
     * @param context
     * @return
     */
    public static boolean isNetEnable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        }
        return false;
    }

    //region **************************************************************************************************************** IO

    /**
     * 获取指定目录下的所有子目录
     *
     * @param dir
     * @return
     */
    public static List<File> getSubdir(File dir) {
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        return Arrays.asList(dir.listFiles(ff));
    }


    /**
     * 根据正则表达式获取符合条件的文件
     *
     * @param dir   要查找的目录
     * @param regex 要匹配的正则表达式
     * @param flag  是否匹配子目录下的文件
     * @return List<File>
     */
    public static List<String> getFiles(File dir, String regex, boolean flag) {
        List<String> files = new ArrayList<String>();
        File[] subFiles = dir.listFiles();
        for (File file : subFiles) {
            if (file.isFile() && getSuffix(file).toLowerCase().matches(regex)) {//文件
                files.add(file.getAbsolutePath());
            } else if (file.isDirectory() && flag) {//目录
                List<String> _files = Common.getFiles(file, regex, flag);
                files.addAll(_files);
            }
        }
        return files;
    }


    /**
     * 根据正则表达式获取符合条件的文件
     *
     * @param dirs  要查找的目录
     * @param regex 要匹配的正则表达式
     * @param flag  是否匹配子目录下的文件
     * @return List<File>
     */
    public static List<String> getFiles(List<File> dirs, String regex, boolean flag) {
        List<String> files = new ArrayList<String>();
        for (File dir : dirs) {
            files.addAll(Common.getFiles(dir, regex, flag));
        }
        return files;
    }

    /**
     * 获取文件的后缀名,带.
     *
     * @param file
     * @return String
     */
    public static String getSuffix(File file) {
        String suffix = "";
        if (file.getName().lastIndexOf(".") == -1) {
            return suffix;
        }
        suffix = file.getName().substring(file.getName().lastIndexOf("."));
        return suffix;
    }

    /**
     * 重命名文件名称
     *
     * @param file
     * @param newName
     * @return
     * @throws IOException
     */
    public static File renameFile(File file, String newName) throws IOException {
        if (!file.isFile()) {
            throw new IOException("file参数所表示的不是一个文件");
        }
        if (!file.exists()) {
            throw new IOException("file参数所表示的文件不存在");
        }
        newName = newName + Common.getSuffix(file);
        File newFile = new File(file.getParentFile(), newName);
        if (file.renameTo(newFile)) {
            return newFile;
        }
        return null;
    }

    /**
     * 拷贝文件
     *
     * @param file
     * @param newFile
     * @return
     */
    public static File copyFile(File file, File newFile) throws IOException {
        if (!file.isFile()) {
            throw new IOException("file参数所表示的不是一个文件");
        }
        if (!file.exists()) {
            throw new IOException("file参数所表示的文件不存在");
        }
        //确认新目录是否存在并且可用
        if ((!newFile.mkdirs())) {
            throw new IOException("新目录无法创建");
        }
        if (!newFile.getParentFile().canWrite()) {
            throw new IOException("新目录无法执行写入操作");
        }
        //创建新的文件
        newFile.delete();
        newFile.createNewFile();
        //创建输入输出流
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(newFile);
            while (true) {
                byte[] buffer = new byte[1024 * 1024 * 4];
                int len = fis.read(buffer);
                if (len == -1) {
                    break;
                }
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newFile;
    }

    /**
     * 移动文件
     *
     * @param file
     * @param _newFile
     * @return
     */
    public static File moveFile(File file, File _newFile) throws IOException {
        File newFile = copyFile(file, _newFile);
        if (newFile != null) {
            file.delete();
        }
        return newFile;
    }

    /**
     * 向指定文件中写入字符串
     *
     * @param file
     * @param text
     * @param isAppend
     */
    public static void writeFile(File file, String text, boolean isAppend) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file, isAppend);
            if (isAppend) {
                fw.write(text);
            } else {
                fw.write(text);
            }
            fw.flush();
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    /**
     * 向指定文件中写入字符串
     *
     * @param file
     * @param buffer
     */
    public static void writeFile(File file, byte[] buffer) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        } else {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                //获取名称
                String[] filename = file.getName().split("\\.");
                File tmp = new File(file.getParent(), filename[0] + "(" + i + ")." + filename[1]);
                if (!tmp.exists()) {
                    log.w("===" + tmp.getAbsolutePath());
                    tmp.createNewFile();
                    file = tmp;
                    return;
                }
            }
        }
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(buffer);
            out.flush();
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 读取文件
     *
     * @param file
     */
    public static List<Byte> readFile(File file) throws IOException {
        List<Byte> list = new ArrayList<Byte>();
        FileInputStream fileInputStream = new FileInputStream(file);
        while (true) {
            byte[] buffer = new byte[1024 * 4 * 4];
            int len = fileInputStream.read(buffer);
            if (len == -1) {
                break;
            }
            for (int i = 0; i < len; i++) {
                List<Byte> _list = Arrays.asList(buffer[i]);
                list.add(_list.get(0));
            }
        }
        return list;
    }

    /**
     * 读取assets中的文件文件,返回字节集合
     *
     * @param assetManager asset管理器
     * @param filename     文件名称
     * @return
     * @throws IOException
     */
    public static List<Byte> readFile(AssetManager assetManager, String filename) throws IOException {
        BufferedInputStream input = new BufferedInputStream(assetManager.open(filename));
        List<Byte> list = new ArrayList<>();
        while (true) {
            byte[] buffer = new byte[1024 * 4 * 4];
            int len = input.read(buffer);
            if (len == -1) {
                break;
            }
            for (int i = 0; i < len; i++) {
                List<Byte> _list = Arrays.asList(buffer[i]);
                list.add(_list.get(0));
            }
        }
        return list;
    }

    /**
     * 读取assets中的文件文件，根据encoding转化为字符，最后拼接返回
     *
     * @param assetManager asset管理器
     * @param filename     文件名称
     * @param encoding     编码方式
     * @return
     * @throws IOException
     */
    public static String readFile(AssetManager assetManager, String filename, String encoding) throws IOException {
        BufferedInputStream input = new BufferedInputStream(assetManager.open(filename));
        StringBuilder sb = new StringBuilder();
        while (true) {
            byte[] buffer = new byte[1024 * 4 * 4];
            int len = input.read(buffer);
            if (len == -1) {
                break;
            }
            sb.append(new String(buffer, 0, len, encoding));
        }
        return sb.toString();
    }
    //endregion


    //region  **************************************************************************************************************** 输入法

    /**
     * 如果输入法在窗口上已经显示，则隐藏，反之则显示
     *
     * @param context
     */
    public static void toggleSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制显示软键盘
     *
     * @param context
     * @param v
     */
    public static void showSoftInput(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制隐藏软键盘
     *
     * @param context
     * @param v
     */
    public static void hideSoftInput(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏系统默认的输入法
     *
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    //endregion

//    /**
//     * 获取输入法打开的状态
//     * @param context
//     * @return
//     */
//    public static boolean isSoftInputActive(Context context) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
//        return isOpen;
//    }

    //region  **************************************************************************************************************** 字节转换

    /**
     * 将数值类型转换为IP地址
     *
     * @param ip
     * @return String
     */
    public static String int2Ip(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }

    /**
     * 将 字节数组 转换为 字节集合
     *
     * @param bytes
     * @return
     */
    public static List<Byte> bytes2List(byte[] bytes) {
        List<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < bytes.length; i++) {
            List<Byte> _list = Arrays.asList(bytes[i]);
            list.add(_list.get(0));
        }
        return list;
    }

    /**
     * 将 字节集合 转换为 字节数组
     *
     * @param list
     * @return
     */
    public static byte[] list2Bytes(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    /**
     * 将 字节数组 追加到 字节集合 中
     *
     * @param list  字节集合
     * @param bytes 字节数组
     * @return
     */
    public static List<Byte> join(List<Byte> list, byte[] bytes) {
        list.addAll(bytes2List(bytes));
        return list;
    }

    /**
     * 将 字节数组 追加到 字节集合 的 指定位置 中
     *
     * @param list     字节集合
     * @param location 指定位置
     * @param bytes    字节数组
     * @return
     */
    public static List<Byte> join(List<Byte> list, int location, byte[] bytes) {
        list.addAll(location, bytes2List(bytes));
        return list;
    }

    /**
     * 将字节组转化为int类型
     *
     * @param buffer
     * @return
     */
    public static int bytes2Int(byte... buffer) {
        return (((buffer[3] & 0xFF) << 24) | ((buffer[2] & 0xFF) << 16) | ((buffer[1] & 0xFF) << 8) | ((buffer[0] & 0xFF)));
    }

    /**
     * 将int类型转化为字节组
     *
     * @param num
     * @return buffer
     */
    public static byte[] int2Bytes(int num) {
        //这个是我写的，上面是网上找的
        byte[] buffer = new byte[4];
        buffer[0] = (byte) (num & 0xFF);
        buffer[1] = (byte) (num >> 8 & 0xFF);
        buffer[2] = (byte) (num >> 16 & 0xFF);
        buffer[3] = (byte) (num >> 24 & 0xFF);
        return buffer;
    }

    /**
     * 将int类型转化为单字节
     *
     * @param num
     * @return
     */
    public static byte int2OneByte(int num) {
        return (byte) (num & 0x000000ff);
    }

    /**
     * 将单字节转化为16进制
     *
     * @param buffer
     * @return
     */
    public static String byte2Int16(byte buffer) {
        String str = Integer.toString(buffer & 0xFF, 16).toUpperCase();
        return str.length() == 1 ? 0 + str : str;
    }

    /**
     * 将一个字节数组转化为16进制然后通过连接符拼接在一起
     *
     * @param buffers 字符数组
     * @param joinStr 连接符
     * @return
     */
    public static String bytes2Int16(byte[] buffers, String joinStr) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < buffers.length; i++) {
            sb.append(byte2Int16(buffers[i]));
            if (i != buffers.length - 1) {
                sb.append(joinStr);
            }
        }

        return sb.toString();
    }

    /**
     * 将16进制字符串转换为byte
     *
     * @param hexStr
     * @return
     */
    public static byte hexstr2Byte(String hexStr) {
        char[] chars = hexStr.toUpperCase().toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) "0123456789ABCDEF".indexOf(chars[i]);
        }
        byte buffer = 0;
        if (bytes.length == 2) {
            buffer = (byte) (((bytes[0] << 4) & 0xf0) | (bytes[1]));
        } else {
            buffer = bytes[0];
        }
        return buffer;
    }

    /**
     * 将mac地址转换为字节组
     *
     * @param mac
     * @return
     */
    public static byte[] mac2Bytes(String mac) {
        String[] _mac = mac.split(":");
        byte[] buffer = new byte[_mac.length];
        for (int i = 0; i < _mac.length; i++) {
            buffer[i] = Common.hexstr2Byte(_mac[i]);
        }
        return buffer;
    }

    /**
     * 将字节组转换为mac地址
     *
     * @param bytes
     * @return
     */
    public static String bytes2Mac(byte... bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Common.byte2Int16(bytes[i]));
            if (i != bytes.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 将字符串的集合转化为字符串集合
     *
     * @param list
     * @return
     */
    public static String[] list2Array(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * 将字符串的集合转化为字符串集合
     *
     * @param list
     * @return
     */
    public static File[] list2FileArray(List<String> list) {
        File[] array = new File[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = new File(list.get(i));
        }
        return array;
    }

    /**
     * 将byte集合转换为字符串
     *
     * @param list
     * @return
     * @throws {@link UnsupportedEncodingException}
     */
    public static String bytes2Str(List<Byte> list) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[(list.size() > Integer.MAX_VALUE ? Integer.MAX_VALUE : list.size())];
        for (int i = 0, j = 0; i < list.size(); i++, j++) {
            buffer[j] = list.get(i);
            if (j == buffer.length - 1) {
                sb.append(new String(buffer, "UTF-8"));
                buffer = new byte[(list.size() > Integer.MAX_VALUE ? Integer.MAX_VALUE : list.size())];
                j = -1;
            }
        }
        String str = sb.toString();
        return str;
    }
    //endregion

    //region  *****************************************************************************************************************Intent

    /**
     * 意图-社会化分享
     *
     * @param context
     * @param type    分享发送的数据类型（text/plain）
     * @param subject 分享的主题
     * @param content 分享的内容
     * @return
     */
    public static Intent getIntentByShare(Context context, String type, String subject, String content, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
        intent.setType(type); // 分享发送的数据类型
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 分享的主题
        intent.putExtra(Intent.EXTRA_TEXT, content); // 分享的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return Intent.createChooser(intent, title);
    }

    /**
     * 意图-社会化分享
     *
     * @param context
     * @param type    分享发送的数据类型（text/plain）
     * @param subject 分享的主题
     * @param uri     分享的地址（Uri.create("url")）
     * @return
     */
    public static Intent getIntentByShare(Context context, String type, String subject, URI uri, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
        intent.setType(type); // 分享发送的数据类型
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 分享的主题
        intent.putExtra(Intent.EXTRA_STREAM, uri); // 分享的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return Intent.createChooser(intent, title);
    }

    /**
     * 意图-从拍照获取图片
     *
     * @param file 存储路径，若文件夹无法创建则会报该错误
     * @return
     */
    public static Intent getIntentByCamera(File file) throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断输出目录有效性
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("无法创建文件夹，file：" + file.getAbsolutePath());
            }
        }
        //存入参数
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        return intent;
    }

    /**
     * 意图-从相册获取图片
     * data.getData()
     *
     * @return
     */
    public static Intent getIntentByPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    /**
     * 意图-使用系统程序查看图片
     *
     * @param file 图片文件
     * @return
     */
    public static Intent getIntentByPhoto(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        return intent;
    }

    /**
     * 意图-使用系统程序查看图片
     *
     * @param uri 图片文件
     * @return
     */
    public static Intent getIntentByPhoto(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    /**
     * 意图-裁剪图片的意图
     *
     * @param photo      需要裁剪的图片位置
     * @param outputFile 图片输出位置，不一定有用，根据用户采用的裁剪程序有关，如miuiv6自带程序可以保存，快图浏览无法保存，所以在接收后需要判断下，若没有需要另外自行保存
     * @param aspectX    宽高比例中的宽度
     * @param aspectY    宽高比例中的高度
     * @param outputX    输出图片的宽度，需和高度一起设置，设置后裁剪会很快，否则程序会一直卡在裁剪操作中
     * @param outputY    输出图片的高度，需和宽度一起设置，设置后裁剪会很快，否则程序会一直卡在裁剪操作中
     * @return
     * @throws IOException 创建输出路径文件夹，若无法创建则抛出该异常
     */
    public static Intent getIntentByCrop(File photo, File outputFile, Integer aspectX, Integer aspectY, Integer outputX, Integer outputY) throws IOException {
        //某些手机中不会自动创建文件夹，所以需要手动创建文件夹
        if ((!outputFile.getParentFile().exists()) && (!outputFile.mkdirs())) {
            throw new IOException("无法创建文件夹！");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(photo), "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
//        intent.putExtra("scale", true);
        //设置宽高比例
        if (aspectX != null && aspectY != null) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }
        //设置裁剪图片宽高
        if (outputX != null && outputY != null) {
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
        }
//        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        //设置输出路径，设置了无效
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        return intent;
    }

    /**
     * 意图-裁剪图片
     *
     * @param photo      需要裁剪的图片位置
     * @param outputFile 图片输出位置，不一定有用，根据用户采用的裁剪程序有关，如miuiv6自带程序可以保存，快图浏览无法保存，所以在接收后需要判断下，若没有需要另外自行保存
     * @param aspectX    宽高比例中的宽度
     * @param aspectY    宽高比例中的高度
     * @return
     * @throws IOException 创建输出路径文件夹，若无法创建则抛出该异常
     */
    public static Intent getIntentByCrop(File photo, File outputFile, int aspectX, int aspectY) throws IOException {
        return getIntentByCrop(photo, outputFile, aspectX, aspectY, null, null);
    }

    /**
     * 意图-裁剪图片
     * 默认以1:1比例，500*500进行裁剪
     *
     * @param photo      需要裁剪的图片位置
     * @param outputFile 图片输出位置，不一定有用，根据用户采用的裁剪程序有关，如miuiv6自带程序可以保存，快图浏览无法保存，所以在接收后需要判断下，若没有需要另外自行保存
     * @return
     * @throws IOException 创建输出路径文件夹，若无法创建则抛出该异常
     */
    public static Intent getIntentByCrop(File photo, File outputFile) throws IOException {
        return Common.getIntentByCrop(photo, outputFile, 1, 1, 500, 500);
    }

    /**
     * 意图-跳转通讯录
     *
     * @return
     */
    public static Intent getIntentByContact() {
        //不加第二个参数有些手机不会跳转至通讯录，如大神F1，显示sim卡应用
        //返回的Uri为硬编码，加上 ContactsContract.Contacts.CONTENT_URI 为了增加移植性
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    /**
     * 意图-跳转短信发送
     *
     * @param phone   电话号码
     * @param message 信息
     * @return
     */
    public static Intent getIntentBySms(String phone, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
        intent.putExtra("sms_body", message);
        return intent;
    }

    /**
     * 意图-跳转安装apk
     *
     * @param context
     * @param uri     Uri.parse(String)
     * @return
     */
    public static Intent getIntentByInstall(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 意图-调起本地浏览器
     *
     * @param url
     * @return
     */
    public static Intent getIntentByBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
//        intent.setClassName("com.android.browser",
//                "com.android.browser.BrowserActivity");
        return intent;
    }

    /**
     * 意图-直接拨打电话
     *
     * @param phone
     * @return Intent
     */
    public static Intent getIntentByCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        return intent;
    }

    /**
     * 意图--跳转拨打电话的界面，自动填入电话号码
     *
     * @param phone
     * @return Intent
     */
    public static Intent getIntentByDial(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        return intent;
    }

    /**
     * 处理从 拍照 获取图片的结果
     * 跳转拍照时候没有传入存储路径时需要使用此方法将图片进行转存
     *
     * @param data 结果
     * @param file 指定要保存到的位置
     * @return boolean 该值表示已经获取到了图片，然后将该图片保存到 file 参数指定的位置 成功或者失败
     * @throws IOException          无法创建文件
     * @throws NullPointerException 若无法获取到图片将报该错误
     */
    public static boolean handleResultForCamera(Intent data, File file) throws IOException, NullPointerException {
        Bitmap photo = null;
        Uri uri = data.getData();
        if (uri != null) {
            photo = BitmapFactory.decodeFile(uri.getPath());
        }
        if (photo == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                photo = (Bitmap) bundle.get("data");
            }
        }
        if (photo == null) {
            throw new NullPointerException("无法获取到拍照的图片");
        }
        return Common.saveBitmap(photo, file);
    }

    /**
     * 处理从 相册 获取图片的结果
     *
     * @param data
     * @param activity
     * @return
     * @throws OutOfMemoryError 小米4相册程序会显示云相册中的照片，用户如果选择了则会报该异常
     */
    public static String handleResultForPhoto(Intent data, Activity activity) {
        return Common.getPath(data.getData(), activity);
    }

    /**
     * 处理图片进行裁剪后的结果
     *
     * @param data  Intent类型的数据
     * @param photo 输入裁剪后图片的位置，某些机型会自动保存图片，所以根据是否有结果以及本地是否有图片进行判断
     * @return boolean 是否存在错误，立即结束代码
     */
    public static boolean handleResultForCrop(Intent data, File photo, Activity activity) {
        Bitmap bitmap = data.getParcelableExtra("data");
        if (bitmap == null && (!photo.exists())) {
            Toast.makeText(activity, "无法保存裁剪后的图片，请自行裁剪后设置!", Toast.LENGTH_LONG).show();
            return true;
        } else if (bitmap != null) {
            try {
                Common.saveBitmap(bitmap, photo);
            } catch (Exception e) {
                //                    e.printStackTrace();
                log.ee("裁剪图片后保存图片异常，Exception：" + e.toString(), activity.getClass().getSimpleName(), "onActivityResult");
                Toast.makeText(activity, "无法保存图片，请检查SD卡!", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        if (bitmap != null) {
            //回收
            bitmap.recycle();
            System.gc();
        }
        return false;
    }

    /**
     * 从通讯录获取电话号码，对应{@link Common#getIntentByContact()}
     *
     * @param data
     * @return String
     */
    public static String handleResultForContact(Intent data, Activity activity) {
        Cursor cursor = null;
        Cursor phonesCursor = null;

        try {
            Uri uri = data.getData();
            cursor = activity.managedQuery(uri, null, null, null, null);
            if (!cursor.moveToNext()) {
                return null;
            }
            //判断用户选择的是否有电话号码
            int phoneNum = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (phoneNum <= 0) {
                return null;
            }
            // 获得联系人的ID号
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            // 获得联系人的电话号码的cursor;
            phonesCursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            // 遍历所有的电话号码
            while (phonesCursor.moveToNext()) {
                int phone_type = phonesCursor.getInt(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (phone_type == 2) {
                    return phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            log.ee("获取用户选择的联系人号码时异常，Excepiton：" + e.toString(), activity.getClass().getSimpleName(), "handleResultForContact");
        } finally {
            //SDK大于14，cursor会自动关闭
//            if (mApp.versionCode < 14) {
            if (cursor != null) {
                cursor.close();
            }
            if (phonesCursor != null) {
                phonesCursor.close();
            }
//            }
        }

        return null;
    }
    //endregion

    //region  *****************************************************************************************************************图片图形

    /**
     * 将uri进行解析，返回一直字符串形式的路径
     *
     * @param uri
     * @param activity
     * @return String
     */
    public static String getPath(Uri uri, Activity activity) {
        // 得到bitmap图片
//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        // 这里开始的第二部分，获取图片的路径：
        String[] proj = {MediaStore.Images.Media.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        return cursor.getString(column_index);
    }

    /**
     * 获取URI中保存的图片，并以指定的宽高进行压缩后返回
     *
     * @param uri
     * @param activity
     * @param image_maxWidth
     * @param image_maxHeight
     * @return
     * @throws OutOfMemoryError 小米4相册程序会显示云相册中的照片，用户如果选择了则会报该异常
     */
    public static Bitmap getBitmap(Uri uri, Activity activity, int image_maxWidth, int image_maxHeight) throws OutOfMemoryError {
        String path = getPath(uri, activity);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getBitmap(path, image_maxWidth, image_maxHeight);
    }

    /**
     * 将指定路径的图片按原图直接返回
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmap(String path) {
        /* 获得图片的宽高*/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 将指定路径的图片，按照指定的宽高进行等比例压缩后返回
     *
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap getBitmap(String path, float maxWidth, float maxHeight) {
        Bitmap bitmap = null;
        /* 获得图片的宽高*/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //缩小比例
        float rate = options.outWidth / maxWidth > options.outHeight / maxHeight ? options.outWidth / maxWidth : options.outHeight / maxHeight;
        if (rate < 1f) {
            rate = 1f;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) rate;

        //options.inPreferredConfig =Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        options.inPurgeable = true;// 同时设置才会有效
        options.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = compression(bitmap, maxWidth, maxHeight);
        return bitmap;
    }

    /**
     * 根据要求的宽/高将图片等比例压缩在返回
     *
     * @param bitmap
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap compression(Bitmap bitmap, float maxWidth, float maxHeight) {
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float widthRate = maxWidth / width;
        float heightRate = maxHeight / height;
        float rate;
        if (widthRate < 1 && heightRate < 1) {
            rate = widthRate < heightRate ? widthRate : heightRate;
        } else if (widthRate < 1 && heightRate >= 1) {
            rate = widthRate;
        } else if (widthRate >= 1 && heightRate < 1) {
            rate = heightRate;
        } else {
            return bitmap;
        }
        log.w(String.format("本次压缩最大宽/高/比例：%f/%f/%f", maxWidth, maxHeight, rate));
        return Common.compression(bitmap, rate);
//            // 创建操作图片用的matrix对象
//            Matrix matrix = new Matrix();
//            float scaleWidth = ((float) newWidth) / width;
//            float scaleHeight = ((float) newHeight) / height;
//            // 缩放图片动作
//            matrix.postScale(scaleWidth, scaleHeight);
//            // 创建新的图片
//            reBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 根据要求的比例将图片等比例压缩在返回
     *
     * @param bitmap
     * @param rate   该值必须大于0小于1
     * @return
     */
    public static Bitmap compression(Bitmap bitmap, float rate) {
        // 创建一个用于返回的bitmap副本
        Bitmap reBitmap = null;
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float newWidth = width * rate;
        float newHeight = height * rate;
        reBitmap = Bitmap.createScaledBitmap(bitmap, (int) newWidth, (int) newHeight, true);
        log.w("图片原宽/高:" + width + "/" + height, Common.class.getSimpleName(), "compression");
        log.w("图片新宽/高:" + newWidth + "/" + newHeight, Common.class.getSimpleName(), "compression");
        bitmap.recycle();
        System.gc();
        return reBitmap;
    }

    /**
     * 将指定图片,压缩至指定大小或更小
     * 若压缩成功则返回新文件的File对象
     * 若压缩失败则返回null
     * <p>
     * 注：通过文件大小的比例来作为压缩图片分辨率大小的比例，此为正比，但正比比例小于1
     * 所以该方法原理是根据限定长度，等比例压缩图片至长/宽等于小于限定长度，然后计算文件大小，若偏大则将限定长度减少再次压缩，直至符合条件
     *
     * @param filePath
     * @param length
     * @param maxWidth
     * @param maxHeight
     * @param step
     * @param newFilePath
     * @return
     * @throws IOException
     */
    public static File getImageFile(String filePath, long length, float maxWidth, float maxHeight, int step, String newFilePath) throws IOException {
        //根据限定大小获取压缩后的图片
        Bitmap bitmap = getBitmap(filePath, maxWidth, maxHeight);
        //保存压缩的图片
        File newFile = new File(newFilePath);
        boolean flag = Common.saveBitmap(bitmap, newFile);
        bitmap.recycle();
        System.gc();
        if (!flag) {
            return null;
        }
        if (Common.isBigThan(newFilePath, length, maxWidth, maxHeight)) {
            maxWidth -= step;
            maxHeight -= step;
            if (maxWidth <= 0 || maxHeight <= 0) {
                return null;
            }
            return getImageFile(filePath, length, maxWidth, maxHeight, step, newFilePath);
        } else {
            return newFile;
        }
    }

    /**
     * 计算给定的大小和文件大小的比例
     *
     * @param length   给定大小
     * @param filePath 文件大小
     * @return
     * @throws IOException
     */
    public static float calcRate(long length, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.isFile() || !file.exists()) {
            throw new IOException("file不是一个文件，或不存在");
        }
        //获取比例
        return ((float) length / file.length());
    }

    /**
     * 检查图片分辨率的宽/高是否超过了限定宽高
     * 若有一个边超过则返回true，反之false
     *
     * @param filePath
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static boolean isBigThan(String filePath, float maxWidth, float maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;
        if (imgWidth > maxWidth || imgHeight > maxHeight) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查图片文件的文件大小和分辨率大小是否超过了限定
     *
     * @param filePath  图片文件的绝对路径
     * @param length    文件大小
     * @param maxWidth  限定宽度
     * @param maxHeight 限定高度
     * @return
     * @throws IOException
     */
    public static boolean isBigThan(String filePath, long length, float maxWidth, float maxHeight) throws IOException {
        //获取比例
        float rate = Common.calcRate(length, filePath);
        //是否过大
        boolean isBigThan = Common.isBigThan(filePath, maxWidth, maxHeight);
        //若不需要压缩直接返回原图
        if (rate >= 1 && !isBigThan) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 将一个bitmap对象保存至指定文件中
     *
     * @param bitmap
     * @param file
     * @return
     * @throws IOException 文件创建失败
     */
    public static boolean saveBitmap(Bitmap bitmap, File file) throws IOException {
        file.getParentFile().mkdirs();
        file.delete();
        file.createNewFile();
        return bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
    }


    /**
     * 把View绘制到Bitmap上
     *
     * @param comBitmap 需要绘制的View
     * @param width     该View的宽度
     * @param height    该View的高度
     * @return 返回Bitmap对象
     * add by csj 13-11-6
     */
    public static Bitmap getViewBitmap(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);
            comBitmap.setDrawingCacheEnabled(true);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            comBitmap.layout(0, 0, width, height);

//            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
                        new RuntimeException());
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }

    /**
     * 将一张图片，以最小边为直径，裁剪成正圆的图片然后返回
     *
     * @param _bitmap
     * @return
     */
    public static Bitmap cropCircle(Bitmap _bitmap) {
        int w = _bitmap.getWidth();
        int h = _bitmap.getHeight();
        //计算出正方形的区域
        int size = Math.min(w, h);

        //创建一个空白的图片
        Bitmap bitmap = Bitmap.createBitmap(size, size, _bitmap.getConfig());
        //获取要绘制这个图片的画布
        Canvas canvas = new Canvas(bitmap);
        //获取画笔
        Paint paint = new Paint();
        //设置画笔颜色，设置画笔颜色为完全透明
//                                    paint.setARGB(0, 0, 0, 0);
        //设置位图渲染,设置XY渲染模式为复制最后一个像素点对超出部分着色
        BitmapShader bitmapShader = new BitmapShader(_bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置矩阵变换,更改渲染的原点位置
        Matrix matrix = new Matrix();
        matrix.setTranslate(-(w - size) / 2, -(h - size) / 2);
        bitmapShader.setLocalMatrix(matrix);
        //设置画笔的渲染
        paint.setShader(bitmapShader);
        paint.setAntiAlias(true);

        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        return bitmap;
    }

    //endregion

    //region *****************************************************************************************************************数据拼接处理

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

    /**
     * 将一个字符串数组根据某个字符串连接
     *
     * @param texts
     * @param str
     */
    public static String concat(List<String> texts, String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.size(); i++) {
            String tmp = texts.get(i);
            sb.append(tmp);
            if (i < texts.size() - 1) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 将多个字节数组进行拼接
     *
     * @param buffer
     * @return
     */
    public static byte[] fit(byte[]... buffer) {
        //计算数据总长度
        int length = 0;
        for (int i = 0; i < buffer.length; i++) {
            length += buffer[i].length;
        }
        byte[] datas = new byte[length];
        //依次进行copy
        int sp = 0;//起始存放位置
        for (int i = 0; i < buffer.length; i++) {
            System.arraycopy(buffer[i], 0, datas, sp, buffer[i].length);
            sp += buffer[i].length;
        }
        return datas;
    }

    /**
     * 每隔几个字符插入一个指定字符
     *
     * @param s        原字符串
     * @param iStr     要插入的字符串
     * @param interval 间隔时间
     * @return
     */
    public static String insert(String s, String iStr, int interval) {
        StringBuffer s1 = new StringBuffer(s);
        int index;
        for (index = interval; index < s1.length(); index += (interval + 1)) {
            s1.insert(index, iStr);
        }
        return s1.toString();
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {

        log.w("delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            log.e("delete file no exists " + file.getAbsolutePath());
        }
    }
    //endregion
}
