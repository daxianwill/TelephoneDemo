package com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.admin.telephonedemo.R;
import com.permission.CheckPermissionsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/6/5.
 */

public class SMSActivity extends CheckPermissionsActivity {

    private SmsObserver smsObserver;
    private TextView textView;
    private String smsContent;
    private IntentFilter intentFilter;
    private Uri SMS_INBOX = Uri.parse("content://sms/");

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            textView.setText(smsContent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        textView = (TextView) findViewById(R.id.text);

        smsObserver = new SmsObserver(this, mHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true,
                smsObserver);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, intentFilter);
    }

//-----------------------------------------------------------------------短信模块---------------------------
    /**
     * 短信观察者
     */
    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法
            getSmsFromPhone();
        }
    }

    public void getSmsFromPhone() {
        Cursor cursor = null;

        String _id = null;
        String address = null;
        String person = null;
        String date = null;
        String type = null;
        String body = null;
        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"), null, null, null,
                    "date desc");
            if(cursor!=null){
                if(cursor.moveToNext()){//不遍历只拿当前最新的一条短信

                    _id = cursor.getString(cursor.getColumnIndex("_id"));
                    address = cursor.getString(cursor.getColumnIndex("address"));

//                    Message msg=Message.obtain();
//                    Bundle bundle=new Bundle();
//                    bundle.putString("_id", _id);
//                    bundle.putString("address", address);
//                    bundle.putString("person", person);
//                    bundle.putString("date", date);
//                    bundle.putString("type", type);
//                    bundle.putString("body", body);
//                    msg.setData(bundle);
//                    mHandler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(cursor!=null){
                cursor.close();
            }

        }
    }

    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            String format = intent.getStringExtra("format");//23以后需要的
            if(format != null){
                Log.e("format", format);
            }

            for (Object obj : objs) {
                byte[] pdu = (byte[]) obj;
                SmsMessage sms = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    sms = SmsMessage.createFromPdu(pdu,format);//API23以后修改成这个
                }else{
                    sms = SmsMessage.createFromPdu(pdu);
                }
                // 短信的内容
                String message = sms.getMessageBody();
                Log.e("logo", "message     " + message);
                // 短息的手机号。。+86开头？
                String from = sms.getOriginatingAddress();
                Log.e("logo", "from     " + from);
                if (!TextUtils.isEmpty(from)) {
                    String code = patternCode(message);
                    if (!TextUtils.isEmpty(code)) {
                        smsContent = code;
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }
        }
    };


    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}

//
//sms主要结构：
//        　　_id：短信序号，如100
//        　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
//        　　address：发件人地址，即手机号，如+86138138000
//        　　person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
//        　　date：日期，long型，如1346988516，可以对日期显示格式进行设置
//        　　protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
//        　　read：是否阅读0未读，1已读
//        　　status：短信状态-1接收，0complete,64pending,128failed
//        　　type：短信类型1是接收到的，2是已发出
//        　　body：短信具体内容
//        　　service_center：短信服务中心号码编号，如+8613800755500


