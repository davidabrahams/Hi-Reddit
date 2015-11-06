package com.mobileproto.hireddit.hireddit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
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
public class GetWords extends AsyncTask<Void, Void, ArrayList<String>>{
    String indicoApiKey = "A7a8f16edc7a58c8a7773ba95c6d2241bA";

    Indico Indico = new Indico();
    //Indico.init(this, indicoApiKey, null);
    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        try {
            final ArrayList<String> haha = new ArrayList<String>();
            Indico.sentiment.predict("indico is so easy to use!", new IndicoCallback<IndicoResult>() {
                @Override public void handle(IndicoResult result) throws IndicoException {
                    Log.i("Indico Sentiment", "sentiment of: " + result.getSentiment());
                    haha.add(result.getSentiment().toString());
                }
            });
            return haha;
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
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
