package com.sunruncn.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import java.util.UUID;

/**
 * 蓝牙4.0帮助类，用于操作蓝牙,doc中称为蓝牙操作器
 * <p>
 * 目前功能：开启搜索，关闭搜索，设置开关。
 * <p>
 * 搜索支持筛选功能，通过{@link BleFilter}
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleHelper {
    /**
     * 用于搜索的通道ID,根据系统api,加了这个搜索效率比较快
     */
    UUID[] mServicesUUID;
    /**
     * 当前界面的上下文
     */
    private Context mContext;
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBleAda;
    /**
     * 设备发现的广播接收器
     */
    private BroadcastReceiver foundReceiver;
    /**
     * 回调
     */
    private BleCallback mBleCallback;
    /**
     * 蓝牙设备筛选器
     */
    private BleFilter mBleFilter;

    /**
     * 蓝牙设备搜索的回调,{@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}
     */
    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            synchronized (mBleCallback) {
                if (mBleFilter != null && mBleFilter.onBleFilter(device)) {
                    mBleCallback.found(device, rssi);
                } else {
                    log.d(String.format("name:%s,mac:%s,rssi:%d,过滤设备", device.getName(), device.getAddress(), rssi));
                }
            }
        }
    };
    /**
     * 蓝牙设备搜索的线程
     * <p>
     * 由于低版本回调过一次的设备将不再回调,所以使用线程间隔1秒关闭打开搜索来重复搜索设备
     */
    private ScanThread scanThread;

    /**
     * 该构造函数要求传入一个上下文以及回调对象,没有默认的蓝牙筛选器,也就是搜索到的所有ble都会进行回调
     *
     * @param context     当前界面的上下文
     * @param bleCallback 回调
     */
    public BleHelper(Context context, BleCallback bleCallback) {
        this.mContext = context;
        this.mBleCallback = bleCallback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bleMgr = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBleAda = bleMgr.getAdapter();
        } else {
            mBleAda = BluetoothAdapter.getDefaultAdapter();
        }
        //绑定广播
        foundReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) -1);
                scanCallback.onLeScan(device, rssi, null);
            }
        };
        mContext.registerReceiver(foundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    /**
     * 该构造函数要求传入上下文,搜索服务的uuid以及回调对象,可以根据搜索服务器的uuid进行定向搜索
     *
     * @param context      当前界面的上下文
     * @param servicesUUID 用于定向搜索的服务ID
     * @param bleCallback  回调
     */
    public BleHelper(Context context, UUID[] servicesUUID, BleCallback bleCallback) {
        this.mContext = context;
        this.mBleCallback = bleCallback;
        this.mServicesUUID = servicesUUID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bleMgr = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBleAda = bleMgr.getAdapter();
        } else {
            mBleAda = BluetoothAdapter.getDefaultAdapter();
        }
        //绑定广播
        foundReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) -1);
                scanCallback.onLeScan(device, rssi, null);
            }
        };
        mContext.registerReceiver(foundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    /**
     * 不使用该类时请调用该函数销毁
     */
    public void onDestroy() {
        if (foundReceiver != null) {
            mContext.unregisterReceiver(foundReceiver);
            foundReceiver = null;
        }
    }

    /**
     * 开启搜索
     */
    public void startScan() {
//        mScanBluetoothDevice.clear();
        if (scanThread == null || scanThread.isClosed()) {
            scanThread = new ScanThread();
            scanThread.start();
        }
    }

    /**
     * 停止搜索
     */
    public void stopScan() {
        if (scanThread != null) {
            scanThread.close();
            scanThread = null;
        }
    }

    /**
     * 获取蓝牙的开启状态
     *
     * @return
     */
    public boolean isEnable() {
        return mBleAda.isEnabled();
    }

    /**
     * 启动蓝牙
     */
    public void enable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mContext.startActivity(enableBtIntent);
    }

    /**
     * 关闭蓝牙
     */
    public void disable() {
        mBleAda.disable();
    }

    //region GETSET
    public BleCallback getBleSmCallback() {
        return mBleCallback;
    }

    public BleHelper setBleSmCallback(BleCallback mBleCallback) {
        this.mBleCallback = mBleCallback;
        return this;
    }

    public BleFilter getBleFilter() {
        return mBleFilter;
    }

    public BleHelper setBleFilter(BleFilter bleFilter) {
        this.mBleFilter = bleFilter;
        return this;
    }

    public BluetoothAdapter getBleAdapter() {
        return mBleAda;
    }

    public void setBleAdapter(BluetoothAdapter mBleAda) {
        this.mBleAda = mBleAda;
    }

    //endregion

    /**
     * 蓝牙筛选器
     * <p>
     * 当搜索到一个蓝牙设备,先调用筛选器,当筛选器返回true时,才会调用回调
     */
    public interface BleFilter {
        boolean onBleFilter(BluetoothDevice device);
    }

    /**
     * 用于定时间隔关闭和开启搜索的线程
     * <p>
     * 由于5.0以下安卓系统在搜索到一个设备后将不会再搜索到这个设备,所以需要做定时开关来反复搜索
     */
    class ScanThread extends IcoThread {
        @Override
        public void run() {
            while (!isClosed()) {
                if (mServicesUUID != null) {
                    mBleAda.startLeScan(mServicesUUID, scanCallback);
                } else {
                    mBleAda.startLeScan(scanCallback);
                }
//                mBleAda.startDiscovery();
                try {
                    Thread.currentThread().sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBleAda.stopLeScan(scanCallback);
//                mBleAda.cancelDiscovery();
            }
        }
    }
}
