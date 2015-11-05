package com.mobileproto.hireddit.hireddit;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
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
public class GetComment extends AppCompatActivity{

    String indicoApiKey = "A7a8f16edc7a58c8a7773ba95c6d2241bA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Indico.init(this, indicoApiKey, null);

        try {
            Indico.sentiment.predict("indico is so easy to use!", new IndicoCallback<IndicoResult>() {
                @Override public void handle(IndicoResult result) throws IndicoException {
                    Log.i("Indico Sentiment", "sentiment of: " + result.getSentiment());
                }
            });
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
    }

}
