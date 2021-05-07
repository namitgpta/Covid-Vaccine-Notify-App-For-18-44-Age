package com.namit.covidVaccineFor18notifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class slots_RecyclerViewAdapter extends RecyclerView.Adapter<slots_RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private JSONArray results;

    public slots_RecyclerViewAdapter(Context context, JSONArray results){
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slot_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        JSONObject obj = null;
        try {
            obj = results.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalObj = obj;

        if(obj!=null){
            try {
                holder.name.setText(obj.getString("name"));
                holder.district_name.setText(obj.getString("district_name"));
                holder.block_name.setText(obj.getString("block_name"));
                holder.fee_type.setText(obj.getString("fee_type") + ": ");
                holder.fee.setText("Rs." + obj.getString("fee"));
                holder.available_capacity.setText("Available: " + obj.getString("available_capacity") + " Doses");
                holder.min_age_limit.setText("Age: " + obj.getString("min_age_limit") + "+");
                holder.vaccine.setText(obj.getString("vaccine"));
                holder.date.setText("Date: " + obj.getString("date"));

//                String date_raw = obj.getString("updated_date");
//                holder.date.setText(date_raw.substring(0, 10).concat("  ")
//                        .concat(date_raw.substring(11, 16)));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public int getItemCount() {
        return results.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, district_name, block_name, fee_type, fee, available_capacity, min_age_limit, vaccine, date;
        //public ImageView imageView;
        //public Button readMore;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.name);
            district_name = itemView.findViewById(R.id.district_name);
            block_name = itemView.findViewById(R.id.block_name);
            fee_type = itemView.findViewById(R.id.fee_type);
            fee = itemView.findViewById(R.id.fee);
            available_capacity = itemView.findViewById(R.id.available_capacity);
            min_age_limit = itemView.findViewById(R.id.min_age_limit);
            vaccine = itemView.findViewById(R.id.vaccine);
            date = itemView.findViewById(R.id.date);

        }
    }

}
