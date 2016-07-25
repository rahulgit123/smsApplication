package com.example.rsinha.smsapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MyReciever extends BroadcastReceiver {
    public MyReciever() {
        Log.i("Check","Its active");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        Log.i("Check","Its detected");
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get("pdus");
            String smsMessageStr = "";

            MainActivity inst = MainActivity.instant();
            String phone = MainActivity.phoneNo;

            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                //smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr = smsBody;
                //Log.i("Check",address);
                address.trim();
                address=MainActivity.valNo(address);
                if(address.equals(phone))
                    inst.sendSMS(smsMessageStr);
                Log.i("Check","Adress = " +address+"  ,  "+phone+"\n"+
                        "Message : "+smsMessageStr);
            }

            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
        }
        Log.i("Check","Its Out");

    }
}
