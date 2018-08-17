package com.sunruncn.ble;

/**
 * Created by ICO on 2017/3/21 0021.
 */

import android.bluetooth.BluetoothDevice;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * 使用该类来对BleDeviceMgr进行下达指令，在开发中可以着重关注在功能上
 */
public class DMBleMgr {
    /**
     * 命令类型
     * <p>
     * 开
     */
    public static final byte CMD_OPEN = 0x01;
    /**
     * 命令类型
     * <p>
     * 快速重启
     */
    public static final byte CMD_RESET = 0x02;
    /**
     * 命令类型
     * <p>
     * 配置wifi进行配置状态
     */
    public static final byte CMD_CONFIG = 0x03;
    /**
     * 命令类型
     * <p>
     * 查询电量,{@link DMBleMgr#power}
     */
    public static final byte CMD_QUERY = 0x04;
    /**
     * 操作成功的判断依据
     * <p>
     * 若数据发送成功则认为操作成功
     */
    public static final int JUDGE_SEND = 0;
    /**
     * 操作成功的判断依据
     * <p>
     * 接收到返回数据,并且返回数据正确,则认为操作成功
     */
    public static final int JUDGE_RECEIVE = 1;
    /**
     * 定时取消操作的一个订阅器
     */
    Subscription sub;
    /**
     * 操作的超时时间,默认为15秒,可通过函数设置
     */
    private long timesout = 15 * 1000L;
    /**
     * 判断操作成功的依据
     * <p>
     * 部分手机由于接收不到返回数据,这可能是手机问题,也有可能是蓝牙设备的问题,所以需要根据实际情况和测试情况去选择判断依据类型
     */
    private int judge = 1;
    /**
     * 标识当前是否正在执行开
     */
    private boolean opening;
    /**
     * 标识当前是否正在执行配置
     */
    private boolean configing;
    /**
     * 标识当前是否正在查询电量
     */
    private boolean querying;
    /**
     * 标识当前是否正在查询电量
     */
    private boolean reseting;
    /**
     * 蓝牙socket
     */
    private BleSocket mBleSocket;
    /**
     * 蓝牙socket的回调
     */
    private MyBleCallback mBleCallback;
    /**
     * 本管理器使用的回调
     */
    private DMBleCallback mDmBleCallback;
    /**
     * 电量
     */
    private int power;

    public DMBleMgr(DMBleCallback callback) {
        this(callback, null);
    }

    public DMBleMgr(DMBleCallback callback, BleSocket bleSocket) {
        this.mDmBleCallback = callback;
        setBleSocket(bleSocket);
    }


    public byte analyze(byte[] buffer) {
        byte cmd = Protocal.analyze(buffer);
        switch (cmd) {
            case CMD_OPEN:
                setOpening(false);
                break;
            case CMD_CONFIG:
                setConfiging(false);
                break;
            case CMD_QUERY:
                power = buffer[4];
                setQuerying(false);
                break;
            case CMD_RESET:
                setReseting(false);
                mBleSocket.close();
                break;
        }
        return cmd;
    }

    public BleSocket getBleSocket() {
        return mBleSocket;
    }

    public void setBleSocket(BleSocket bleSocket) {
        //将原有的blesocket中的回调清除,以免与本次的blesocket冲突
        if (this.mBleSocket != null) {
            this.mBleSocket.setBleCallback(null);
        }
        //保存并设置读取的服务器ID
        this.mBleSocket = bleSocket;
        this.mBleSocket.setReadUUID(Protocal.getReadUUID());
        //初始化MyBleCallback
        if (this.mBleCallback == null) {
            this.mBleCallback = new MyBleCallback();
        }
        //设置回调
        this.mBleSocket.setBleCallback(this.mBleCallback);
    }


    public synchronized void open(Action1 action1) {
        mBleSocket.send(Protocal.getOpenI(), Protocal.getWriteUUID());
        opening = true;
        sub = Observable.just("").subscribeOn(AndroidSchedulers.mainThread()).delay(timesout, TimeUnit.MILLISECONDS).subscribe(action1);
    }

    public synchronized void config(Action1 action1) {
        mBleSocket.send(Protocal.getConfigI(), Protocal.getWriteUUID());
        configing = true;
        sub = Observable.just("").subscribeOn(AndroidSchedulers.mainThread()).delay(timesout, TimeUnit.MILLISECONDS).subscribe(action1);
    }


