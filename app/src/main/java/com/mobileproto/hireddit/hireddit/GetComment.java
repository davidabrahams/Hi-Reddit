package com.mobileproto.hireddit.hireddit;
import android.content.Context;
import android.net.Uri;
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
    public void commentSearch(String searchQuery, final Context context, final CommentCallback callback) {

        String query = searchQuery.replaceAll(" ", "+");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.pushshift.io")
                .appendPath("reddit")
                .appendPath("search")
                .appendQueryParameter("q",query)
                .appendQueryParameter("fields","body");
        String Url = builder.build().toString();
        Log.d("redditapicall", Url);
        JsonObjectRequest getRequest = new JsonObjectRequest(
            Request.Method.GET,
            Url,
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
                        Toast.makeText(context, "No comments available", Toast.LENGTH_SHORT).show();
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
        getRequest.setRetryPolicy(new DefaultRetryPolicy( //changes Volley settings
                10000, //earlier I was having issues with this api taking more than the 5 seconds it takes Volley to time out
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, //now the time is 10 seconds, the api seems faster now
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

}
