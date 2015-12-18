package com.mobileproto.hireddit.hireddit.visuals;

import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.reddit.RedditSearcher;
import com.mobileproto.hireddit.hireddit.sharedPreference.SharedPreference;
import com.mobileproto.hireddit.hireddit.speech.SpeechCallback;
import com.mobileproto.hireddit.hireddit.speech.SpeechListener;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.tbouron.shakedetector.library.ShakeDetector;

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
 * SpeechFragment: Primary fragment shown in the app that has all speech related views
 **/
public class SpeechFragment extends Fragment implements SpeechCallback,
        RedditSearcher.CommentCallback, ListViewAdapterCallback, CustomEditTextCallback {

    private InfoFragment.NumberCommentsToSearchCallback numToSearchCb;
    private OnFragmentInteractionListener fragmentInteractionListener;
    private static final String DEBUG_TAG = "SpeechFragmentDebug";
    private static final String PREFS_QUIET = "QUIET";
    private static final String PREFS_SHAKE = "VIBRATE";
    private boolean isListening;
    private boolean firstResponse = true;
    private boolean shakeOn = true;
    private Intent recognizerIntent;
    private SpeechRecognizer sr;
    private boolean typeMode = false;
    private boolean quietMode = false;
    private ArrayList<String> links = new ArrayList<>();
    private ViewGroup.LayoutParams layoutParams;
    private int initialParams;
    private SharedPreference sharedPreference;
    private ArrayList<String> allRequests = new ArrayList<>();
    private ArrayList<String> allResponses = new ArrayList<>();
    private ListViewAdapter listViewAdapter;
    private int listViewHeight;
    private View footerSpacing;

    @Bind(R.id.listView) ListView listView;
    @Bind(R.id.volumeOnButton) ImageView quietModeButton;
    @Bind(R.id.textInputDisplay) CustomEditText editText;
    @Bind(R.id.listenButton) ImageView listenButton;
    @Bind(R.id.helloReddit) TextView helloReddit;
    @Bind(R.id.shakeButton) ImageView shakeButton;
    @Bind(R.id.infoButton) ImageView infoButton;

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

        // view
        View footerView = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        footerSpacing = footerView.findViewById(R.id.footerSpace);
        listView.addFooterView(footerView);
        listViewAdapter = new ListViewAdapter(getActivity(), allRequests, allResponses, this);
        listView.setAdapter(listViewAdapter);

        editText.setCallback(this);
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(6);

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
            //allows you to type
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                typeMode();
                return false;
            }
        });

            //if you press "done"
        editText.setOnEditorActionListener(new CustomEditText.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doneTypeMode();
                    return true;
                }
                return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() { //if scroll, disallow changing input
            @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(DEBUG_TAG, "onScrollStateChanged called - making editText invisible");
                editText.setVisibility(View.INVISIBLE);
            }

            @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        // add link functionality
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(position)));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
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

        layoutParams = listenButton.getLayoutParams();
        initialParams = layoutParams.width;

        if (sharedPreference.getValue(getActivity(), PREFS_QUIET))
            quietMode();

        shakeOn = sharedPreference.getValue(getActivity(), PREFS_SHAKE);
        updateShake();
        return view;
    }

    @Override public void leavingEditTextCallback() {
        Log.d(DEBUG_TAG, "Leaving Edit Text callback called");
        doneTypeMode();
    }

    public void quietMode() {
        Log.d(DEBUG_TAG, "enabled quietMode");
        fragmentInteractionListener.stopSpeaking();
        fragmentInteractionListener.mute();
        quietModeButton.setImageResource(R.drawable.mute);
        quietMode = true;
    }

    public void voiceMode() {
        Log.d(DEBUG_TAG, "enabled voiceMode");
        fragmentInteractionListener.unMute();
        if (!isListening && listViewAdapter.getCount() != 0) fragmentInteractionListener.speak(allResponses.get(listViewAdapter.getCount() - 1));

        quietModeButton.setImageResource(R.drawable.volume_on);
        quietMode = false;
    }

    public void typeMode() {
        Log.d(DEBUG_TAG, "enabled typeMode");
        if (typeMode) return;
        fragmentInteractionListener.stopSpeaking();
        listView.setAlpha(0);
        editText.setCursorVisible(true);
        editText.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        typeMode = true;
    }

    public void doneTypeMode() {
        Log.d(DEBUG_TAG, "enabled doneTypeMode");

        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        ArrayList<String> textInput = new ArrayList<String>();
        textInput.add(0, editText.getText().toString());
        speechResultCallback(textInput);

        if (!typeMode) return;
        Log.d(DEBUG_TAG, "typeMode is true");
        fragmentInteractionListener.stopSpeaking();
        editText.setCursorVisible(false);
        typeMode = false;
    }

    public void doListen() {
        if (!isListening) {
            listView.setAlpha(0);
            editText.setText(""); //reset what speechText says
            editText.setAlpha(1);

            Log.d(DEBUG_TAG, "Start listening");
            isListening = true;
            fragmentInteractionListener.stopSpeaking();
            sr.startListening(recognizerIntent);
            updateListeningIndicator();
        }
    }

    public void dontListen() {
        if (isListening) {
            Log.d(DEBUG_TAG, "Stop listening.");
            isListening = false;
            updateListeningIndicator();
            sr.stopListening();
        }
    }

    public void shake() {
        Log.d(DEBUG_TAG, "you shaked shaked");
        editText.setText(R.string.shake_string);
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
            if (!fragmentInteractionListener.isNetworkConnectionAvailable())
                noWifi();
            else
                new RedditSearcher(this, shakeWord, getActivity().getApplicationContext()).getRedditComment();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateShake() {
        if (shakeOn) {
            Log.d(DEBUG_TAG, "Shake mode off");
            shakeButton.setImageResource(R.drawable.no_shake);
            ShakeDetector.stop();
            shakeOn = false;
        } else {
            Log.d(DEBUG_TAG, "Shake mode on");
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
        if(getActivity() == null || !isAdded()) return;
        Resources res = getResources();
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got result, stopped listening.");
        if (voiceResult == null) {
            showComment(res.getString(R.string.error_not_recognized), null);
        }
        String firstResult = voiceResult.get(0).toString();
        editText.setText(firstResult);

        ArrayList<String> hiReddit = new ArrayList<>(
                Arrays.asList(
                        res.getString(R.string.hi_reddit_1),
                        res.getString(R.string.hi_reddit_2),
                        res.getString(R.string.hi_reddit_3),
                        res.getString(R.string.hi_reddit_4),
                        res.getString(R.string.hi_reddit_5),
                        res.getString(R.string.hi_reddit_6)
                )
        );

        if (!fragmentInteractionListener.isNetworkConnectionAvailable())
            noWifi();
        else if (hiReddit.contains(firstResult))
            showComment(res.getString(R.string.hi_reddit_response), null);
        else
            new RedditSearcher(this, firstResult, getActivity().getApplicationContext()).getRedditComment();
    }

    @Override public void partialCallback(ArrayList partialResult) {
        editText.setText(partialResult.get(0).toString());
    }

    @Override public void rmsCallback(float rmsdB) {
        int radius = initialParams + (int) rmsdB * 2;
        layoutParams.width = radius;
        layoutParams.height = radius;
        listenButton.setLayoutParams(layoutParams);
    }

    @Override public void errorCallback(int errorCode, int numErrors) {
        if(getActivity() == null || !isAdded()) return;
        isListening = false;
        updateListeningIndicator();
        Resources res = getResources();
        Log.d(DEBUG_TAG, "Got error, stopped listening.");
        if (numErrors == 1) { // to prevent repeating errors
            if (!fragmentInteractionListener.isNetworkConnectionAvailable()){
                noWifi();
            } else if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) { // error 7
                Log.d(DEBUG_TAG, "Error 7: speech not recognized");
                showComment(res.getString(R.string.error_1), null);
            } else if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) { //error 6
                Log.d(DEBUG_TAG, "Error 6: Speech timed out");
                showComment(res.getString(R.string.error_say_something), null);
            } else {
                Log.d(DEBUG_TAG, "Error " + errorCode + ": Error callback occurred from speech listener");
                showComment(res.getString(R.string.error_our_side), null);
            }
        }
    }

    @Override public int getCommentsToSearch() {
        return numToSearchCb.getCommentsToSearch();
    }

    @Override public void commentCallback(String comment, String link) {
        if (getActivity() == null || !isAdded()) return;
        Resources res = getResources();
        if (comment == null) {
            Log.d(DEBUG_TAG, "No valid comments found");
            showComment(res.getString(R.string.no_comments), null);
        } else {
            Log.d(DEBUG_TAG, "Comment callback with comment: " + comment);
            showComment(comment, link);
        }
    }

    public void showComment(String comment, String link){ //a comment will always show, so do it here
        Log.d(DEBUG_TAG, "calling showComment");
        //show and speak final result:
        fragmentInteractionListener.speak(comment);

        //add full result to history (aka listView):
        allRequests.add(editText.getText().toString());
        allResponses.add(comment);
        links.add(link);
        listViewAdapter.notifyDataSetChanged();

        //change footer view
        if(firstResponse) {
            //gives you height in pixels, NORA tested for accuracy
            listViewHeight = listView.getHeight();
            firstResponse = false;
        } else {
            //TODO: I think I can get rid of this line
            updateFooter(listViewAdapter.getLastItemHeight());
        }

        //make listView visible and overlayed input visible (as long as you don't scroll)
        listView.setAlpha(1);
        editText.setVisibility(View.VISIBLE);
    }

    @Override public void itemHeightCallback(int height) {
        updateFooter(height);
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
            fragmentInteractionListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
        dontListen();
    }

    private void switchToInfoFragment() {
        fragmentInteractionListener.switchFragment(InfoFragment.newInstance(numToSearchCb), FragmentTransaction.TRANSIT_NONE,
                R.anim.slide_out_up);
        fragmentInteractionListener.stopSpeaking();
        dontListen();
    }

    private void noWifi() {
        if(getActivity() == null || !isAdded()) return;
        Resources res = getResources();
        ArrayList<String> NETWORK_UNAVAILABLE = new ArrayList<>(
                Arrays.asList(
                        res.getString(R.string.no_wifi_1),
                        res.getString(R.string.no_wifi_2),
                        res.getString(R.string.no_wifi_3),
                        res.getString(R.string.no_wifi_4),
                        res.getString(R.string.no_wifi_5),
                        res.getString(R.string.no_wifi_6)
                )
        );
        Random mRandom = new Random();
        int index = mRandom.nextInt(NETWORK_UNAVAILABLE.size());
        showComment(NETWORK_UNAVAILABLE.get(index), null);
    }
    public interface OnFragmentInteractionListener {
        void switchFragment(Fragment f);
        void switchFragment(Fragment f, int customAnimationIn, int customAnimationOut);
        void speak(String comment);
        void stopSpeaking();
        boolean isNetworkConnectionAvailable();
        void mute();
        void unMute();
    }
}
