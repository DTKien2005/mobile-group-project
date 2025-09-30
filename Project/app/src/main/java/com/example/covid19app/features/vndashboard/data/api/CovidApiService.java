package com.example.covid19app.features.vndashboard.data.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid19app.features.vndashboard.data.model.CovidStats;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class CovidApiService {

    private static final String URL = "https://disease.sh/v3/covid-19/countries/VN";

    // Callback interface for Activity take the data
    public interface CovidCallback {
        void onSuccess(CovidStats stats);
        void onError(String errorMessage);
    }

    // Function that call API
    public static void fetchCovidStats(Context context, final CovidCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // cách 1: parse JSON
                            JSONObject obj = new JSONObject(response);
                            String country = obj.getString("country");
                            int cases = obj.getInt("cases");
                            int deaths = obj.getInt("deaths");

                            // Cách 2: Dùng Gson map sang CovidStats
                            CovidStats stats = new Gson().fromJson(response, CovidStats.class);

                            callback.onSuccess(stats);

                        } catch (JSONException e) {
                            callback.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }
}
