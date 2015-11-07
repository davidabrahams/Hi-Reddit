package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * TextToSpeech includes a method which will convert text to corresponding audio output
 * TextToSpeech constructor gets the application context
 * Speak() takes a String to generate an audio output
 * TextToSpeech is able to speak out different languages, but for this project, we set it to English
 */
public class WordToSpeech {
    private TextToSpeech m_ttobj;

    public WordToSpeech(Context appContext) {

        m_ttobj = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    m_ttobj.setLanguage(Locale.US);
                }
            }
        });
    }

    public void speak(String toSpeak) {
        m_ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void stop() {
        m_ttobj.stop();
        m_ttobj.shutdown();
    }
}
