package com.mobileproto.hireddit.hireddit.speech;

import java.util.ArrayList;

/**
 * Speech Callback: Callback for getting speech results after user is done talking.
 */
public interface SpeechCallback {

    void speechResultCallback(ArrayList voiceResult);

    void errorCallback(int errorCode, int numErrors);

    void partialCallback(ArrayList partialResult);

    void rmsCallback(float rmsdB);

}
