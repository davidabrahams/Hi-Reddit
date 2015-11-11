package com.mobileproto.hireddit.hireddit;

import android.app.Fragment;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Voice Recognition: For speech to text input
 */

public class SpeechListener implements RecognitionListener {
    String TAG = "SpeechListener Debug";
    private String voiceInput;
    private ArrayList data;
    private int flag = 0;
    private SpeechCalback speechCallback;

    public SpeechListener(SpeechCalback mSpeechCallback){
        speechCallback = mSpeechCallback;
    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech " + params);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "oneBeginningOfSpeech - Start talking!");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.d(TAG, "onRmsChanged " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBuffer Received");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech - Finished talking.");
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "onError - Error occurred with voice recognition. Error code: " + error);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults " + results);

        data = null;
        data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for (int i = 0; i < data.size(); i++) {
            Log.d(TAG, "onResults - ArrayList: " +  data.get(i));
        }

        speechCallback.callback(data);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults " + partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType + ", " + params);
    }
}
