package com.namit.covidVaccineFor18notifier;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity2 extends AppCompatActivity {

    public int days_to_fetch = 30;
    public int days_fetched = 0;

    private int jsonApiFetchError_count = 0;

    public JSONArray json_arr_18 = new JSONArray();

    private RecyclerView recyclerView;
    slots_RecyclerViewAdapter recyclerViewAdapter;
    RequestQueue queue;
    private ProgressBar progressBar;
    private TextView no_slots, total_slot_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recyclerView_slots);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar_slots);
        progressBar.setVisibility(View.VISIBLE);

        no_slots = findViewById(R.id.no_slots);
        total_slot_results = findViewById(R.id.total_slot_results);

        jsonApiFetchError_count = 0;

        Date dt = new Date();

        days_fetched = 0;

        for (int i = 0; i < days_to_fetch; i++) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

            String[] date_arr = format.format(dt).split("/");

            String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode="
                    + MainActivity._pincode + "&date=" + date_arr[0] + "-" +
                    date_arr[1] + "-" + date_arr[2];

            int finalI = i;
            Thread t1 = new Thread(() -> {
                load_slots(url, finalI);
            });
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
        queue = Volley.newRequestQueue(this);

        System.out.println("Started for " + ii);

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
                                json_arr_18.put(obj);
                            }

                        }

                        days_fetched++;

                        //System.out.println("Done for loop" + ii);
                        recyclerProcess(results_arr, ii);

                        //progressBar.setVisibility(View.INVISIBLE);

                    } catch (Exception e) {
                        Log.d("json error", "json api error");
                        e.printStackTrace();
                    }
                }, error -> {
            VolleyLog.d("json connect fail", "json api fail");
            jsonApiFetchErrorToastOnce();
        });
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
        progressBar.setVisibility(View.INVISIBLE);

        if (json_arr_18.length() == 0) {
            no_slots.setText(R.string.no_slots_available);
        } else {
            total_slot_results.setText(getString(R.string.total_results) + " " + json_arr_18.length());
            recyclerViewAdapter = new slots_RecyclerViewAdapter(this, json_arr_18);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public void jsonApiFetchErrorToastOnce(){
        if(jsonApiFetchError_count == 0){
            Toast.makeText(this, "Json API error\nPlease try after some time....", Toast.LENGTH_LONG).show();
            jsonApiFetchError_count++;
        }
    }
}