package com.mobileproto.hireddit.hireddit;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private SpeechRecognizer sr;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);

        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        SpeechListener listener = new SpeechListener();
        sr.setRecognitionListener(listener);

    }

    public void doListen(){
        sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
    }
}
