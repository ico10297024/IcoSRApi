package com.sunruncn.lib.mqtt;

/**
 * Created by ICO on 2017/3/21 0021.
 */

import android.content.Context;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 使用该类来对BleDeviceMgr进行下达指令，在开发中可以着重关注在功能上
 */
public class MqttSwtichMgr {

    public static final int DEVICE_TYPE = 50;
    //超时时间
    public static final long TIMESOUT = 15 * 1000L;
    //    public static String TOPIC_CLOSE = "SR/switch/dev/%s/close";
//    public static String TOPIC_PUSH = "SR/switch/dev/%s/push";
//    public static String TOPIC_CTRL = "SR/switch/dev/%s/ctrl";
    public static String TOPIC_CLOSE = "JK/market/dev/%s/close";
    public static String TOPIC_PUSH = "JK/market/dev/%s/push";
    public static String TOPIC_CTRL = "JK/market/dev/%s/ctrl";
    public AboutState aboutState;
    protected String mProject;
    protected String mHost;
    protected int mUserId;
    private Context mContext;
    //蓝牙连接
    private MqttSocket mqttSocket;
    private MqttSwitchCallback mMqttSwitchCallback;

    public MqttSwtichMgr(Context context, String mHost, String mProject, int mUserId, MqttSwitchCallback mqttSwitchCallback) {
        this.mContext = context;
        this.mHost = mHost;
        this.mProject = mProject;
        this.mUserId = mUserId;
        this.mMqttSwitchCallback = mqttSwitchCallback;
        this.mqttSocket = new MqttSocket(mHost, mProject + Common.getLocalMacAddress(mContext), mqttSwitchCallback);
        this.aboutState = new AboutState();
        mqttSwitchCallback.setMqttSwtichMgr(this);
    }

    public byte analyze(byte[] buffer) {
        if (buffer.length < 25) {
            return -1;
        }
        switch (buffer[24]) {
            case MqttData.CMD_QUERY:
                setQuerying(false);
                break;
            case MqttData.CMD_OPEN:
            case MqttData.CMD_CLOSE:
                setOnoffing(false);
                break;
        }
        return buffer[24];
    }

    public void subscribe(String... serial) throws MqttException {
        //订阅消息
        int[] qos = new int[serial.length * 2];
        String[] topic1 = new String[serial.length * 2];
        for (int i = 0; i < serial.length; i++) {
            qos[i * 2] = 2;
            qos[i * 2 + 1] = 2;
            topic1[i * 2] = String.format(TOPIC_CLOSE, serial[i]);
            topic1[i * 2 + 1] = String.format(TOPIC_PUSH, serial[i]);
        }
        mqttSocket.getMqttClient().subscribe(topic1, qos);
        log.w(String.format("MQTT通道订立成功,topic:%s", Arrays.asList(topic1)), MqttSocket.class.getSimpleName());
    }

    public void connect() {
        mqttSocket.connect();
    }

    public void close() {
        mqttSocket.close();
    }

    public boolean isClosed() {
        return mqttSocket.isClosed();
    }

    //region control
    public void query(TimerTask timerTask, String serial) {
        if (aboutState.queryTimer != null) {
            aboutState.queryTimer.cancel();
            aboutState.queryTimer = null;
        }
        if (timerTask != null) {
            aboutState.queryTimer = new Timer();
            aboutState.queryTimer.schedule(timerTask, TIMESOUT);
        }
        setQuerying(true);

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        byte[] data = MqttData.simpleControl(DEVICE_TYPE, mUserId, serial, MqttData.CMD_QUERY);
        mqttMessage.setPayload(data);
        mqttSocket.send(new MqttMessageIco(String.format(TOPIC_CTRL, serial), mqttMessage));
    }

    /**
     * 设置设备开关状态
     */
    public void onoff(TimerTask timerTask, String serial, boolean onoff) {
        if (aboutState.onoffTimer != null) {
            aboutState.onoffTimer.cancel();
            aboutState.onoffTimer = null;
        }
        if (timerTask != null) {
            aboutState.onoffTimer = new Timer();
            aboutState.onoffTimer.schedule(timerTask, TIMESOUT);
        }
        setOnoffing(true);

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        byte[] data = MqttData.simpleControl(DEVICE_TYPE, mUserId, serial, onoff ? MqttData.CMD_OPEN : MqttData.CMD_CLOSE);
        mqttMessage.setPayload(data);
        mqttSocket.send(new MqttMessageIco(String.format(TOPIC_CTRL, serial), mqttMessage));
    }
    //endregion

    //region GETSET
    public boolean isOpering() {
        return isQuerying() || isOnoffing();
    }

    public void finishOper() {
        setOnoffing(false);
        setQuerying(false);
    }

    public boolean isQuerying() {
        return aboutState.querying;
    }

    public MqttSwtichMgr setQuerying(boolean querying) {
        aboutState.querying = querying;
        if (!querying && aboutState.queryTimer != null) {
            aboutState.queryTimer.cancel();
            aboutState.queryTimer = null;
        }
        return this;
    }

    public boolean isOnoffing() {
        return aboutState.onoffing;
    }

    public MqttSwtichMgr setOnoffing(boolean onoffing) {
        aboutState.onoffing = onoffing;
        if (!onoffing && aboutState.onoffTimer != null) {
            aboutState.onoffTimer.cancel();
            aboutState.onoffTimer = null;
        }
        return this;
    }
    //endregion

    /**
     * 用来保存设备状态的,包括开关状态,当前操作状态
     */
    public class AboutState {
        public boolean onoff = false;
        public boolean querying = false, onoffing = false;
        public Timer queryTimer, onoffTimer;
    }
}
