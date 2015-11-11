package com.mobileproto.hireddit.hireddit;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    String TAG = "myDebug";
    private View view;
    private SpeechRecognizer sr;
    private SpeechListener listener;
    private Intent recognizerIntent;
    private ArrayList voiceInput;
    private boolean isListening = false;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        Button speechButton = (Button) view.findViewById(R.id.speech);

        // *~Speech stuff~* //
        listener = new SpeechListener(new SpeechCalback() {
            @Override
            public void callback(ArrayList voiceResult) {
                Log.d(TAG, "callbacking -> you got resultzzz");
                voiceInput = voiceResult;
                Log.d(TAG, "" + voiceInput);
                isListening = false;
            }
        });

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sr = SpeechRecognizer.createSpeechRecognizer(getContext());
        sr.setRecognitionListener(listener);

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isListening) {
                    doListen();
                } else{
                    //what you want to happen if you press the button an you're already listening for voice
                }
            }
        });
        return view;
    }

    public void doListen(){
        Log.d(TAG, "Start listening.");
        isListening = true;
        sr.startListening(recognizerIntent);
    }


    public void dontListen(){ //doesn't need to be called, but I'll leave it here if we want to manually stop recording for some reason?
        Log.d(TAG, "Stop listening.");
        sr.stopListening();
    }
}
