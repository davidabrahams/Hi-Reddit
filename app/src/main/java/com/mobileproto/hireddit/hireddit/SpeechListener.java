package com.mobileproto.hireddit.hireddit;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Voice Recognition: For speech to text input
 */
//public class VoiceRecognition implements View.OnClickListener {


    class SpeechListener implements RecognitionListener {
        String TAG = "myDebug";
        private String voiceInput;
        private ArrayList data;

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
            data = null;
            voiceInput = "";
            Log.d(TAG, "onResults " + results);
            data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                voiceInput += data.get(i) + " ";
            }
            Log.d(TAG, "onResults - ArrayList: " + voiceInput);

            //TODO: get results fromm here and put them in fragment
        }

        public ArrayList getResults() {
            return data;
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
