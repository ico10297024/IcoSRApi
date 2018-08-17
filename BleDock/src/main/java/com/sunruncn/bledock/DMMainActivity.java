package com.sunruncn.bledock;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.sunruncn.ble.BleSocket;
import com.sunruncn.ble.DMBleCallback;
import com.sunruncn.ble.DMBleMgr;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import rx.functions.Action1;

public class DMMainActivity extends BaseFragActivity implements EasyPermissions.PermissionCallbacks {
    AboutBle aboutBle;
    Button btn_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ble);
//        ButterKnife.bind(this);
        aboutBle = new AboutBle();
        btn_query = (Button) findViewById(R.id.btn_query);
//        log.w("==="+ Common.bytes2Int16(Protocal.getQueryI()," "));
    }


    public void onClickOpen(View v) {
        aboutBle.open();
    }

    public void onClickConfig(View v) {
        aboutBle.config();
    }

    public void onClickQuery(View v) {
        aboutBle.query();
    }

    public void onClickOpenAndQuery(View v) {
        aboutBle.openAndQuery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aboutBle.onDestroy();
    }


    public class AboutBle {
        /*蓝牙参数*/
        //蓝牙连接对象,可以通过构造函数来实现不同的功能,具体可以查看下面实体创建的说明
        public BleSocket bleSocket;
        //设备管理器,通过管理器去对蓝牙连接对象进行管理,包括数据的发送和接收,蓝牙的连接与断开,信息的存储等等
        public DMBleMgr dmBleMgr;
        //这是蓝牙的回调,在适当的时候进行回调
        public MyBleCallback myBleCallback;
        //这是一个超时任务,若操作超过一定时间将会被出发
        public Action1 bleTimeTask = new Action1() {
            @Override
            public void call(Object o) {
                dismissDialog();
                if (bleSocket.getBluetoothDevice() == null) {
                    showToast("周边未发现该设备！");
                } else if (bleSocket.getConnectionState() == BleSocket.STATE_CONNECTING) {
                    showToast("连接超时！");
                } else if (bleSocket.getConnectionState() == BleSocket.STATE_CONNECTED) {
                    showToast("服务发现超时！");
                } else if (bleSocket.getConnectionState() == BleSocket.STATE_CONNECTED_DISCOVER) {
                    showToast("操作超时！");
                } else {
                    showToast("超时！");
                }
                if (bleSocket != null) {
                    bleSocket.close();
                }
                dmBleMgr.finishOper();
            }
        };
        private boolean openAndQuery = false;

        public AboutBle() {
            /*1.创建回调函数的实体*/
            myBleCallback = new MyBleCallback();
            /*2.创建蓝牙连接的对象
                蓝牙连接的整个流程包括有:搜索/连接/服务发现,至此这个蓝牙才能连接上
                {@link BleHelper}这个类能帮助我们管理蓝牙,同时可以设置筛选器,筛选器只有在满足条件的情况下,才会对搜索到的设备进行回调
             */
            //该方法在搜索到设备后进行调用去创建,也就是说,搜索需要在外部进行
//            bleSocket = new BleSocket(mActivity,bluetoothDevice);

            //该方法在创建蓝牙连接对象的同时,可以设置筛选的关键字和筛选类型,分别有0(筛选mac,不带间隔符),1(筛选名称,模糊搜索)
            // 然后再对蓝牙连接对象进行操作时,若当前BleSocket不存在bluetoothDevice时,会执行搜索,并且根据筛选器条件进行筛选
//            bleSocket = new BleSocket(mActivity, "010BA04675FB", 0);
//            bleSocket = new BleSocket(mActivity, "A4C13862FA58", 0);
//            bleSocket = new BleSocket(mActivity, "A4C13862FA5F", 0);

            //同上,上面这个函数在内部设置了BleHelper,该方法是从外部设置BleHelper
//            bleSocket = new BleSocket(mActivity, myBleCallback, BleHelper);

            //该方法设置搜索服务ID,据sdk和百度说可以精准搜索,加快搜索速度,实际体验,感觉并没有加快多少
//            bleSocket = new BleSocket(mActivity, new UUID[]{});

            bleSocket = new BleSocket(mActivity, "DM", 1);
            //3.创建管理器,使用管理器去操作蓝牙连接对象
            dmBleMgr = new DMBleMgr(myBleCallback, bleSocket);
        }

        public void open() {
            openAndQuery = false;
            if (!bleSocket.getBleHelper().isEnable()) {
                bleSocket.getBleHelper().enable();
                return;
            }
            /*targetsdkversion在23以上,需要在运行时检查权限,由于是蓝牙操作,所以建议设置为18*/
//            if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                EasyPermissions.requestPermissions(mActivity, "使用蓝牙开门需要以下权限允许", 0x001, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//                return;
//            }
            bleSocket.reset();
            dmBleMgr.open(bleTimeTask);
            showDialog(ProgressDialog.show(mActivity, "", "正在操作中", true, false));
        }

        public void config() {
            openAndQuery = false;
            if (!bleSocket.getBleHelper().isEnable()) {
                bleSocket.getBleHelper().enable();
                return;
            }
            /*targetsdkversion在23以上,需要在运行时检查权限,由于是蓝牙操作,所以建议设置为18*/
//            if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                EasyPermissions.requestPermissions(mActivity, "使用蓝牙开门需要以下权限允许", 0x001, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//                return;
//            }
            bleSocket.reset();
            dmBleMgr.config(bleTimeTask);
            showDialog(ProgressDialog.show(mActivity, "", "正在操作中", true, false));
        }

        public void query() {
            openAndQuery = false;
            if (!bleSocket.getBleHelper().isEnable()) {
                bleSocket.getBleHelper().enable();
                return;
            }
            /*targetsdkversion在23以上,需要在运行时检查权限,由于是蓝牙操作,所以建议设置为18*/
//            if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                EasyPermissions.requestPermissions(mActivity, "使用蓝牙开门需要以下权限允许", 0x001, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//                return;
//            }
            bleSocket.reset();
            dmBleMgr.query(bleTimeTask);
            showDialog(ProgressDialog.show(mActivity, "", "正在操作中", true, false));
        }

        public void openAndQuery() {
            openAndQuery = true;
            if (!bleSocket.getBleHelper().isEnable()) {
                bleSocket.getBleHelper().enable();
                return;
            }
            /*targetsdkversion在23以上,需要在运行时检查权限,由于是蓝牙操作,所以建议设置为18*/
//            if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                EasyPermissions.requestPermissions(mActivity, "使用蓝牙开门需要以下权限允许", 0x001, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//                return;
//            }
            bleSocket.reset();
            dmBleMgr.open(bleTimeTask);
            showDialog(ProgressDialog.show(mActivity, "", "正在操作中", true, false));
        }


        public void onDestroy() {
            if (bleSocket != null) {
                bleSocket.onDestroy();
            }
        }

        class MyBleCallback implements DMBleCallback {
            @Override
            public void success(final DMBleMgr dmBleMgr, byte cmd) {
                dismissDialog();
                switch (cmd) {
                    case DMBleMgr.CMD_OPEN:
                        showToast("开门成功");
                        //这里开门成功了,但是因为还要查询电量,所以执行查询函数,然后return掉
                        if (openAndQuery) {
                            query();
                            return;
                        } else {
                            return;
                        }
//                        break;
                    case DMBleMgr.CMD_QUERY:
                        showToasts("查询成功");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_query.setText("" + dmBleMgr.getPower());
                            }
                        });
                        break;
                    case DMBleMgr.CMD_CONFIG:
                        showToast("配置成功");
                        break;
                    case DMBleMgr.CMD_RESET:
                        showToast("配置成功");
                        break;
                }
                dmBleMgr.close();
