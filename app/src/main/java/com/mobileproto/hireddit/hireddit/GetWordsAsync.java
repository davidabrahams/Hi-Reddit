package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

import io.indico.Indico;
import io.indico.enums.TextTag;
import io.indico.results.IndicoResult;
import io.indico.api.Api;
import io.indico.network.IndicoCallback;
import io.indico.clients.TextApi;
import io.indico.utils.IndicoException;;

/**
 * Created by lwilcox on 11/5/2015.
 */
public class GetWordsAsync extends AsyncTask<Void, Void, ArrayList<String>>{
    private String spokenString;
    private String importantWords;
    private Context context;
    public ArrayList<String> wordList = new ArrayList<>();
    public ArrayList<String> allComments;
    public String postComment;
    public TextView commentText;
    public GetWordsAsync(String spokenString, String importantWords, Context context, TextView commentText){
        this.spokenString = spokenString;
        this.importantWords = importantWords;
        this.context = context;
        this.commentText = commentText;
    }
    String indicoApiKey = "7a8f16edc7a58c8a7773ba95c6d2241b";
    Indico indico = Indico.init(context, indicoApiKey, null);

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        try {

            //final ArrayList<String> wordList = new ArrayList<String>();
            indico.keywords.predict(spokenString, new IndicoCallback<IndicoResult>() {
                @Override
                public void handle(IndicoResult result) throws IndicoException {
                    Log.i("Indico Keywords", "keywords: " + result.getKeywords());
                    if (result.getKeywords() != null) {
                        wordList.add(result.getKeywords().keySet().toString());
                    }
                }
            });
            Log.d("wordlist", wordList.toString());
            try {
                Thread.sleep(1500);
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            return wordList;
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        Log.d("inpost","true");
        super.onPostExecute(result);
        if (! result.isEmpty()) {
            importantWords = result.toString();
            importantWords = importantWords.replace(",", "").replace("[", "").replace("]", "");
            GetComment getComment = new GetComment(context);
            getComment.commentSearch(importantWords, new CommentCallback() {
                @Override
                public void callback(ArrayList<String> commentList) {
                    allComments = commentList;
                    ChooseComment chooseComment = new ChooseComment();
                    postComment = chooseComment.pickComment(allComments);
                    Log.i("comment", postComment);
                    commentText.setText(postComment);
                }
            });
        }
    }
}