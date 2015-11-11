package com.mobileproto.hireddit.hireddit;

import java.util.ArrayList;

/**
 * Speech Callback: Callback for getting speech results after user is done talking.
 */
public interface SpeechCalback {
    void callback(ArrayList voiceResult);
}
