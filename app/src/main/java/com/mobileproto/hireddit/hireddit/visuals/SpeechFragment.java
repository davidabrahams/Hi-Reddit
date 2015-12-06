package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.reddit.RedditSearcher;
import com.mobileproto.hireddit.hireddit.speech.SpeechCallback;
import com.mobileproto.hireddit.hireddit.speech.SpeechListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private SpeechListener listener;
    private ArrayList voiceInput;
    private Intent recognizerIntent;
    private SpeechRecognizer sr;
    private String link;
    private ViewGroup.LayoutParams cParams;
    private Integer radius;

    @Bind(R.id.listenButton) ImageView listenButton;
    @Bind(R.id.helloReddit) TextView helloReddit;
    @Bind(R.id.speechTextDisplay) TextView speechTextDisplay;
    @Bind(R.id.commentText) TextView commentText;
    @Bind(R.id.settingsButton) ImageView settingsButton;

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
        if (getArguments() != null) {
        }
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
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening)
                    dontListen();
                else
                    doListen();
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

        return view;
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
        ArrayList<String> possibleWords = new ArrayList<>(Arrays.asList("abductee", "anime", "app", "backslash", "barista", "bling", "blog", "blogger", "broadband", "buckyball", "burka", "carbs", "ciabatta", "colonoscopy", "cybersex", "detainee", "dotcom", "earbud", "ecotourism", "eldercare", "electronica", "flipflop", "globalization", "google", "handheld", "hazmat", "helpline", "hoodie", "hummus", "hyperlink", "inbox", "intranet", "jihadist", "latte", "login", "logoff", "logon", "logout", "loonie", "madrassah", "malware", " manga", "mashup", "microloan", "multiplayer", "nanotechnology", "neocon", "neoconservative", "nigga", "offload", "offshoring", "orc", "paragliding", "parasailing", "pecs", "phishing", "playlist", "podcast", "polyamory", "prenup", "quesadilla", "remortgage", "reorg", "ringtone", "rugrat", "sampling", "satay", "scrunchie", "selloff", "semiotics", "semiretired", "sharia", "shiitake", "shopaholic", "sim", "simulcast", "slideshow", "smoothie", "snarky", "snowblower", "soulmate", "spam", "spammer", "spellcheck", "spellchecker", "spyware", "startup", "stoner", "supermodel", "supersize", "tealight", "techno", "uninstall", "unsubscribe", "username", "voicemail", "wack", "webcam", "webcast", "webmaster", "webpage", "widescreen", " wiki", "wishlist", "zapper", "acid reflux", "al-Qaeda", "asymmetric warfare", "bird flu", "black box", "bling-bling", "body piercing", "bok choy", "booty call", "Botox", "break-dance", "break-dancer", "break-dancing", "caller ID", "call waiting", "cargo pants", "chat room", "civil union", "clip art", "closed-captioned", "control freak", "crash diet", "data mining", "DHS", "dialog box", "digital camera", "dim sum", "dirty bomb", "double-click", "drama queen", "e-commerce", "end product", "ethnic cleansing", "FAQ", "feng shui", "flight recorder", "greenhouse gas", "ground zero", "guest worker", "gut-wrenching", "hard-wired", "HDTV", "hedge fund", "help desk", "home fries", "Homeland Security", "HTML", "hybrid car", "identity theft", "IED", "insider trading", "instant message", "Internet cafe", "IPO", "iPod", "ISP", "IVF", "jet ski", "jet skiing", "lap dance", "live-in", "low-carb", "market share", "memory stick", "model home", "MP3", "MP3 player", "open-plan", "par-per-view", "personal trainer", "plug-in", "pop culture", "pop-up", "prenuptial agreement", "pro bono", "Prozac", "pump and dump", "Rasta", "Rastafarian", "Rastafarianism", "RDA", "reality TV", "refried beans", "restraining order", "road trip", "same-sex", "SARS", "satellite radio", "screen saver", "search engine", "sex worker", "shock jock", "SIDS", "smart bomb", "snake oil", "soccer mom", "social networking", "special needs", "speed dial", "spring roll", "squeaky clean", "starter home", "stretch limo", "strip search", "Sudoku", "suicide bomber", "suicide bombing", "SUV", "tailgate party", "talk radio", "Tex-Mex", "text message", "top-of-the-line", "trans fat", "urban myth", "Viagra", "weapon of mass destruction", "wine cooler", "win-win", "WMD", "Ziploc bag"));
        Random mRandom = new Random();
        int index = mRandom.nextInt(possibleWords.size());
        String shakeWord = possibleWords.get(index);
        speechTextDisplay.setText("");
        new RedditSearcher(this, shakeWord, getActivity().getApplicationContext()).getRedditComment();
    }

    private void updateListeningIndicator() {
        if (isListening)
            listenButton.setImageResource(R.drawable.yes_mic);
        else
            listenButton.setImageResource(R.drawable.no_mic);
    }

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
    public void rmsCallback(float rmsdB){
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
        void speak(String comment);
        void stopSpeaking();
    }

}
