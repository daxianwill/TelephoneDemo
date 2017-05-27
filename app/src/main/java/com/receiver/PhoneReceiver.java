package com.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phone.PhoneView;

/**
 * 电话状态接收者
 */
public class PhoneReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("PhoneReceiver",action);
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
        } else {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            String incoming_number = "";
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incoming_number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    try {
                        //3s后再开启activity，是为了挡在系统的接听界面之前
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent tmpI = new Intent(context, PhoneView.class);
                    tmpI.putExtra(PhoneView.INCOMING_CALL_NAME,incoming_number);
                    tmpI.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(tmpI);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

}