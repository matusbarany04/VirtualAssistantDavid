package com.david.notify.davidnotifyme.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;


import static android.content.Context.NOTIFICATION_SERVICE;

import com.david.notify.R;
import com.david.notify.davidnotifyme.david.DavidClockUtils;

public class DavidNotifications {

    public static final int LESSON_ONGOING_NOTIFICATION = 0;
    public static final int MORNING_NOTIFICATION = 1;
    public static final int REMIND_NOTIFICATION = 2;

    public static void showNotificationMessage(Context context, String header, String message, int id) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "messages");

        notificationBuilder.setContentTitle(header)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);

        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        String channelId = "messages";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "messages",
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        notificationBuilder.setChannelId(channelId);

        notificationManager.notify(id, notification);
    }


    public static void updateNotification(Context context, String header, String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "messages");

        notificationBuilder.setContentTitle(header)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup("My group")
                .setGroupSummary(false)
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN);

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        String channelId = "messages";
        NotificationChannel channel = new NotificationChannel(channelId, "messages",
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        notificationBuilder.setChannelId(channelId);

        //Service service = (Service) context;
        //service.startForeground(0, notification);
        notificationManager.notify(LESSON_ONGOING_NOTIFICATION, notification);
    }

    public static void stopNotification(Context context, int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public static void planMorningNotification(Context context, String time) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(time == null) time = pref.getString("time_notify_morning", "7:00");
        boolean show = pref.getBoolean("show_notify_morning", false);

        Intent notificationIntent = new Intent(context, BroadCastReceiver.class);
        Bundle extras = new Bundle();
        extras.putString("notificationType", "morningMessage");
        notificationIntent.putExtras(extras);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 100, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        if(show && time != null) {

            int millis =  DavidClockUtils.millisFromNowTill(time);
            if(millis < 0) millis += AlarmManager.INTERVAL_DAY;


            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millis, pendingIntent);

            Log.d("planning", millis + "(" + time + ")");
            Log.d("millis", System.currentTimeMillis() + "");
        } else {
            Log.d("notify", "cancel");
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
            alarmManager.cancel(sender);
        }
    }
}