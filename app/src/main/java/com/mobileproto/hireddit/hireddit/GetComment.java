package com.mobileproto.hireddit.hireddit;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by lwilcox on 11/4/2015.
 */

public class GetComment {
    public RequestQueue queue;
    public GetComment(Context context) {
        queue = Volley.newRequestQueue(context);
    }
    public void commentSearch(String searchQuery, final CommentCallback callback) {
        String query = searchQuery.replaceAll(" ", "+");
        String URL = "https://api.pushshift.io/reddit/search?q=%22";
        URL = URL + query + "%22~5&fields=body";
        Log.d("redditapicall", URL);
        JsonObjectRequest getRequest = new JsonObjectRequest(
            Request.Method.GET,
            URL,
            new JSONObject(),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response){
                    ArrayList<String> allComments = new ArrayList<String>();
                    try {
                        JSONArray items = response.getJSONArray("data");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject body = items.getJSONObject(i);
                            String comment = body.getString("body");
                            allComments.add(comment);
                        }
                        callback.callback(allComments);
                    } catch (Exception e)
                    {
                        Log.d("Failure", "No comments available");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)  {
                   //Log.e("Error", error.getMessage());
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            Log.d("Error","timeout");
                        }
                    }
                    Log.e("Error", "volleyfail");
                }
            }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

}