    public synchronized void query(Action1 action1) {
        mBleSocket.send(Protocal.getQueryI(), Protocal.getWriteUUID());
        querying = true;
        sub = Observable.just("").subscribeOn(AndroidSchedulers.mainThread()).delay(timesout, TimeUnit.MILLISECONDS).subscribe(action1);
    }

    public synchronized void reset(Action1 action1) {
        mBleSocket.send(Protocal.getResetI(), Protocal.getWriteUUID());
        querying = true;
        sub = Observable.just("").subscribeOn(AndroidSchedulers.mainThread()).delay(timesout, TimeUnit.MILLISECONDS).subscribe(action1);
    }

    public void close() {
        reset(new Action1() {
            @Override
            public void call(Object o) {

            }
        });
    }

    public boolean isOpering() {
        return isOpening() || isConfiging() || isQuerying();
    }

    public void finishOper() {
        setOpening(false);
        setConfiging(false);
        setQuerying(false);
    }

    public boolean isOpening() {
        return opening;
    }

    public void setOpening(boolean opening) {
        this.opening = opening;
        if (!opening) {
            if (sub != null) {
                sub.unsubscribe();
            }
        }
    }

    public boolean isQuerying() {
        return querying;
    }

    public void setQuerying(boolean querying) {
        this.querying = querying;
        if (!querying) {
            if (sub != null) {
                sub.unsubscribe();
            }
        }
    }

    public boolean isReseting() {
        return reseting;
    }

    public void setReseting(boolean reseting) {
        this.reseting = reseting;
        if (!reseting) {
            if (sub != null) {
                sub.unsubscribe();
            }
        }
    }

    public boolean isConfiging() {
        return configing;
    }

    public void setConfiging(boolean configing) {
        this.configing = configing;
        if (!configing) {
            if (sub != null) {
                sub.unsubscribe();
            }
        }
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public long getTimesout() {
        return timesout;
    }

    public void setTimesout(long timesout) {
        this.timesout = timesout;
    }

    class MyBleCallback extends BleCallback {
        @Override
        final void receive(BleSocket bleSocket, String uuid, byte[] instruct) {
            super.receive(bleSocket, uuid, instruct);
            if (judge != JUDGE_RECEIVE) {
                return;
            }
            byte cmd = Protocal.analyze(instruct);
            switch (cmd) {
                case CMD_OPEN:
                    setOpening(false);
                    break;
                case CMD_CONFIG:
                    setConfiging(false);
                    break;
                case CMD_QUERY:
                    power = instruct[4];
                    setQuerying(false);
                    break;
            }
            mDmBleCallback.success(DMBleMgr.this, cmd);
        }

        @Override
        final void sendSuccess(BleSocket bleSocket, byte[] instruct) {
            super.sendSuccess(bleSocket, instruct);
            if (judge != JUDGE_SEND) {
                return;
            }
            byte cmd = Protocal.analyze(instruct);
            switch (cmd) {
                case CMD_OPEN:
                    setOpening(false);
                    break;
                case CMD_CONFIG:
                    setConfiging(false);
                    break;
                case CMD_QUERY:
                    power = instruct[4];
                    setQuerying(false);
                    break;
            }
            mDmBleCallback.success(DMBleMgr.this, cmd);
        }

        @Override
        final void sendFail(BleSocket bleSocket, byte[] instruct, int failStatus) {
            super.sendFail(bleSocket, instruct, failStatus);
            byte cmd = Protocal.analyze(instruct);
            switch (cmd) {
                case CMD_OPEN:
                    setOpening(false);
                    break;
                case CMD_CONFIG:
                    setConfiging(false);
                    break;
                case CMD_QUERY:
                    power = instruct[4];
                    setQuerying(false);
                    break;
            }
            mDmBleCallback.sendFail(DMBleMgr.this, cmd, failStatus);
        }

        @Override
        final void connectFail(BleSocket bleSocket, int failStatus) {
            super.connectFail(bleSocket, failStatus);
            finishOper();
            mDmBleCallback.connectFail(DMBleMgr.this, failStatus);
        }

        @Override
        void connectSuccess(BleSocket bleSocket) {
            super.connectSuccess(bleSocket);
            mDmBleCallback.connectSuccess(DMBleMgr.this);
            mBleSocket.push();
        }

        @Override
        void disconnect(BleSocket bleSocket) {
            super.disconnect(bleSocket);
            mDmBleCallback.disconnect(DMBleMgr.this);
        }

        @Override
        void found(BluetoothDevice device, int rssi) {
            super.found(device, rssi);
            mBleSocket.setBluetoothDevice(device);
            mDmBleCallback.found(device, rssi);
        }
    }
}
