package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.mobileproto.hireddit.hireddit.visuals.MainActivity;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import io.indico.Indico;
import io.indico.network.IndicoCallback;
import io.indico.results.IndicoResult;
import io.indico.utils.IndicoException;

/**
 * GetWordsAsync: Takes in user's spoken string and displays the best comment on the UI
 * Uses Indico to get key words
 * Calls GetComment to get Reddit comments with key words
 * Calls ChooseComment to get the best comment
 */
public class GetWordsAsync extends AsyncTask<Void, Void, ArrayList<String>>
{
    private String spokenString;
    private String importantWords;
    private static final String DEBUG_TAG = "GetWordsAsync Debug";
    private Context context;
    public ArrayList<String> wordList = new ArrayList<>();
    public ArrayList<String> allComments;
    public String postComment;
    public TextView commentText;
    public Indico indico;

    public GetWordsAsync(String spokenString, Context context, TextView commentText)
    {

        this.spokenString = spokenString;
        this.context = context;
        this.commentText = commentText;
        String indicoApiKey = getApi(context);
        this.indico = Indico.init(context, indicoApiKey, null);
    }

    public static String getApi(Context context)
    {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream apikey = assetManager.open("indicoapitxt.txt");
            String apiKeyString = CharStreams.toString(new InputStreamReader(apikey, "UTF-8"));
            return apiKeyString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        try {
            indico.keywords.predict(spokenString, new IndicoCallback<IndicoResult>() {
                @Override
                public void handle(IndicoResult result) throws IndicoException {
                    Log.i(DEBUG_TAG, "keywords: " + result.getKeywords());
                    if (result.getKeywords() != null) {
                        wordList.add(result.getKeywords().keySet().toString());
                    }
                }
            });
            while (wordList.isEmpty()) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            Log.d(DEBUG_TAG, "wordlist:" + wordList.toString());
            return wordList;
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        super.onPostExecute(result);
        if (!result.isEmpty()) {
            importantWords = result.toString();
            importantWords = importantWords.replace(",", "").replace("[", "").replace("]", ""); // do not change to .replaceAll("[^A-Za-z0-9]", "");
            GetComment getComment = new GetComment(context);
            getComment.commentSearch(importantWords, context, new CommentCallback()
            {
                @Override
                public void callback(ArrayList<String> commentList)
                {
                    allComments = commentList;
                    ChooseComment chooseComment = new ChooseComment();
                    postComment = chooseComment.pickComment(allComments);
                    Log.d(DEBUG_TAG, "postComment:" + postComment);
                    commentText.setText(postComment);
                    MainActivity.speech.speak(postComment);
                }
            });
        }
    }
}