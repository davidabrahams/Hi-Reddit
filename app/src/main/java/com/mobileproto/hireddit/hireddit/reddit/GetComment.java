package com.mobileproto.hireddit.hireddit.reddit;
import android.content.Context;
import android.net.Uri;
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

import java.util.ArrayList;


/**
 * Created by lwilcox on 11/4/2015.
 */

public class GetComment implements Response.Listener<JSONObject>, Response.ErrorListener {
    private RequestQueue queue;
    private CommentCallback callback;
    private Context context;
    public GetComment(Context context, CommentCallback callback) {

        queue = Volley.newRequestQueue(context);
        this.callback = callback;
        this.context = context;
    }


    public void commentSearch(String searchQuery) {

        // I think the Uri Builder does this for you
        //String searchQuery = searchQuery.replaceAll(" ", "+");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.pushshift.io")
                .appendPath("com/mobileproto/hireddit/hireddit/reddit")
                .appendPath("search")
                .appendQueryParameter("q", searchQuery)
                .appendQueryParameter("fields","body");
        String Url = builder.build().toString();
        Log.d("redditapicall", Url);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Url,
                new JSONObject(), this, this);

        getRequest.setRetryPolicy(new DefaultRetryPolicy( //changes Volley settings
                10000, //earlier I was having issues with this api taking more than the 5 seconds it takes Volley to time out
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, //now the time is 10 seconds, the api seems faster now
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);
    }

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

    @Override
    public void onErrorResponse(VolleyError error)  {
        if (error.networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                Log.d("Error","timeout");
            }
        }
        Log.e("Error", "volleyfail");
    }

}
