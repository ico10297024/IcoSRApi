package com.sunruncn.ble;

/**
 * Created by root on 17-6-13.
 */
class Protocal {
    //这一段必须要写！意思是加载这个native-lib里的函数和东西
    static {
        System.loadLibrary("native-lib");
    }
    //调用的时候就这么调就好了，这就是在native-lib里的函数

    static native byte[] getQueryI();

    static native byte[] getOpenI();

    static native byte[] getResetI();

    static native byte[] getConfigI();

    static native byte analyze(byte[] buffer);

    static native String getWriteUUID();

    static native String getReadUUID();
//    /**
//     * 响应数据-F5F50104XXSS0D0A		// XX 为电量值 SS 为校验
//     *
//     * @return
//     */
//    static byte[] getQueryI() {
//        //校验
//        byte[] bytes = new byte[8];
//        bytes[0] = (byte) 0xf5;
//        bytes[1] = (byte) 0xf5;
//        //设备类型
//        bytes[2] = 0x01;
//        //命令体
//        bytes[3] = 0x04;
//        //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
//        bytes[4] = 0x00;
//        bytes[5] = 0x00;
//        //结束
//        bytes[6] = 0x0d;
//        bytes[7] = 0x0a;
//        return bytes;
//    }
//
//    static byte[] getOpenI() {
//        //校验
//        byte[] bytes = new byte[8];
//        bytes[0] = (byte) 0xf5;
//        bytes[1] = (byte) 0xf5;
//        //设备类型
//        bytes[2] = 0x01;
//        //命令体
//        bytes[3] = 0x01;
//        //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
//        bytes[4] = 0x00;
//        bytes[5] = 0x00;
//        //结束
//        bytes[6] = 0x0d;
//        bytes[7] = 0x0a;
//        return bytes;
//    }
//
//    static byte[] getConfigI() {
//        //校验
//        byte[] bytes = new byte[8];
//        bytes[0] = (byte) 0xf5;
//        bytes[1] = (byte) 0xf5;
//        //设备类型
//        bytes[2] = 0x01;
//        //命令体
//        bytes[3] = 0x03;
//        //校验  bytes[0]|bytes[1]|bytes[2]|bytes[3]
//        bytes[4] = 0x00;
//        bytes[5] = 0x00;
//        //结束
//        bytes[6] = 0x0d;
//        bytes[7] = 0x0a;
//        return bytes;
//    }
//
//    static byte analyze(byte[] buffer) {
//        if (buffer.length < 8) {
//            return -1;
//        }
//        if ((buffer[0] & 0xff) == 0xf5
//                && (buffer[1] & 0xff) == 0xf5
//                && (buffer[6] & 0xff) == 0x0d
//                && (buffer[7] & 0xff) == 0x0a
//                ) {
//            return buffer[3];
//        } else {
//            return -1;
//        }
//    }
}
