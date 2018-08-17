package com.sunruncn.bledock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class BaseFragActivity extends AppCompatActivity {
    public final static String DEFAULT_DIALOG = "default";
    public BaseFragActivity mActivity;
    public LinkedHashMap<String, Dialog> mDialogs = new LinkedHashMap<>();
    public DialogFragment mDialogFrag;
    public Toast mToast;

    /**
     * 媒介，用他的post方法来执行线程
     */
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }


    /**
     * “返回”按钮
     *
     * @param v
     */
    public void onClickBack(View v) {
        mActivity.finish();
    }

    /**
     * 弹出土司
     *
     * @param stringResId
     */
    public void showToast(@StringRes final int stringResId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CharSequence content = getResources().getString(stringResId);
                if (mToast != null) {
                    mToast.setText(content);
                    mToast.show();
                    return;
                }
                mToast = Toast.makeText(mActivity, content, Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }


    /**
     * 弹出土司
     *
     * @param text
     */
    public void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CharSequence content = TextUtils.isEmpty(text) ?/* getResources().getString(R.string.ico_application_error)*/"程序出错，请稍候再试!" : text;
                if (mToast != null) {
                    mToast.setText(content);
                    mToast.show();
                    return;
                }
                mToast = Toast.makeText(mActivity, content, Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    /**
     * 弹出土司
     *
     * @param text
     */
    public void showToasts(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CharSequence content = TextUtils.isEmpty(text) ? /*getResources().getString(R.string.ico_application_error) */"程序出错，请稍候再试!" : text;
                Toast toast = Toast.makeText(mActivity, content, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /**
     * 关闭当前对话框，显示输入参数所表示的对话框
     *
     * @param _dialog
     */
    public void showDialog(Dialog _dialog) {
        showDialog(_dialog, DEFAULT_DIALOG);
    }

    /**
     * 关闭对话框
     */
    public void dismissDialog() {
        dismissDialog(DEFAULT_DIALOG);
    }

    /**
     * 关闭当前对话框，显示输入参数所表示的对话框
     *
     * @param _dialog
     */
    public void showDialog(Dialog _dialog, String key) {
        dismissDialog(key);
        mDialogs.put(key, _dialog);
        _dialog.show();
    }

    /**
     * 关闭对话框
     */
    public void dismissDialog(String key) {
        Dialog dialog = mDialogs.remove(key);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 关闭全部的对话框
     */
    public void dismissDialogs() {
        Iterator<Map.Entry<String, Dialog>> iter = mDialogs.entrySet().iterator();
        while (iter.hasNext()) {
            dismissDialog(iter.next().getKey());
        }
        //下面的代码通过findbugs插件表示影响性能
//        for (String key : mDialogs.keySet()) {
//            dismissDialog(key);
//        }
    }

    /**
     * 显示对话框碎片
     *
     * @param dialogFragment
     */
    public void showDialogFrag(DialogFragment dialogFragment, String tag) {
        showDialogFrag(dialogFragment, tag, getSupportFragmentManager());
    }


    /**
     * 显示对话框碎片
     *
     * @param dialogFragment
     */
    public void showDialogFrag(DialogFragment dialogFragment, String tag, FragmentManager fragmentManager) {
        dismissDialogFrag();
        if (!TextUtils.isEmpty(tag)) {
            if (!dialogFragment.isAdded() || dialogFragment.isHidden()) {
                dialogFragment.show(fragmentManager, tag);
            }
        }
        mActivity.mDialogFrag = dialogFragment;
    }

    /**
     * 关闭对话框碎片
     */
    public void dismissDialogFrag() {
        if (mDialogFrag != null && mDialogFrag.isVisible()) {
            mDialogFrag.dismiss();
            mDialogFrag = null;
        }
    }

    /**
     * 程序退出的广播
     */
    public class ExitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mActivity.finish();
        }
    }
}
