package com.mobileproto.hireddit.hireddit.reddit;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import com.mobileproto.hireddit.hireddit.R;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    private static final String DEBUG_TAG = "RedditSearcher Debug";
    private static final String ERROR_TAG = "RedditSearcher Error";
    private String spokenString;
    private Context context;
    private Indico indico;
    private CommentCallback myCommentCallback;
    private RequestQueue queue;

    private IndicoCallback<IndicoResult> indicoCallback = new IndicoCallback<IndicoResult>() {
        @Override
        public void handle(IndicoResult result) throws IndicoException {
            List<String> indicoWords = new ArrayList<>(result.getKeywords().keySet());
            if (indicoWords.size() != 0) {
                getCommentFromKeywords(indicoWords);
            } else {
                String[] spokenArray = spokenString.split(" ");
                List<String> spokenSet = Arrays.asList(spokenArray);
                getCommentFromKeywords(spokenSet);
            }
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

    private static String getApi(Context context) {
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

    private void getCommentFromKeywords(List<String> keywords) {
        String importantWords = StringUtils.join(keywords, " ");
        String fields = "body,link_id,id";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.pushshift.io")
                .appendPath("reddit")
                .appendPath("search")
                .appendQueryParameter("q", importantWords)
                .appendQueryParameter("fields", fields)
                .appendQueryParameter("limit", "1000");

        String Url = builder.build().toString();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Url,
                new JSONObject(), this, this);
        getRequest.setRetryPolicy(new DefaultRetryPolicy( //changes Volley settings
                10000, //earlier I was having issues with this api taking more than the 5 seconds it takes Volley to time out
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, //now the time is 10 seconds, the api seems faster now
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);
    }

    private void filterComment(ArrayList<String[]> allComments) {
        for (int i = 0; i < allComments.size(); i++) {
            String comment = allComments.get(i)[0];
            if (comment.length() > 300 || comment.toLowerCase().contains("http")) {
                allComments.remove(i);
                i--;
            }
        }
    }

    private void pickComment(ArrayList<String[]> allComments) {

        filterComment(allComments);

        if (allComments.size() <= 0) {
            myCommentCallback.commentCallback(null, null);
        } else {
            //  search the last 10 comments for upvotes. This is because the most recent comments
            // often don't have upvote info.

            List<String[]> commentRange = allComments.subList(Math.max(allComments.size() -
                            myCommentCallback.getCommentsToSearch(), 0), allComments.size());

            if (commentRange.size() == 1)
                myCommentCallback.commentCallback(commentRange.get(0)[0], commentRange.get(0)[1]);
            else {
                HighestUpvoteCommentAsync t = new HighestUpvoteCommentAsync(myCommentCallback,
                        commentRange);
                t.searchComments();
            }
        }
    }

    private String getRedditUrl(String linkId, String id, int context) {
        return "https://www.reddit.com/comments/" + linkId + "/_/" + id + "?context=" +
                Integer.toString(context);
    }

    @Override
    public void onResponse(JSONObject response) {

        ArrayList<String[]> allComments = new ArrayList<>();
        Resources res = context.getResources();
        try {
            JSONArray items = response.getJSONArray("data");
            for (int i = 0; i < items.length(); i++) {
                JSONObject body = items.getJSONObject(i);
                String comment = body.getString("body");
                //removing first 3 removes t1_
                String linkId = body.getString("link_id").substring(3);
                String commentId = body.getString("id");

                String commentLink = getRedditUrl(linkId, commentId, 0);
                String[] eachLinkInfo = {comment, commentLink};
                allComments.add(eachLinkInfo);
            }
            pickComment(allComments);
        } catch (JSONException e) {
            Log.e(ERROR_TAG, "JSON Exception");
            Toast.makeText(context, res.getString(R.string.no_comments), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(ERROR_TAG, "Volley experienced an error");

        Resources res = context.getResources();
        String TOO_GENERAL = res.getString(R.string.general);
        myCommentCallback.commentCallback(TOO_GENERAL, null);

        if (error.networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                Log.e(ERROR_TAG, "A timeout error occurred");
            }
        }
    }

    public interface CommentCallback {
        int getCommentsToSearch();
        void commentCallback(String comment, String link);
    }

}