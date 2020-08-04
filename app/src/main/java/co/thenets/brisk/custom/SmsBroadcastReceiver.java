package co.thenets.brisk.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.thenets.brisk.events.CodeDetectedInSmsEvent;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;

/**
 * Created by Roei on 24-Nov-15.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver
{
    public static final String SMS_BUNDLE = "pdus";

    Pattern pattern = Pattern.compile("[0-9]+");

    public void onReceive(Context context, Intent intent)
    {
        Bundle intentExtras = intent.getExtras();
        String smsBody = "";
        if (intentExtras != null)
        {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String verificationCode = "";
            for (int i = 0; i < sms.length; ++i)
            {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                Matcher matcher = pattern.matcher(smsBody);

                String appName = ContentManager.getInstance().getApplicationName(context);
                if (address.contains(appName))
                {
                    while (matcher.find())
                    {
                        verificationCode = matcher.group();
                    }
                }
            }
            EventsManager.getInstance().post(new CodeDetectedInSmsEvent(verificationCode));
        }
    }
}