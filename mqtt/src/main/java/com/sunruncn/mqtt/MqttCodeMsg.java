package com.sunruncn.mqtt;

/**
 * Created by Administrator on 2017/1/6.
 */

public class MqttCodeMsg {
    public static String getErrorMsg(int errorCode) {
        String erroeMsg;
        switch (errorCode) {
            case 0:
                erroeMsg = "执行成功";
                break;
            case 1:
                erroeMsg = "消息指令非法";
                break;
            case 2:
                erroeMsg = "消息格式不对";
                break;
            case 3:
                erroeMsg = "设备类型错误";
                break;
            case 4:
                erroeMsg = "协议版本不正确";
                break;
            case 5:
                erroeMsg = "设备标示符不一致";
                break;
            case 6:
                erroeMsg = "控制数据非法";
                break;
            default:
                erroeMsg = "操作异常";
                break;
        }
        return erroeMsg;
    }
}
