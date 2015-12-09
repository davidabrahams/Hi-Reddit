package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.reddit.RedditSearcher;
import com.mobileproto.hireddit.hireddit.speech.SpeechCallback;
import com.mobileproto.hireddit.hireddit.speech.SpeechListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeechFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeechFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeechFragment extends Fragment implements SpeechCallback,
        RedditSearcher.CommentCallback {
    private OnFragmentInteractionListener mListener;
    private static final String DEBUG_TAG = "SpeechFragment Debug";
    private boolean isListening;
    private boolean shakeOn = true;
    private SpeechListener listener;
    private ArrayList voiceInput;
    private Intent recognizerIntent;
    private SpeechRecognizer sr;
    private boolean typeMode = false;
    private boolean quietMode = false;
    private String link;
    private ViewGroup.LayoutParams cParams;
    private Integer radius;

    @Bind(R.id.volumeOnButton) ImageView quietModeButton;
    @Bind(R.id.TextInputDisplay) EditText TextInputDisplay;
    @Bind(R.id.listenButton) ImageView listenButton;
    @Bind(R.id.helloReddit) TextView helloReddit;
    @Bind(R.id.speechTextDisplay) TextView speechTextDisplay;
    @Bind(R.id.commentText) TextView commentText;
    @Bind(R.id.shakeButton) ImageView shakeButton;
    @Bind(R.id.infoButton) ImageView infoButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpeechFragment.
     */
    public static SpeechFragment newInstance() {
        SpeechFragment fragment = new SpeechFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SpeechFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShakeDetector.create(this.getContext(), new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                shake();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speech, container, false);
        ButterKnife.bind(this, view);

        listener = new SpeechListener(this);

        isListening = false;
        updateListeningIndicator();

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        sr = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        sr.setRecognitionListener(listener);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/volkswagen-serial-bold.ttf");
        helloReddit.setTypeface(tf);

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening)
                    dontListen();
                else
                    doListen();
            }
        });

        quietModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quietMode)
                    voiceMode();
                else
                    quietMode();
            }
        });

        speechTextDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeMode();
            }
        });

        TextInputDisplay.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(TextInputDisplay.getWindowToken(), 0);
                            ArrayList<String> TextInput = new ArrayList<String>();
                            TextInput.add(0, TextInputDisplay.getText().toString());
                            speechResultCallback(TextInput);
                            SpeakMode();
                            return true;
                        }
                        return false;
                    }
                });

        helloReddit.setTypeface(tf);

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });

        shakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShake();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.switchFragment(InfoFragment.newInstance());
            }
        });

        return view;
    }

    public void quietMode() {
        mListener.stopSpeaking();
        mListener.flipMute();
        quietModeButton.setImageResource(R.drawable.mute);
        quietMode = true;
    }

    public void voiceMode() {
        mListener.flipMute();
        if (!isListening) mListener.speak(commentText.getText().toString());
        quietModeButton.setImageResource(R.drawable.volume_on);
        quietMode = false;
    }

    public void typeMode() {
        if (typeMode) return;
        mListener.stopSpeaking();
        TextInputDisplay.setText(speechTextDisplay.getText().toString());
        speechTextDisplay.setVisibility(View.INVISIBLE);
        TextInputDisplay.setVisibility(View.VISIBLE);
        TextInputDisplay.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(TextInputDisplay, InputMethodManager.SHOW_IMPLICIT);
        typeMode = true;
    }

    public void SpeakMode() {
        if (!typeMode) return;
        mListener.stopSpeaking();
        TextInputDisplay.setText(TextInputDisplay.getText().toString());
        TextInputDisplay.setVisibility(View.INVISIBLE);
        speechTextDisplay.setVisibility(View.VISIBLE);
        typeMode = false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void doListen() {
        Log.d(DEBUG_TAG, "Start listening");
        isListening = true;
        updateListeningIndicator();
        mListener.stopSpeaking();
        sr.startListening(recognizerIntent);
    }

    public void dontListen() {
        Log.d(DEBUG_TAG, "Stop listening.");
        isListening = false;
        updateListeningIndicator();
        sr.stopListening();
    }

    public void shake(){
        speechTextDisplay.setText("**Shake Shake**");
        try {
            ArrayList<String> possibleWords = new ArrayList<>();
            AssetManager assetManager = getContext().getAssets();
            InputStream shakeStream = assetManager.open("randomwords.txt");
            BufferedReader shakeReader = new BufferedReader(new InputStreamReader(shakeStream, "UTF-8"));
            String str;
            while((str = shakeReader.readLine()) != null){
                possibleWords.add(str);
            }
            Random mRandom = new Random();
            int index = mRandom.nextInt(possibleWords.size());
            String shakeWord = possibleWords.get(index);
            new RedditSearcher(this, shakeWord, getActivity().getApplicationContext()).getRedditComment();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateShake() {
        if (shakeOn) {
            shakeButton.setImageResource(R.drawable.no_shake);
            ShakeDetector.stop();
            shakeOn = false;
        }
        else {
            shakeButton.setImageResource(R.drawable.yes_shake);
            ShakeDetector.start();
            shakeOn = true;
        }
    }

    private void updateListeningIndicator() {
        if (isListening)
            listenButton.setImageResource(R.drawable.yes_mic);
        else
            listenButton.setImageResource(R.drawable.no_mic);
    }

    // TODO: Make this function only take a String
    @Override
    public void speechResultCallback(ArrayList voiceResult) {
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got result, stopped listening.");

        voiceInput = voiceResult;
        String firstResult = voiceInput.get(0).toString();
        speechTextDisplay.setText(firstResult);
        new RedditSearcher(this, firstResult, getActivity().getApplicationContext()).getRedditComment();
    }

    @Override
    public void partialCallback(ArrayList partialResult) {
        speechTextDisplay.setText(partialResult.get(0).toString());
    }

    @Override
    public void rmsCallback(float rmsdB) {
        radius = 180 + (int) rmsdB * 2; // 180 is the initial radius
        cParams = listenButton.getLayoutParams();
        cParams.width = radius;
        cParams.height = radius;
        listenButton.setLayoutParams(cParams);
    }

    @Override
    public void errorCallback(int errorCode, int numErrors) {
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got error, stopped listening.");

        if (numErrors == 1) { // to prevent showing multiple toasts
            if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) { // error 7
                //TODO: change this to saying out loud, "please try again"
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error: Speech was not recognized.", Toast.LENGTH_SHORT).show();
            } else if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) { //error 6
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error: Please say something.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error occurred! Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void commentCallback(String comment, ArrayList<String> linkInfo) {
        if (comment == null) {
            Log.d(DEBUG_TAG, "No valid comments found");
            Toast.makeText(getContext(), "No valid comments available", Toast.LENGTH_SHORT).show();
        } else {
            //context is 2 to show the previous two comments above (if available) because people wanted to see the parent comments
            link = "https://www.reddit.com/comments/" + linkInfo.get(0) + "/_/" + linkInfo.get(1) + "?context=2";
            commentText.setText(comment);
            mListener.speak(comment);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        ShakeDetector.stop();
    }

    @Override
    public void onStop(){
        super.onStop();
        ShakeDetector.stop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ShakeDetector.destroy();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void switchFragment(Fragment f);

        void speak(String comment);

        void stopSpeaking();

        void flipMute();
    }

}
