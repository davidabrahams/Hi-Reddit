package com.mobileproto.hireddit.hireddit.speech;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Voice Recognition: Listener for speech to text input
 */

public class SpeechListener implements RecognitionListener {
    String DEBUG_TAG = "SpeechListener Debug";
    private SpeechCallback speechCallback;
    private int numErrors;

    public SpeechListener(SpeechCallback mSpeechCallback) {
        speechCallback = mSpeechCallback;
        numErrors = 0;
    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(DEBUG_TAG, "onReadyForSpeech " + params);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(DEBUG_TAG, "oneBeginningOfSpeech - Start talking!");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.d(DEBUG_TAG, "onRmsChanged " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(DEBUG_TAG, "onBuffer Received");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(DEBUG_TAG, "onEndOfSpeech - Finished talking.");
        numErrors = 0;
    }

    @Override
    public void onError(int error) {
        Log.d(DEBUG_TAG, "onError - Error occurred with voice recognition. Error code: " + error);
        numErrors += 1;
        speechCallback.errorCallback(error, numErrors);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(DEBUG_TAG, "onResults: " + data);
        speechCallback.speechResultCallback(data);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> partial = partialResults.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(DEBUG_TAG, "onPartialResults: " + partial);
        speechCallback.partialCallback(partial);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(DEBUG_TAG, "onEvent " + eventType + ", " + params);
    }
}
