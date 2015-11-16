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

;

/**
 * Created by lwilcox on 11/5/2015.
 */
public class GetWordsAsync extends AsyncTask<Void, Void, ArrayList<String>>
{
    private String spokenString;
    private String importantWords;
    private Context context;
    public ArrayList<String> wordList = new ArrayList<>();
    public ArrayList<String> allComments;
    public String postComment;
    public TextView commentText;
    public Indico indico;


    public GetWordsAsync(String spokenString, String importantWords, Context context, TextView commentText)
    {

        this.spokenString = spokenString;
        this.importantWords = importantWords;
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
            Log.d("apikey", apiKeyString);
            return apiKeyString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params)
    {
        try {
            //final ArrayList<String> wordList = new ArrayList<String>();
            indico.keywords.predict(spokenString, new IndicoCallback<IndicoResult>()
            {
                @Override
                public void handle(IndicoResult result) throws IndicoException
                {
                    Log.i("Indico Keywords", "keywords: " + result.getKeywords());
                    if (result.getKeywords() != null) {
                        wordList.add(result.getKeywords().keySet().toString());
                    }
                }
            });
            Log.d("wordlist", wordList.toString());
            while (wordList.isEmpty()) {
                Log.d("wating", "true");
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            Log.d("wordList", wordList.toString());
            return wordList;
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        Log.d("inpost", "true");
        super.onPostExecute(result);
        if (!result.isEmpty()) {
            importantWords = result.toString();
            importantWords = importantWords.replace(",", "").replace("[", "").replace("]", "");
            GetComment getComment = new GetComment(context);
            getComment.commentSearch(importantWords, new CommentCallback()
            {
                @Override
                public void callback(ArrayList<String> commentList)
                {
                    allComments = commentList;
                    ChooseComment chooseComment = new ChooseComment();
                    postComment = chooseComment.pickComment(allComments);
                    Log.i("comment", postComment);
                    commentText.setText(postComment);
                    MainActivity.speech.speak(postComment);
                }
            });
        }
    }
}