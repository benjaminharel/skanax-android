package co.thenets.brisk.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.MainActivity;
import co.thenets.brisk.activities.OrderActivity;
import co.thenets.brisk.adapters.GsonTypeAdapters;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.enums.PushType;
import co.thenets.brisk.enums.RoleType;
import co.thenets.brisk.events.OrdersStateChangedEvent;
import co.thenets.brisk.events.RefreshOrderActivityEvent;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.general.BriskApplication;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;


/**
 * Created by DAVID BELOOSESKY on 11/02/2015.
 */
public class AppGcmIntentReceiver extends AppPushBroadcastReceiver
{
    public static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private static final String LOG_TAG = AppGcmIntentReceiver.class.getSimpleName();

    @Override
    public void onPushReceived(Context context, Intent intent, Bundle extras)
    {
        Log.d("Push Received", extras.toString());

        JSONObject pushPayloadObj;

        // Tell the Gson about some Types that I have.
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PushType.class, GsonTypeAdapters.numberAsPushTypeAdapter);
        gsonBuilder.registerTypeAdapter(OrderState.class, GsonTypeAdapters.numberAsOrderStateAdapter);
        Gson gson = gsonBuilder.create();

        try
        {
            pushPayloadObj = new JSONObject(extras.getString(Params.GCM_EXTRA_PAYLOAD));
            PushType pushType = PushType.fromInt(pushPayloadObj.getInt(Params.GCM_TYPE));
            switch (pushType)
            {
                case STORE_STATE:
                    StoreStatePushPayload storeStatePushPayload = gson.fromJson(extras.getString(Params.GCM_EXTRA_PAYLOAD), StoreStatePushPayload.class);
                    handleStoreStatePush(context, extras.getString(Params.GCM_EXTRA_MESSAGE), storeStatePushPayload);
                    break;
                case ORDER:
                    OrderPushPayload pushPayload = gson.fromJson(extras.getString(Params.GCM_EXTRA_PAYLOAD), OrderPushPayload.class);
                    handleOrderPush(context, extras.getString(Params.GCM_EXTRA_MESSAGE), pushPayload);
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void handleStoreStatePush(Context context, String notificationMessage, StoreStatePushPayload pushPayload)
    {
        BriskApplication briskApplication = (BriskApplication) context.getApplicationContext();
        boolean applicationInForeground = briskApplication.getAppLifecycleHandler().isApplicationInForeground();

        if (!applicationInForeground)
        {
            // App on background, Show notification.
            sendStoreStateNotification(context, notificationMessage, pushPayload, true);
        }
        else
        {
            // App on foreground
            if(ContentManager.getInstance().isAppInSellerMode())
            {
                // The app is on seller mode, show push without action
                sendStoreStateNotification(context, notificationMessage, pushPayload, false);
                EventsManager.getInstance().post(new StoreUpdatedEvent(true));
            }
            else
            {
                // The app is on customer mode - add action to move to store mode
                sendStoreStateNotification(context, notificationMessage, pushPayload, true);
            }
        }
    }

    private void handleOrderPush(Context context, String notificationMessage, OrderPushPayload pushPayload)
    {
        BriskApplication briskApplication = (BriskApplication) context.getApplicationContext();
        boolean applicationInForeground = briskApplication.getAppLifecycleHandler().isApplicationInForeground();

        if (!applicationInForeground)
        {
            // App on background, Show notification.
            sendOrderNotification(context, notificationMessage, pushPayload);
        }
        else
        {
            // App on foreground
            if (pushPayload != null)
            {
                switch (pushPayload.getType())
                {
                    case ORDER:
                        if (pushPayload.getRole() == RoleType.CUSTOMER)
                        {
                            if (OrderActivity.isVisible() && pushPayload.getData().getOrderID().equals(OrderActivity.getOrderID()))
                            {
                                // The same OrderActivity is visible right now, so refreshOrders it without push
                                EventsManager.getInstance().post(new RefreshOrderActivityEvent());
                            }
                            else
                            {
                                // The app is open, but the app is not open on the same Order activity
                                sendOrderNotification(context, notificationMessage, pushPayload);
                            }

                            EventsManager.getInstance().post(new OrdersStateChangedEvent());
                        }
                        else if (pushPayload.getRole() == RoleType.STORE)
                        {
                            if (!ContentManager.getInstance().isSeller())
                            {
                                // It's a push for seller, but the user isn't register as a seller yet ignore the push
                                Log.e(LOG_TAG, "Push for seller, but the user isn't register as a seller yet");
                            }
                            else
                            {
                                if (OrderActivity.isVisible() && pushPayload.getData().getOrderID().equals(OrderActivity.getOrderID()))
                                {
                                    // The same OrderActivity is visible right now, so refreshOrders it without push
                                    EventsManager.getInstance().post(new RefreshOrderActivityEvent());
                                }
                                else
                                {
                                    // The app is open, but the app is not open on the same Order activity
                                    sendOrderNotification(context, notificationMessage, pushPayload);
                                }

                                EventsManager.getInstance().post(new OrdersStateChangedEvent());

                                // If the app on seller mode
                                if (ContentManager.getInstance().isAppInSellerMode())
                                {
                                    // If the push regarding closed / payment
                                    if (pushPayload.getData().getState() == OrderState.CLOSED
                                            || pushPayload.getData().getState() == OrderState.OPEN)
                                    {
                                        // Update dashboard
                                        EventsManager.getInstance().post(new StoreUpdatedEvent(false));
                                    }
                                }
                            }
                        }
                        break;
                }
            }
            else
            {
                Log.e(LOG_TAG, "Got push without payload");
            }
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendStoreStateNotification(Context context, String pushMessage, StoreStatePushPayload pushPayload, boolean actionWhenClickOnPush)
    {
        Intent intent = null;
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Set general data on the notification (like text icon and sound)
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(pushMessage)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessage))
                        .setAutoCancel(true)
                        .setSound(sound);

        if(actionWhenClickOnPush)
        {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(Params.GCM_TYPE, pushPayload.getType());

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
        else
        {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendOrderNotification(Context context, String pushMessage, OrderPushPayload pushPayload)
    {
        Intent intent = null;
        Uri sound = null;

        if (pushPayload.getRole() == RoleType.STORE && pushPayload.getData().getState() == OrderState.OPEN)
        {
            sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.money_sound);
        }
        else
        {
            sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound);
        }
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Set general data on the notification (like text icon and sound)
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(pushMessage)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessage))
                        .setAutoCancel(true)
                        .setSound(sound);

        intent = new Intent(context, MainActivity.class);
        if (pushPayload.getType().equals(PushType.ORDER))
        {
            intent.putExtra(Params.GCM_TYPE, pushPayload.getType());
            intent.putExtra(Params.ORDER_ID, pushPayload.getData().getOrderID());
            intent.putExtra(Params.ORDER_ROLE, pushPayload.getRole());
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}