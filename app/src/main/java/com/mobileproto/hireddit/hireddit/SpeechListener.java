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

        private String voiceInput;

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("onReadyForSpeech", "" + params);
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("Begin Speech", "Start talking!");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d("onRmsChanged", "" + rmsdB);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d("onBufferReceived", "buffer buffer buffer");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("End Speech", "Finished talking.");
        }

        @Override
        public void onError(int error) {
            Log.d("Error", "Error occurred with voice recognition. Error code: " + error);
        }

        @Override
        public void onResults(Bundle results) {
            voiceInput = "";
            Log.d("onResults", "" + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                voiceInput += data.get(i) + " ";
            }
            Log.d("onResults", "ArrayList: " + voiceInput);
        }

        public String getResults() {
            return voiceInput;
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d("onPartialResults", "" + partialResults);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d("onEvent", "" + eventType + ", " + params);
        }
    }
