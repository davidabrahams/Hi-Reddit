package com.mobileproto.hireddit.hireddit.speech;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Voice Recognition: For speech to text input
 */

public class SpeechListener implements RecognitionListener
{
    String DEBUG_TAG = "SpeechListener Debug";
    private String voiceInput;
    private ArrayList data;
    private ArrayList partial;
    private SpeechCallback speechCallback;

    public SpeechListener(SpeechCallback mSpeechCallback)
    {
        speechCallback = mSpeechCallback;
    }


    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(DEBUG_TAG, "onReadyForSpeech " + params);
    }

    @Override
    public void onBeginningOfSpeech()
    {
        Log.d(DEBUG_TAG, "oneBeginningOfSpeech - Start talking!");
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
        //Log.d(DEBUG_TAG, "onRmsChanged " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(DEBUG_TAG, "onBuffer Received");
    }

    @Override
    public void onEndOfSpeech()
    {
        Log.d(DEBUG_TAG, "onEndOfSpeech - Finished talking.");
    }

    @Override
    public void onError(int error)
    {
        Log.d(DEBUG_TAG, "onError - Error occurred with voice recognition. Error code: " + error);
        speechCallback.errorCallback(error);
    }

    @Override
    public void onResults(Bundle results)
    {
        Log.d(DEBUG_TAG, "onResults " + results);

        data = null;
        data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(DEBUG_TAG, "onResults: " + data);
        speechCallback.callback(data);
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        partial = null;
        partial = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(DEBUG_TAG, "onPartialResults: " + partial);
        speechCallback.partialCallback(partial);
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(DEBUG_TAG, "onEvent " + eventType + ", " + params);
    }
}