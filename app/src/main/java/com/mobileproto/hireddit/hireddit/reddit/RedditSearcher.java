package com.mobileproto.hireddit.hireddit.reddit;

import android.content.Context;
import android.content.res.AssetManager;
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
import com.google.common.io.CharStreams;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import io.indico.Indico;
import io.indico.network.IndicoCallback;
import io.indico.results.IndicoResult;
import io.indico.utils.IndicoException;

/**
 * RedditSearcher: Takes in user's spoken string and displays the best comment on the UI
 * Uses Indico to get key words
 * Calls GetComment to get Reddit comments with key words
 * Calls ChooseComment to get the best comment
 */
public class RedditSearcher implements Response.Listener<JSONObject>, Response.ErrorListener {

    private String spokenString;
    private static final String DEBUG_TAG = "RedditSearcher Debug";
    private static final String ERROR_TAG = "RedditSearcher Error";

    private Context context;
    private Indico indico;
    private CommentCallback myCommentCallback;
    private RequestQueue queue;

    private IndicoCallback<IndicoResult> indicoCallback = new IndicoCallback<IndicoResult>() {
        @Override
        public void handle(IndicoResult result) throws IndicoException {
            getCommentFromKeywords(result.getKeywords().keySet());
        }
    };

    public RedditSearcher(CommentCallback myCommentCallback, String spokenString, Context context) {
        this.myCommentCallback = myCommentCallback;
        this.spokenString = spokenString;
        this.context = context;
        String indicoApiKey = getApi(context);
        this.indico = Indico.init(context, indicoApiKey, null);
        this.queue = Volley.newRequestQueue(context);
    }

    public static String getApi(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream apiKey = assetManager.open("indicoapitxt.txt");
            String apiKeyString = CharStreams.toString(new InputStreamReader(apiKey, "UTF-8"));
            return apiKeyString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getRedditComment() {
        try {
            indico.keywords.predict(spokenString, indicoCallback);
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
    }

    private void getCommentFromKeywords(Set<String> keywords) {
        String importantWords = StringUtils.join(keywords, " ");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.pushshift.io")
                .appendPath("reddit")
                .appendPath("search")
                .appendQueryParameter("q", importantWords)
                .appendQueryParameter("fields", "body");
        String Url = builder.build().toString();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Url,
                new JSONObject(), this, this);

        getRequest.setRetryPolicy(new DefaultRetryPolicy( //changes Volley settings
                10000, //earlier I was having issues with this api taking more than the 5 seconds it takes Volley to time out
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, //now the time is 10 seconds, the api seems faster now
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);
    }

    public void filterComment(ArrayList<String> allComments) {
        for (int i = 0; i < allComments.size(); i++) {
            if (allComments.get(i).length() > 300 || allComments.get(i).toLowerCase().contains("http")) {
                allComments.remove(i);
            }
        }
    }

    public String pickComment(ArrayList<String> allComments) {
        if (allComments.size() == 0) {
            return null;
        } else {
            Random mRandom = new Random();
            int index = mRandom.nextInt(allComments.size());
            filterComment(allComments);
            return allComments.get(index);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<String> allComments = new ArrayList<>();
        try {
            JSONArray items = response.getJSONArray("data");
            for (int i = 0; i < items.length(); i++) {
                JSONObject body = items.getJSONObject(i);
                String comment = body.getString("body");
                allComments.add(comment);
            }
            String postComment = pickComment(allComments);
            myCommentCallback.commentCallback(postComment);
        } catch (JSONException e) {
            Log.e(ERROR_TAG, "JSON Exception");
            Toast.makeText(context, "No comments available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(ERROR_TAG, "Volley experienced an error");
        if (error.networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                Log.e(ERROR_TAG, "A timeout error occurred");
            }
        }
    }

    public interface CommentCallback {
        void commentCallback(String comment);
    }


}