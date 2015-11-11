package com.mobileproto.hireddit.hireddit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Speech Fragment: Holds functionality for requesting and recieving voice input
 */
public class SpeechFragment extends Fragment{
    String TAG = "myDebug";
    private View view;
    private SpeechRecognizer sr;
    private SpeechListener listener;
    private Intent recognizerIntent;
    private ArrayList voiceInput;
    private boolean isListening = false;
    @Bind(R.id.speech) Button speechButton;

    public SpeechFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_speech, container, false);
        ButterKnife.bind(this, view);

        // *~Speech stuff~* //
        listener = new SpeechListener(new SpeechCalback() {
            @Override
            public void callback(ArrayList voiceResult) {
                voiceInput = voiceResult;
                Log.d(TAG, "" + voiceInput);
                isListening = false;
            }
        });

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sr = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        sr.setRecognitionListener(listener);

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isListening) {
                    doListen();
                } else{
                    //what you want to happen if you press the button an you're already listening for voice
                    dontListen(); //note that you don't need to press the button again to stop listening - it'll automatically stop
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

    public void dontListen(){
        Log.d(TAG, "Stop listening.");
        sr.stopListening();
        isListening = false;
    }
}
