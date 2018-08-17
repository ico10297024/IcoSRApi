package com.sunruncn.mqtt;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sunruncn.lib.mqtt.MqttSwitchCallback;
import com.sunruncn.lib.mqtt.MqttSwtichMgr;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    //    public static final String PROJECT = "switch";
    public static final String PROJECT = "market";
    //    public static final String HOST = "tcp://101.37.26.48:1883";
    public static final String HOST = "tcp://sriot.sunruncn.com:1883";
    public static final int USERID = 5;
    //    public static final String SERIAL = "CECD4A00";
    public static final String SERIAL = "85381700";
    AppCompatActivity mActivity;
    Handler mHandler;
    AboutControl aboutControl;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        text = (TextView) findViewById(R.id.text);
        aboutControl = new AboutControl();
    }

    public void onClickOpen(View v) {
        aboutControl.control(true);
    }

    public void onClickClose(View v) {
        aboutControl.control(false);
    }

    public void onClickQuery(View v) {
        aboutControl.query();
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 关于与设备交互
     */
    public class AboutControl {
        MqttSwitchCallback callback = new MqttSwitchCallback() {

            @Override
            public void queryFinish(MqttSwtichMgr mqttSwtichMgr, boolean isSuccess, final boolean isOpen) {
                if (isSuccess) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText((isOpen ? "开" : "关"));
                        }
                    });
                } else {
                    mqttSwtichMgr.finishOper();
                    showToast("操作失败");
                }
//                hideDialog();
            }

            @Override
            public void onoffFinish(MqttSwtichMgr mqttSwtichMgr, boolean isSuccess, final boolean isOpen) {
                if (isSuccess) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText((isOpen ? "开" : "关"));
                        }
                    });
                } else {
                    mqttSwtichMgr.finishOper();
                    showToast("操作失败");
                }
//                hideDialog();
            }

            @Override
            public void unknowError(MqttSwtichMgr mqttSwtichMgr, byte data) {
                showToast(MqttCodeMsg.getErrorMsg(data));
//                hideDialog();
            }

            @Override
            public void connectFail(MqttSwtichMgr mqttSwtichMgr, Exception e) {
                mqttSwtichMgr.finishOper();
                showToast("连接失败");
//                hideDialog();
            }

            @Override
            public void connectSuccess(MqttSwtichMgr mqttSwtichMgr, boolean b, String s) {
                try {
                    mqttSwtichMgr.subscribe(SERIAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendSuccess(MqttSwtichMgr mqttSwtichMgr, IMqttDeliveryToken iMqttDeliveryToken) {

            }

            @Override
            public void connectDisconnect(MqttSwtichMgr mqttSwtichMgr, Throwable throwable) {
                if (mqttSwtichMgr.isClosed()) {
                    return;
                }
//                hideDialog();
                throwable.printStackTrace();
                String errorMsg = "";
                switch (((MqttException) throwable).getReasonCode()) {
                    case 32199:
                        errorMsg = "网络异常";
                        break;
                    case 32109:
                        errorMsg = "其他地方已登录";
                        break;
                    default:
                        errorMsg = "异常断开";
                }
                showToast(errorMsg);
            }
        };
        private ProgressDialog mDialog;
        private MqttSwtichMgr mqttSwtichMgr;

        public AboutControl() {
            mqttSwtichMgr = new MqttSwtichMgr(MainActivity.this, HOST, PROJECT, USERID, callback);
            mqttSwtichMgr.connect();
        }

//        public void showDialog() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //对话框
//                    if (mDialog == null) {
//                        mDialog = new ProgressDialog(mActivity);
//                        mDialog.setMessage("操作中");
//                        mDialog.setCancelable(false);
//                    }
//                    mDialog.show();
//                }
//            });
//        }
//
//        public void hideDialog() {
//            if (mDialog != null && mDialog.isShowing()) {
//                mDialog.dismiss();
//            }
//        }

        public void query() {
//            showDialog();
//            mqttSwtichMgr.query(new TimeoutTask(), SERIAL);
            mqttSwtichMgr.query(null, SERIAL);
        }

        /**
         * 设置设备开关状态
         */
        public void control(boolean flag) {
//            showDialog();
//            mqttSwtichMgr.onoff(new TimeoutTask(), SERIAL, flag);
            mqttSwtichMgr.onoff(null, SERIAL, flag);
        }

        protected void onDestroy() {
            if (mqttSwtichMgr != null) {
                mqttSwtichMgr.close();
                mqttSwtichMgr = null;
            }
        }

//        class TimeoutTask extends TimerTask {
//
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showToast("请求超时");
//                        aboutControl.hideDialog();
//                    }
//                });
//            }
//        }
    }
}
