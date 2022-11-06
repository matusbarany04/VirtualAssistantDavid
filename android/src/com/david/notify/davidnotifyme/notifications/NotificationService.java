package com.david.notify.davidnotifyme.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;


import com.david.notify.davidnotifyme.david.David;

import java.util.Calendar;
import java.util.concurrent.Executor;

/*
*
* this class is deprecated
*
* */

public class NotificationService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;



    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("toast", "service starting");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void scheduleNewService(){
        int timeInMinutes = David.findNearestNotificationChange(null);
        int hours = timeInMinutes / 60;
        int remainingMinutes = timeInMinutes - (hours * 60);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, remainingMinutes+ 40);
        calendar.set(Calendar.SECOND, 30);

        Calendar now = Calendar.getInstance();
        int calendarComparison = now.compareTo(calendar);
        if (calendarComparison == 1){
            calendar.add(Calendar.DATE, 1);
        }


        Intent my_intent = new Intent(this, BroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
//                new Intent(this, BroadCastReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);

//        Log.d("alarm manager" , alarmManager.getNextAlarmClock().toString());
        /*
        Intent intent1 = new Intent(context, AlarmReceiver.class);
    final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent1, 0);


    final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
    */

    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        Context context;
        public ServiceHandler(Looper looper, Context context) {
            super(looper);
            this.context = context;

        }



        @Override
        public void handleMessage(Message msg) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }

            Executor executor = runnable -> new Thread(runnable).start();
          //  David david = new David(context,executor);

           // DavidNotifications.updateNotification(context, David.currentTimeInStringWithSeconds(), david.ziskajDalsiuHodinuEdupage(null));

           // scheduleNewService();

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }


}