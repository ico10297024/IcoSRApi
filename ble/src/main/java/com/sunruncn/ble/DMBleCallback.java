package com.sunruncn.ble;

import android.bluetooth.BluetoothDevice;

/**
 * DMBleMgr中使用的回调函数
 */
public interface DMBleCallback {
    /**
     * 当操作成功时回调
     *
     * @param dmBleMgr
     * @param cmd      命令类型 {@link DMBleMgr#CMD_CONFIG,DMBleMgr#CMD_OPEN,DMBleMgr#CMD_QUERY}
     */
    void success(DMBleMgr dmBleMgr, byte cmd);

    /**
     * 当数据发送失败时回调
     *
     * @param dmBleMgr
     * @param cmd        命令类型 {@link DMBleMgr#CMD_CONFIG,DMBleMgr#CMD_OPEN,DMBleMgr#CMD_QUERY}
     * @param failStatus 失败状态码{@link BleSocket#FAIL_STATUS_NONE,BleSocket#FAIL_STATUS_PATH_NOT_FOUND,BleSocket#FAIL_STATUS_PATH_NOT_WRITE}
     */
    void sendFail(DMBleMgr dmBleMgr, byte cmd, int failStatus);

    /**
     * 当与蓝牙设备的连接失败时回调
     *
     * @param dmBleMgr
     * @param failStatus 失败状态码{@link BleSocket#FAIL_STATUS_SERVICES_UNDISCOVER,BleSocket#FAIL_STATUS_UNCONNECT_DISCONNECT}
     */
    void connectFail(DMBleMgr dmBleMgr, int failStatus);

    /**
     * 当与蓝牙设备的连接成功时回调
     *
     * @param dmBleMgr
     */
    void connectSuccess(DMBleMgr dmBleMgr);

    /**
     * 当与蓝牙设备的连接断开时回调
     *
     * @param dmBleMgr
     */
    void disconnect(DMBleMgr dmBleMgr);

    /**
     * 当搜索到符合条件的设备时回调
     *
     * @param device
     * @param rssi   蓝牙设备的信号强度
     */
    void found(BluetoothDevice device, int rssi);
}
