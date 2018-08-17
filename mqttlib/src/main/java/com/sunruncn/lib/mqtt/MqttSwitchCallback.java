package com.sunruncn.lib.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by root on 17-11-8.
 */

public abstract class MqttSwitchCallback implements MqttCallback {
    MqttSwtichMgr mqttSwtichMgr;

    public void setMqttSwtichMgr(MqttSwtichMgr mqttSwtichMgr) {
        this.mqttSwtichMgr = mqttSwtichMgr;
    }


    public abstract void queryFinish(MqttSwtichMgr mqttSwtichMgr, boolean isSuccess, boolean isOpen);

    public abstract void onoffFinish(MqttSwtichMgr mqttSwtichMgr, boolean isSuccess, boolean isOpen);

    public abstract void unknowError(MqttSwtichMgr mqttSwtichMgr, byte data);

    public abstract void connectFail(MqttSwtichMgr mqttSwtichMgr, Exception e);

    public abstract void connectSuccess(MqttSwtichMgr mqttSwtichMgr, boolean b, String s);

    public abstract void sendSuccess(MqttSwtichMgr mqttSwtichMgr, IMqttDeliveryToken iMqttDeliveryToken);

    public abstract void connectDisconnect(MqttSwtichMgr mqttSwtichMgr, Throwable throwable);

    //region 原生函数
    @Override
    public final void receive(MqttSocket mqttSocket, String s, MqttMessage mqttMessage) {
        final byte[] data = mqttMessage.getPayload();
        byte cmd = mqttSwtichMgr.analyze(data);
        if (cmd < 0) {
            return;
        }
        switch (cmd) {
            case MqttData.CMD_QUERY:
                queryFinish(mqttSwtichMgr, true, data[25] == 1);
                break;
            case MqttData.CMD_OPEN:
            case MqttData.CMD_CLOSE:
                onoffFinish(mqttSwtichMgr, true, data[25] == 1);
                break;
            case 14:
                unknowError(mqttSwtichMgr, data[25]);
                break;
        }
    }

    @Override
    public final void sendFail(MqttSocket mqttSocket, MqttMessageIco mqttMessageIco, Exception e) {
        final byte[] data = mqttMessageIco.getMqttMessage().getPayload();
        byte cmd = mqttSwtichMgr.analyze(data);
        if (cmd < 0) {
            return;
        }
        switch (cmd) {
            case MqttData.CMD_QUERY:
                queryFinish(mqttSwtichMgr, false, false);
                break;
            case MqttData.CMD_OPEN:
            case MqttData.CMD_CLOSE:
                onoffFinish(mqttSwtichMgr, false, false);
                break;
        }
    }

    @Override
    public final void connectFail(MqttSocket mqttSocket, Exception e) {
        connectFail(mqttSwtichMgr, e);
    }

    @Override
    public final void connectSuccess(MqttSocket mqttSocket, boolean b, String s) {
        connectSuccess(mqttSwtichMgr, b, s);
    }

    @Override
    public final void sendSuccess(MqttSocket mqttSocket, IMqttDeliveryToken iMqttDeliveryToken) {
        sendSuccess(mqttSwtichMgr, iMqttDeliveryToken);
    }


    @Override
    public final void connectDisconnect(MqttSocket mqttSocket, Throwable throwable) {
        connectDisconnect(mqttSwtichMgr, throwable);
    }
    //endregion


}
