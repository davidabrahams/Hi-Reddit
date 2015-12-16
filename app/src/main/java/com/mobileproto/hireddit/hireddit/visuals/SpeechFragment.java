package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.reddit.RedditSearcher;
import com.mobileproto.hireddit.hireddit.sharedPreference.SharedPreference;
import com.mobileproto.hireddit.hireddit.speech.SpeechCallback;
import com.mobileproto.hireddit.hireddit.speech.SpeechListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import butterknife.Bind;
import butterknife.ButterKnife;
import android.widget.ListView;

/**
 * SpeechFragment: Primary fragment shown in the app that has
 **/
public class SpeechFragment extends Fragment implements SpeechCallback,
        RedditSearcher.CommentCallback, ListViewAdapterCallback {

    private InfoFragment.NumberCommentsToSearchCallback numToSearchCb;
    private OnFragmentInteractionListener mListener;
    private static final String DEBUG_TAG = "SpeechFragment Debug";
    private static final String PREFS_QUIET = "QUIET";
    private static final String PREFS_SHAKE = "VIBRATE";
    private boolean isListening;
    private boolean firstResponse = true;
    private boolean shakeOn = true;
    private ArrayList voiceInput;

    private Intent recognizerIntent;
    private SpeechRecognizer sr;
    private boolean typeMode = false;
    private boolean quietMode = false;
    private ArrayList<String> links = new ArrayList<String>();

    private ViewGroup.LayoutParams cParams;
    private int initialParams;
    private SharedPreference sharedPreference;


    private ArrayList<String> allRequests = new ArrayList<String>();
    private ArrayList<String> allResponses = new ArrayList<String>();
    private ListViewAdapter listViewAdapter;
    private int listViewHeight;
    private int itemHeight;
    private View footerSpacing;

    @Bind (R.id.listView) ListView listView;
    @Bind(R.id.volumeOnButton) ImageView quietModeButton;
    @Bind(R.id.textInputDisplay) EditText inputTextDisplay;
    @Bind(R.id.listenButton) ImageView listenButton;
    @Bind(R.id.helloReddit) TextView helloReddit;
    //@Bind(R.id.speechTextDisplay) TextView speechTextDisplay;
    @Bind(R.id.shakeButton) ImageView shakeButton;
    @Bind(R.id.infoButton) ImageView infoButton;

    //private static final String didntUnderstand = "Sorry, what was that? I didn't understand what you said.";
    private static final ArrayList<String> NETWORK_UNAVAILABLE = new ArrayList<>(
            Arrays.asList(
                    "You know, life without Wi-Fi is hard.",
                    "maybe you should move to canada, we have really good internet here :o)",
                    "Pay me $50 and I will get you Wi-Fi",
                    "I agree that free Wi-Fi shouldn't be forbidden",
                    "Keep looking on the internet. Surely there must be a good forecast somewhere" +
                            " out there.",
                    "I won't work until you provide me with life-long free Wi-Fi"
            )
    );


    public static SpeechFragment newInstance(InfoFragment.NumberCommentsToSearchCallback cb) {
        SpeechFragment fragment = new SpeechFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setNumToSearchCb(cb);
        return fragment;
    }

    public SpeechFragment() {}

    private void setNumToSearchCb(InfoFragment.NumberCommentsToSearchCallback cb) {
        this.numToSearchCb = cb;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShakeDetector.create(this.getContext(), new ShakeDetector.OnShakeListener() {
            @Override public void OnShake() {
                shake();
            }
        });
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speech, container, false);
        ButterKnife.bind(this, view);

        sharedPreference = new SharedPreference();

        // listView
        View footerView = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        footerSpacing = footerView.findViewById(R.id.footerSpace);
        listView.addFooterView(footerView);
        //TODO: get rid of overscroll graphic

        listViewAdapter = new ListViewAdapter(getActivity(), allRequests, allResponses, this);
        listView.setAdapter(listViewAdapter);

        // voice recognition
        SpeechListener listener = new SpeechListener(this);
        isListening = false;
        updateListeningIndicator();

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        sr = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        sr.setRecognitionListener(listener);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/volkswagen-serial-bold.ttf");
        helloReddit.setTypeface(tf);

        //create listeners for buttons
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (isListening) dontListen();
                else doListen();
            }
        });

        quietModeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (quietMode) voiceMode();
                else quietMode();
            }
        });

        //allowing text input
        inputTextDisplay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                typeMode();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() { //if scroll, disallow changing input
            @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(DEBUG_TAG, "onscrollstatechangeddddd");
                inputTextDisplay.setVisibility(View.INVISIBLE);
            }

            @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Do nothing
                }
        });

        inputTextDisplay.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        Log.d(DEBUG_TAG, "on editor action");
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.d(DEBUG_TAG, "on editor action DONE");
                            InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(inputTextDisplay.getWindowToken(), 0);
                            ArrayList<String> textInput = new ArrayList<String>();
                            textInput.add(0, inputTextDisplay.getText().toString());
                            speechResultCallback(textInput);
                            speakMode();
                            return true;
                        }
                        return false;
                    }
                });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(links.get(position) != "false") {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(position)));
                    startActivity(browserIntent);
                }
                return false;
            }
        });

        shakeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                updateShake();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                switchToInfoFragment();
            }
        });

        cParams = listenButton.getLayoutParams();
        initialParams = cParams.width;

        if (sharedPreference.getValue(getActivity(), PREFS_QUIET)) {
            quietMode();
        } else {
            voiceMode();
        }

        shakeOn = sharedPreference.getValue(getActivity(), PREFS_SHAKE);
        updateShake();
        return view;
    }

    public void quietMode() {
        Log.d(DEBUG_TAG, "enabled quietMode");
        mListener.stopSpeaking();
        mListener.flipMute();
        quietModeButton.setImageResource(R.drawable.mute);
        quietMode = true;
    }

    public void voiceMode() {
        Log.d(DEBUG_TAG, "enabled voiceMode");
        mListener.flipMute();
        if (!isListening && listViewAdapter.getCount() != 0) mListener.speak(allResponses.get(listViewAdapter.getCount() - 1));
        quietModeButton.setImageResource(R.drawable.volume_on);
        quietMode = false;
    }

    public void typeMode() {
        Log.d(DEBUG_TAG, "enabled typeMode");
        if (typeMode) return;
        mListener.stopSpeaking();
        inputTextDisplay.setText(inputTextDisplay.getText().toString());
        listView.setAlpha(0);
        //Animation listViewAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
        //        R.anim.listview_fade);
        //listView.startAnimation(listViewAnimation);

        inputTextDisplay.setCursorVisible(true);

        inputTextDisplay.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(inputTextDisplay, InputMethodManager.SHOW_IMPLICIT);
        typeMode = true;
    }

    public void speakMode() {
        Log.d(DEBUG_TAG, "enabled speakMode");
        if (!typeMode) return;
        mListener.stopSpeaking();
        inputTextDisplay.setText(inputTextDisplay.getText().toString()); //TODO: shouldn't this be speechTextDisplay? this is redundant
        //listView.setAlpha(1);
        inputTextDisplay.setCursorVisible(false);
        typeMode = false;
    }

    public void doListen() {
        //TODO: make listView animate off page instead. Code below doesn't work.
        //Animation listViewAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
        //        R.anim.listview_out);
        //listView.startAnimation(listViewAnimation);
        listView.setAlpha(0);
        inputTextDisplay.setText(""); //reset what speechText says
        inputTextDisplay.setAlpha(1);

        Log.d(DEBUG_TAG, "Start listening");
        isListening = true;
        mListener.stopSpeaking();
        sr.startListening(recognizerIntent);
        updateListeningIndicator();
    }

    public void dontListen() {
        Log.d(DEBUG_TAG, "Stop listening.");
        isListening = false;
        updateListeningIndicator();
        sr.stopListening();
    }

    public void shake() {
        Log.d(DEBUG_TAG, "you shaked shaked");
        inputTextDisplay.setText(R.string.shake_string);
        try {
            ArrayList<String> possibleWords = new ArrayList<>();
            AssetManager assetManager = getContext().getAssets();
            InputStream shakeStream = assetManager.open("randomwords.txt");
            BufferedReader shakeReader = new BufferedReader(new InputStreamReader(shakeStream, "UTF-8"));
            String str;
            while ((str = shakeReader.readLine()) != null) {
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
        Log.d(DEBUG_TAG, "changing shake mode");
        if (shakeOn) {
            shakeButton.setImageResource(R.drawable.no_shake);
            ShakeDetector.stop();
            shakeOn = false;
        } else {
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
    @Override public void speechResultCallback(ArrayList voiceResult) {
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got result, stopped listening.");
        voiceInput = voiceResult;

        // TODO: why not just let error callback do this? if your voiceInput is null you're
        // going to get an error
        if (voiceInput == null) {
            showComment("Sorry, you said nothing.", "false");
        }

        String firstResult = voiceInput.get(0).toString();
        inputTextDisplay.setText(firstResult);

        if (mListener.isNetworkConnectionAvailable()) {
            new RedditSearcher(this,
                    firstResult, getActivity().getApplicationContext()).getRedditComment();
        } else {
            Random mRandom = new Random();
            int index = mRandom.nextInt(NETWORK_UNAVAILABLE.size());
            showComment(NETWORK_UNAVAILABLE.get(index), "false");
        }
    }

    @Override public void partialCallback(ArrayList partialResult) {
        inputTextDisplay.setText(partialResult.get(0).toString());
    }

    @Override public void rmsCallback(float rmsdB) {
        int radius = initialParams + (int) rmsdB * 2;
        cParams.width = radius;
        cParams.height = radius;
        listenButton.setLayoutParams(cParams);
    }

    @Override public void errorCallback(int errorCode, int numErrors) {
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got error, stopped listening.");

        if (numErrors == 1) { // to prevent repeating errors
            if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) { // error 7
                //TODO: change this to saying out loud, "please try again"
                Log.d(DEBUG_TAG, "Error 7: speech not recognized");
                showComment("make sense pls", "false");
            } else if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) { //error 6
                Log.d(DEBUG_TAG, "Error 6: Speech timed out");
                showComment("you have a mouth right", "false");
            } else {
                Log.d(DEBUG_TAG, "Error " + errorCode + ": Error callback occurred from speech listener");
                showComment("error on our side, sorry :'-(", "false");
            }
        }
    }

    @Override public int getCommentsToSearch() {
        return numToSearchCb.getCommentsToSearch();
    }

    @Override public void commentCallback(String comment, String link) {
        if (comment == null) {
            Log.d(DEBUG_TAG, "No valid comments found");
            Toast.makeText(getContext(), "No valid comments available", Toast.LENGTH_SHORT).show();
            showComment("Reddit doesn't know how to respond to that", "false");
        } else {
            Log.d(DEBUG_TAG, "Comment callback with comment: " + comment);
            showComment(comment, link);
        }
    }

    public void showComment(String comment, String link){ //a comment will always show, so do it here
        Log.d(DEBUG_TAG, "calling showComment");
        //show and speak final result:
        mListener.speak(comment);

        //add full result to history (aka listView):
        allRequests.add(inputTextDisplay.getText().toString());
        allResponses.add(comment);
        links.add(link);
        listViewAdapter.notifyDataSetChanged();

        //change footer view
        if(firstResponse == true) {
            //gives you height in pixels, NORA tested for accuracy
            listViewHeight = listView.getHeight();
            firstResponse = false;
        } else {
            //TODO: I think I can get rid of this line
            updateFooter(listViewAdapter.getLastItemHeight());
        }

        //make listview visible and overlayed input visible (as long as you don't scroll)
        listView.setAlpha(1);
        inputTextDisplay.setVisibility(View.VISIBLE);
    }

    @Override public void itemHeightCallback(int height) {
        itemHeight = height;
        updateFooter(itemHeight);
    }

    public void updateFooter(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) footerSpacing.getLayoutParams();
        params.height = listViewHeight - height;
        footerSpacing.setLayoutParams(params);
        listView.setSelection(listViewAdapter.getCount() - 1);
    }

    @Override public void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override public void onPause() {
        super.onPause();
        ShakeDetector.stop();
    }

    @Override public void onStop() {
        super.onStop();
        sharedPreference.save(getActivity(), PREFS_QUIET, quietMode);
        sharedPreference.save(getActivity(), PREFS_SHAKE, !shakeOn);
        ShakeDetector.stop();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void switchToInfoFragment() {
        mListener.switchFragment(InfoFragment.newInstance(numToSearchCb), FragmentTransaction.TRANSIT_NONE,
                R.anim.slide_out_up);
        mListener.stopSpeaking();
        dontListen();
    }

    public interface OnFragmentInteractionListener {
        void switchFragment(Fragment f);
        void switchFragment(Fragment f, int customAnimationIn, int customAnimationOut);
        void speak(String comment);
        void stopSpeaking();
        boolean isNetworkConnectionAvailable();
        void flipMute();
    }
}