//                bleSocket.close();
            }

            @Override
            public void sendFail(DMBleMgr dmBleMgr, byte cmd, int failStatus) {
                dismissDialog();
                switch (failStatus) {
                    case BleSocket.FAIL_STATUS_NONE:
                        showToast("操作失败");
                        break;
                    case BleSocket.FAIL_STATUS_PATH_NOT_FOUND:
                        showToast("数据通道未找到");
                        break;
                    case BleSocket.FAIL_STATUS_PATH_NOT_WRITE:
                        showToast("数据通道没有写入特性");
                        break;
                    case BleSocket.FAIL_STATUS_SERVICES_UNDISCOVER:
                        showToast("未发现服务通道");
                        break;
                    case BleSocket.FAIL_STATUS_UNCONNECT_DISCONNECT:
                        showToast("连接失败");
                        break;
                }
                bleSocket.close();
            }

            @Override
            public void connectFail(DMBleMgr dmBleMgr, int failStatus) {
                dismissDialog();
                switch (failStatus) {
                    case BleSocket.FAIL_STATUS_NONE:
                        showToast("操作失败");
                        break;
                    case BleSocket.FAIL_STATUS_PATH_NOT_FOUND:
                        showToast("数据通道未找到");
                        break;
                    case BleSocket.FAIL_STATUS_PATH_NOT_WRITE:
                        showToast("数据通道没有写入特性");
                        break;
                    case BleSocket.FAIL_STATUS_SERVICES_UNDISCOVER:
                        showToast("未发现服务通道");
                        break;
                    case BleSocket.FAIL_STATUS_UNCONNECT_DISCONNECT:
                        showToast("连接失败");
                        break;
                }
                bleSocket.close();
            }

            @Override
            public void connectSuccess(DMBleMgr dmBleMgr) {

            }

            @Override
            public void disconnect(DMBleMgr dmBleMgr) {

            }

            @Override
            public void found(BluetoothDevice bluetoothDevice, int i) {

            }
        }
    }
}
