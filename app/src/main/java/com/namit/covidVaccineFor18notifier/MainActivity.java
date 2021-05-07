package com.namit.covidVaccineFor18notifier;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static TextView selectedDate;
    EditText pincode;
    Button show_slots;
    //Button future_alerts, stop_alerts;
    SwitchCompat switch_alert;

    public static String _pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();




        selectedDate = findViewById(R.id.selected_date);
        pincode = findViewById(R.id.pincode);
        show_slots = findViewById(R.id.show_slots);
        switch_alert = findViewById(R.id.switch_alert);

        SharedPreferences prefs = getSharedPreferences("com.namit.vaccine18notifier", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("service_status", false);
        String stored_pincode = prefs.getString("stored_pincode", "");
        _pincode = stored_pincode;

        pincode.setText(stored_pincode);

        if(switchState){
            //Do your work for switch is selected on
            switch_alert.setChecked(true);
            switch_alert.setText("ON");
        } else {
            //Code for switch off
            switch_alert.setChecked(false);
            switch_alert.setText("OFF");
        }

        switch_alert.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("com.namit.vaccine18notifier", MODE_PRIVATE)
                    .edit();
            editor.putBoolean("service_status", switch_alert.isChecked());
            editor.commit();

            if(switch_alert.isChecked()){

                _pincode = pincode.getText().toString().trim();
                if(_pincode.equals("")){
                    switch_alert.setChecked(false);
                    Toast.makeText(MainActivity.this, "Please Enter Pincode", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(MainActivity.this, _pincode, Toast.LENGTH_SHORT).show();

                    switch_alert.setText("ON");

                    AlarmHandler alarmHandler = new AlarmHandler(this);
                    //cancel the previous scheduled alarms
                    alarmHandler.cancelAlarmManager();
                    //set the new alarm after 10 seconds
                    alarmHandler.setAlarmManager();



                    Toast.makeText(this, "Alerts Enabled!", Toast.LENGTH_SHORT).show();
                }


//            Intent intent = new Intent(MainActivity.this, FutureBroadcast.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//            long timeAtButtonClick = System.currentTimeMillis();
//
//            long tenSecondsInMillis = 1000 * 10;
//
//            alarmManager.set(AlarmManager.RTC_WAKEUP,
//                    timeAtButtonClick + tenSecondsInMillis,
//                    pendingIntent);

            }else{
                switch_alert.setText("OFF");

                AlarmHandler alarmHandler = new AlarmHandler(this);
                //cancel the previous scheduled alarms
                alarmHandler.cancelAlarmManager();

                Toast.makeText(this, "Alerts Stopped!", Toast.LENGTH_SHORT).show();

            }

        });

        show_slots.setOnClickListener(v -> {
            _pincode = pincode.getText().toString().trim();
            if(_pincode.equals("")){
                Toast.makeText(MainActivity.this, "Please Enter Pincode", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(MainActivity.this, _pincode, Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = getSharedPreferences("com.namit.vaccine18notifier", MODE_PRIVATE)
                        .edit();
                editor.putString("stored_pincode", _pincode);
                editor.commit();

                startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "VaccineReminderChannel";
            String description = "Channel for Vaccine Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =  new NotificationChannel("notifyVaccine", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            String text = "Selected Date: " + day + "/" + (month+1) + "/" + year;
            selectedDate.setText(text);

//            _day = day;
//            _month = month + 1;
//            _year = year;
        }
    }
}