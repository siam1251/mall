package com.ivanhoecambridge.mall.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.WelcomeMessage;
import com.ivanhoecambridge.mall.receivers.WelcomeMsgReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Kay on 2016-10-11.
 */

public class KcpNotificationManager{

    public static final int NOTIFICATION_ID_WELCOME = 1;
    public static final String NOTIBTN_SAVE = "notibtn_save";
    public static final String NOTIBTN_DISMISS = "notibtn_dismiss";
    public static final String INTENT_EXTRA_KEY_WELCOME_NOTI_ID = "welcome_noti_id";
    public static final String INTENT_EXTRA_KEY_BTN = "key_btn";

    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    public KcpNotificationManager(Context context){
        mContext = context;
    }

    public void sendWelcomeNotification() {
        mBuilder = new NotificationCompat.Builder(mContext);

        Intent saveIntent = new Intent(mContext, WelcomeMsgReceiver.class);
        saveIntent.putExtra(INTENT_EXTRA_KEY_BTN, NOTIBTN_SAVE);
        saveIntent.putExtra(INTENT_EXTRA_KEY_WELCOME_NOTI_ID, NOTIFICATION_ID_WELCOME);
        PendingIntent savePendingIntent = PendingIntent.getBroadcast(mContext, 0, saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismissIntent = new Intent(mContext, WelcomeMsgReceiver.class);
        dismissIntent.putExtra(INTENT_EXTRA_KEY_BTN, NOTIBTN_DISMISS);
        dismissIntent.putExtra(INTENT_EXTRA_KEY_WELCOME_NOTI_ID, NOTIFICATION_ID_WELCOME);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(mContext, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = mContext.getString(R.string.welcome_message) + " " + mContext.getString(R.string.mall_name) + "!";
        String body = mContext.getString(R.string.welcome_message_ask_notification);

        mBuilder.setContentIntent(savePendingIntent);
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);

        mBuilder.setSmallIcon(R.drawable.icn_noti);
//        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icn_noti_parking));

//        mBuilder.setSmallIcon(R.drawable.icn_noti);

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setAutoCancel(true);
        mBuilder.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

//        mBuilder.addAction(R.drawable.img_profile_default, mContext.getString(R.string.action_dismiss), dismissPendingIntent);
//        mBuilder.addAction(R.drawable.img_profile_default, mContext.getString(R.string.action_save), savePendingIntent);

        NotificationCompat.BigTextStyle bigtext =  new NotificationCompat.BigTextStyle();
        bigtext.setBigContentTitle(title);
        bigtext.bigText(body);
        mBuilder.setStyle(bigtext);


        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID_WELCOME, mBuilder.build());
    }

    public static void onWelcomeNotiClick(Context context, Intent intent){
        try {
            if(intent != null) {
                String btnType = intent.getStringExtra(KcpNotificationManager.INTENT_EXTRA_KEY_BTN);
                if(btnType != null && btnType.equals(NOTIBTN_SAVE)) {
                    context.startActivity(new Intent(context, WelcomeMessage.class));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelNotification(Context context, int notificationId){
        NotificationManager mNM = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(notificationId);

    }

}
