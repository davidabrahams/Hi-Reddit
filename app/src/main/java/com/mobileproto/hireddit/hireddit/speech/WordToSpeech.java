package com.mobileproto.hireddit.hireddit.speech;

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
    private TextToSpeech mTtobj;
    private boolean mute;

    public WordToSpeech(Context appContext) {

        mTtobj = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTtobj.setLanguage(Locale.US);
                }
            }
        });

        mute = false;
    }

    public void speak(String toSpeak) {
        if (mute) return;
        // version check, if SDK is newer than 21, use the update speak method
        // if not, use the deprecated one
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            mTtobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTtobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void stop() {
        if (mTtobj.isSpeaking()) {
            mTtobj.stop();
        }
    }

    public void mute() {
        mute = true;
    }

    public void unMute() {
        mute = false;
    }

    public void destroy() {
        mTtobj.stop();
        mTtobj.shutdown();
    }
}
