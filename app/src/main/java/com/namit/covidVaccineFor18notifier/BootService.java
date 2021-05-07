package com.namit.covidVaccineFor18notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BootService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //code to run after boot
            Toast.makeText(context, "Boot completed!", Toast.LENGTH_SHORT).show();

            SharedPreferences prefs = context.getSharedPreferences("com.namit.vaccine18notifier", Context.MODE_PRIVATE);
            String pincode_here = prefs.getString("stored_pincode", "");

            if(!pincode_here.equals("")){

                AlarmHandler alarmHandler = new AlarmHandler(context);
                //cancel the previous scheduled alarms
                alarmHandler.cancelAlarmManager();
                //set the new alarm after 10 seconds
                alarmHandler.setAlarmManager();



                //Toast.makeText(this, "Alerts Enabled!", Toast.LENGTH_SHORT).show();
            }




//            Intent intent1 = new Intent(context, MainActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
        }
    }
}
