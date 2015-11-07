package com.mobileproto.hireddit.hireddit;

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

    private ArrayList voiceInput;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        Button speechButton = (Button) view.findViewById(R.id.speech);
        listener = new SpeechListener();
        sr = SpeechRecognizer.createSpeechRecognizer(getContext());
        sr.setRecognitionListener(listener);

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doListen();
            }
        });
        return view;
    }

    public void doListen(){
        Log.d(TAG, "Start listening.");
        sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getContext()));

       // voiceInput = listener.getResults();
    }
    public void dontListen(){ //doesn't need to be called, but I'll leave it here if we want to manually stop recording.
        Log.d(TAG, "Stop listening.");
        sr.stopListening();
    }
}
