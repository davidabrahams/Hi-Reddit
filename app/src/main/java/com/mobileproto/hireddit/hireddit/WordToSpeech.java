package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * WordToSpeech includes a method which will convert text to corresponding audio output
 * WordToSpeech constructor gets the application context
 * Speak() takes a String to generate an audio output
 * WordToSpeech is able to speak out different languages, but for this project, we set it to English
 */
public class WordToSpeech {
    private TextToSpeech ttobj;

    public WordToSpeech(Context appContext) {

        ttobj = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.US);
                }
            }
        });
    }

    public void speak(String toSpeak) {
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stop() {
        ttobj.stop();
        ttobj.shutdown();
    }
}
