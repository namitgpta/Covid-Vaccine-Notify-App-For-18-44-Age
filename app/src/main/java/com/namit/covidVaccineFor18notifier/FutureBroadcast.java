package com.namit.covidVaccineFor18notifier;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FutureBroadcast extends BroadcastReceiver {

    public int days_to_fetch = 15;
    public int days_fetched = 0;
    private String pincode_here;
    public String logTAG = "Vaccine";

    private Context context_global;

    public NotificationManagerCompat notificationManager;
    public NotificationCompat.Builder builder;

    //public JSONArray json_arr_18 = new JSONArray();
    public int json_number = 0;
    RequestQueue queue;

    @Override
    public void onReceive(Context context, Intent intent) {
        //this will be executed at selected interval
        context_global = context;
        json_number = 0;

        SharedPreferences prefs = context.getSharedPreferences("com.namit.vaccine18notifier", Context.MODE_PRIVATE);
        pincode_here = prefs.getString("stored_pincode", "");

        //Toast.makeText(context, "Awesome!", Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

        builder = new NotificationCompat.Builder(context, "notifyVaccine")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder - Vaccine for 18+ Available")
                .setContentText("Hello, Vaccine for 18+ Available Now. Please Check the App")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        notificationManager = NotificationManagerCompat.from(context);

        availableFor_18_OrNot();


        //notificationManager.notify(200, builder.build());
    }

    public void availableFor_18_OrNot(){
        Date dt = new Date();

        days_fetched = 0;

        for (int i = 0; i < days_to_fetch; i++) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

            String[] date_arr = format.format(dt).split("/");

            String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode="
                    + pincode_here + "&date=" + date_arr[0] + "-" +
                    date_arr[1] + "-" + date_arr[2];

            int finalI = i;

            Log.d(logTAG, url);

            Thread t1 = new Thread(() -> load_slots(url, finalI));
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dt = DateUtil.addDays(dt, 1);
        }

    }


    public void load_slots(String url, int ii) {
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(context_global);

        Log.d(logTAG, "Started for " + ii);

        // Request a string response from the provided URL.


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    //Log.d("json success 1", "json api success");
                    //JSON Request Success
                    try {
                        JSONArray results_arr = response.getJSONArray("sessions");


                        for (int i = 0; i < results_arr.length(); i++) {
                            JSONObject obj = results_arr.getJSONObject(i);
                            if (obj.getString("min_age_limit").equals("45")) {
                                json_number++;
                            }

                        }

                        days_fetched++;

                        //System.out.println("Done for loop" + ii);
                        recyclerProcess(results_arr, ii);

                    } catch (Exception e) {
                        Log.d("json error", "json api error");
                        e.printStackTrace();
                    }
                }, error -> VolleyLog.d("json connect fail", "json api fail"));
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }


    public void recyclerProcess(JSONArray results_arr, int ii) {
        //setup adapter
        if (results_arr == null)
            Log.d("results_arr json fail", "results_arr json fail");
        else {
            if (days_fetched == days_to_fetch) {
                after_slots_fetch();
            }

            Log.d("results_arr fetched", "success for " + ii);

        }
    }

    public void after_slots_fetch() {

        if(json_number > 0) {
            notificationManager.notify(200, builder.build());
            Log.d(logTAG, "Fetched: " + json_number);
        }

        queue = null;

        //while_loop = false;

        notificationManager = null;
        builder = null;
    }

}