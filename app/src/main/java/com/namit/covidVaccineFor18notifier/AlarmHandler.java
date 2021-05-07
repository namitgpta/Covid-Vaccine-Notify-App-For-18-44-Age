package com.namit.covidVaccineFor18notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmHandler {
    private Context context;

    public AlarmHandler(Context context){
        this.context = context;
    }

    //this will activate the alarm
    public void setAlarmManager(){
        Intent intent = new Intent(context, FutureBroadcast.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null) {

            long triggerAfter = 10*1000; //this will trigger the service after 10 seconds
            long triggerEvery = 60*60*1000; //this will repeat it every hour after that
            am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAfter, triggerEvery, sender);
        }
    }

    //this will cancel the alarm
    public void cancelAlarmManager(){
        Intent intent = new Intent(context, FutureBroadcast.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null) {
            am.cancel(sender);
        }
    }

}
