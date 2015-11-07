package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
    //public AsyncIndicoResponse delegate = null;
    public GetWordsAsync(ArrayList<String> allComments, String spokenString, String importantWords, Context context){
        this.allComments = allComments;
        this.spokenString = spokenString;
        this.importantWords = importantWords;
        this.context = context;
    }
    String indicoApiKey = "7a8f16edc7a58c8a7773ba95c6d2241b";
    Indico indico = Indico.init(context, indicoApiKey, null);
    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        try {
            //final ArrayList<String> wordList = new ArrayList<String>();
            indico.keywords.predict("indico is so easy to use!", new IndicoCallback<IndicoResult>() {
                @Override
                public void handle(IndicoResult result) throws IndicoException {
                    //ArrayList<String> wordList = new ArrayList<String>();
                    Log.i("Indico Sentiment", "sentiment of: " + result.getKeywords());
                    if (result.getKeywords() != null) {
                        wordList.add(result.getKeywords().toString());
                    }
                }
                //return wordList;
            });
            return wordList;
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
        importantWords = result.toString();
        GetComment getComment = new GetComment(context);
        getComment.commentSearch(importantWords, new CommentCallback() {
            @Override
            public void callback(ArrayList<String> commentList) {
                allComments = commentList;
                //delegate.processFinish(allComments);
            }
        });
    }

    public String returnComment(){
        ChooseComment chooseComment = new ChooseComment();
        String comment = chooseComment.pickComment(allComments);
        return comment;
    }
}

//public class GetWords extends AppCompatActivity {
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
