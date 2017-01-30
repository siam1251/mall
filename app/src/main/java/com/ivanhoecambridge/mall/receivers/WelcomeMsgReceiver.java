package com.ivanhoecambridge.mall.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.managers.KcpNotificationManager;


/**
 * Created by Kay on 2016-10-12.
 */

public class WelcomeMsgReceiver extends BroadcastReceiver {

    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            return null;
        }
        String className = launchIntent.getComponent().getClassName();
        Class<? extends Activity> cls = null;
        try {
            cls = (Class <? extends Activity>)Class.forName(className);
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        return cls;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String btnType = intent.getStringExtra(KcpNotificationManager.INTENT_EXTRA_KEY_BTN);
        if(btnType != null) {
            if(btnType.equals(KcpNotificationManager.NOTIBTN_SAVE)) {
                Intent activityIntent = new Intent(context, MainActivity.class);
                activityIntent.putExtra(KcpNotificationManager.INTENT_EXTRA_KEY_BTN, KcpNotificationManager.NOTIBTN_SAVE);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(activityIntent);
            } else if(btnType.equals(KcpNotificationManager.NOTIBTN_DISMISS)) {

            }

            int notificationId = intent.getIntExtra(KcpNotificationManager.INTENT_EXTRA_KEY_WELCOME_NOTI_ID, 0);
            KcpNotificationManager.cancelNotification(context, notificationId);
        }
    }
}
