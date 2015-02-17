package io.github.poerhiza.textsafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.github.poerhiza.textsafe.constants.Constants;
import io.github.poerhiza.textsafe.utilities.DataTransport;
import io.github.poerhiza.textsafe.utilities.SQLHelper;
import io.github.poerhiza.textsafe.valueobjects.AutoResponse;


public class SMSBroadcastInterception extends BroadcastReceiver {
    private SharedPreferences settings;
    private static ArrayList<String> lastSent = null;
    private static Timer timer = null;
    private static TimerTask clearLastSent = null;
    private static boolean timerSet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (lastSent == null) {
            lastSent = new ArrayList<String>();
        }

        if (timer == null) {
            timer = new Timer();

            if (clearLastSent == null) {
                clearLastSent = new CustomTimerTask();
            }
        }

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            settings = PreferenceManager.getDefaultSharedPreferences(context);

            if (settings.getBoolean(Constants.TOGGLE_AUTO_RESPONSE, false)) {
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                String msg_from;
                String msg;

                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];

                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            msg = msgs[i].getMessageBody();

                            if (msg_from != null && !lastSent.contains(msg_from)) {
                                SmsManager sms = SmsManager.getDefault();
                                String response = settings.getString(Constants.AUTO_RESPONSE_VALUE, Constants.DEFAULT_AUTO_RESPONSE);

                                if (!response.equalsIgnoreCase(Constants.DEFAULT_AUTO_RESPONSE)) {
                                    SQLHelper sqlHelper = new SQLHelper(context);
                                    AutoResponse ar = sqlHelper.getAutoResponse(Integer.valueOf(response));

                                    if (ar != null) {
                                        response = ar.getResponse();
                                        ar.setFreq(ar.getFreq() + 1);
                                        sqlHelper.updateAutoResponse(ar);
                                    } else {
                                        response = Constants.DEFAULT_AUTO_RESPONSE;
                                    }
                                } else {
                                    response = Constants.DEFAULT_AUTO_RESPONSE;
                                }

                                sms.sendTextMessage(msg_from, null, response, null, null);
                                lastSent.add(msg_from);

                                if (!timerSet) {
                                    timer.purge();
                                    timer.schedule(clearLastSent, 10000);
                                    timerSet = true;
                                }
                            }

                            if (settings.getBoolean(Constants.TOGGLE_FORWARD_TO_WS, false)) {
                                String URL = settings.getString(Constants.INPUT_WS_TO_FORWARD, "");

                                if (!URL.equalsIgnoreCase("")) {
                                    HashMap<String, String> data = new HashMap<String, String>();
                                    data.put("from", msg_from);
                                    data.put("to", DataTransport.returnNumber(context));
                                    data.put("message", msg);

                                    DataTransport.sendDataToHTTPService(URL, data);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception caught", e.getMessage());
                    }
                }

                if (settings.getBoolean(Constants.TOGGLE_DROP_INCOMING, false)) {
                    abortBroadcast();
                }
            }
        }
    }

    protected static void clearAutoResponseLog() {
        lastSent.clear();
    }

    //	without this an autoResponse storm can ensue!
    private class CustomTimerTask extends TimerTask {
        private Handler mHandler = new Handler();

        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            SMSBroadcastInterception.clearAutoResponseLog();
                            timerSet = false;
                            timer = null;
                        }
                    });
                }
            }).start();

        }

    }
}