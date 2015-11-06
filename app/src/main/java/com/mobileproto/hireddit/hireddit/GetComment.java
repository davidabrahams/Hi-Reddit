package com.mobileproto.hireddit.hireddit;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import io.indico.Indico;
import io.indico.enums.TextTag;
import io.indico.results.IndicoResult;
import io.indico.api.Api;
import io.indico.network.IndicoCallback;
import io.indico.clients.TextApi;
import io.indico.utils.IndicoException;;
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
                        Log.e("Error", error.getMessage());
                    }
                }
        );

        queue.add(getRequest);
    }

}
//public class GetComment extends AppCompatActivity{
//
//    String indicoApiKey = "A7a8f16edc7a58c8a7773ba95c6d2241bA";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Indico.init(this, indicoApiKey, null);
//
//        try {
//            Indico.sentiment.predict("indico is so easy to use!", new IndicoCallback<IndicoResult>() {
//                @Override public void handle(IndicoResult result) throws IndicoException {
//                    Log.i("Indico Sentiment", "sentiment of: " + result.getSentiment());
//                }
//            });
//        } catch (IOException | IndicoException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
