package com.sunruncn.lib.mqtt;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/5/16.
 */

public class MqttData {
    // 1查询状态 2开 3关 6渐变开 7渐变关 8定时查询
    public static final int CMD_QUERY = 1;
    public static final int CMD_OPEN = 2;
    public static final int CMD_CLOSE = 3;
    private static final byte VERSION = 0x01;   //版本号
    private static final int PROJECT_ID = 1;    //项目id

    static {
        System.loadLibrary("native-lib");
    }

    private static native byte[] GetHead(byte devType, byte[] userId, byte[] serial, int length, byte[] time);

    /**
     * 获取数据头
     *
     * @param devType 设备类型
     * @param userId  用户id
     * @param serial  序列号
     * @param length  数据长度
     * @return
     */
    public static byte[] getHead(byte devType, int userId, String serial, int length) {
        //用户ID
        byte[] userIds = Common.int2Bytes(userId);
        //设备序列号
        byte[] macBytes = Common.hexstr2Bytes(Common.insert(serial, ":", 2));
        //年
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String str = Integer.toHexString(year);
        if (str.length() % 2 != 0) str = "0" + str;
        byte[] yearBytes = Common.hexstr2Bytes(Common.insert(str, ":", 2));
        byte[] time = new byte[8];
        time[0] = yearBytes[1];
        time[1] = yearBytes[0];
        time[2] = (byte) (calendar.get(Calendar.MONTH));
        time[3] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        time[4] = (byte) (calendar.get(Calendar.DAY_OF_WEEK));
        time[5] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        time[6] = (byte) (calendar.get(Calendar.MINUTE));
        time[7] = (byte) calendar.get(Calendar.SECOND);
        return GetHead(devType, userIds, macBytes, length, time);
    }

    /**
     * 控制开关、查询状态
     *
     * @param devType
     * @param userId
     * @param serial
     * @param controlType 1查询状态 2开 3关 6渐变开 7渐变关 8定时查询
     * @return
     */
    public static byte[] simpleControl(int devType, int userId, String serial, int controlType) {
        byte[] data = new byte[25];
        byte[] head = getHead((byte) devType, userId, serial, data.length);
        System.arraycopy(head, 0, data, 0, head.length);
        data[24] = (byte) controlType;
        return data;
    }
}
