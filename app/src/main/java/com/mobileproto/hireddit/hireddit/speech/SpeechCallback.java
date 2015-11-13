package com.mobileproto.hireddit.hireddit.speech;

import java.util.ArrayList;

/**
 * Speech Callback: Callback for getting speech results after user is done talking.
 */
public interface SpeechCallback
{
    void callback(ArrayList voiceResult);

    void errorCallback(int errorCode);

    void partialCallback(ArrayList partialResult);
}
