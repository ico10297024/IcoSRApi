package com.sunruncn.lib.mqtt;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 公共的工具类
 * 字节序为大段字节序（如int123456789 byte 0 1 2 3 分别对应12 34 56 78）
 */
public class Common {


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
        return (((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | ((buffer[3] & 0xFF)));
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
        buffer[3] = (byte) (num & 0xFF);
        buffer[2] = (byte) (num >> 8 & 0xFF);
        buffer[1] = (byte) (num >> 16 & 0xFF);
        buffer[0] = (byte) (num >> 24 & 0xFF);
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
     * 将一个16进制的字符串转化成一个字节数组
     * 字符串以16进制字节为一个单位以冒号分隔
     * 如 AA:BB:CC:DD...
     * 分隔符可以通过{@link Common#insert(String, String, int)}进行插入
     *
     * @param mac 会对格式进行检查
     *            如果只有一位,左边加0,变两位,如 A->0A
     * @return
     */
    public static byte[] hexstr2Bytes(String mac) throws IllegalArgumentException {
        mac = mac.toUpperCase();
        //检查数据中是否存在16进制以外的数字
        Pattern pattern = Pattern.compile("[GHIJKLMNOPQRSTUVWXYZ]+");
        if (pattern.matcher(mac).find()) {
            throw new IllegalArgumentException("mac中存在16进制以外的英文,mac=" + mac);
        }
        if (mac.length() != 2) {
            if (!mac.matches("([0123456789ABCDEF]{2}[:]{1})+[0123456789ABCDEF]{2}")) {
                throw new IllegalArgumentException("输入参数mac格式为AA:BB:CC...,如果是一个字节的16进制字符串,请使用hexstr2Byte,mac=" + mac);
            }
        }
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
