package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.reddit.RedditSearcher;
import com.mobileproto.hireddit.hireddit.speech.SpeechCallback;
import com.mobileproto.hireddit.hireddit.speech.SpeechListener;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeechFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeechFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeechFragment extends Fragment implements SpeechCallback,
        RedditSearcher.CommentCallback, ListViewAdapterCallback {
    private OnFragmentInteractionListener mListener;
    private static final String DEBUG_TAG = "SpeechFragmentDebug";
    private boolean isListening;
    private boolean firstResponse = true;
    private SpeechListener listener;
    private ArrayList<String> voiceInput;
    private Intent recognizerIntent;
    private SpeechRecognizer sr;

    private ArrayList<String> allRequests = new ArrayList<String>();
    private ArrayList<String> allResponses = new ArrayList<String>();
    private ListViewAdapter listViewAdapter;
    private int listViewHeight;
    private int itemHeight;

    @Bind (R.id.listView) ListView listView;
    @Bind (R.id.listenButton) ImageView listenButton;
    @Bind (R.id.helloReddit) TextView helloReddit;
    @Bind (R.id.speechText) TextView speechTextDisplay;
    //@Bind(R.id.commentText) TextView commentText;
    @Bind (R.id.settingsButton) ImageView settingsButton;
    private View footerSpacing;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpeechFragment.
     **/
    public static SpeechFragment newInstance() {
        SpeechFragment fragment = new SpeechFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SpeechFragment() {
        // Required empty public constructor
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speech, container, false);
        ButterKnife.bind(this, view);

        // listView
        View footerView = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        footerSpacing = (View) footerView.findViewById(R.id.footerSpace);
        listView.addFooterView(footerView);
        //TODO: get rid of overscroll graphic, allow overscroll

        listViewAdapter = new ListViewAdapter(getActivity(), allRequests, allResponses, this);
        listView.setAdapter(listViewAdapter);

        // voice recognition
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

        return view;
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

    public void doListen() {
        //TODO: make listView animate off page instead. Code below doesn't work.
        //Animation listViewAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
        //        R.anim.listview_out);
        //listView.startAnimation(listViewAnimation);
        listView.setAlpha(0);
        speechTextDisplay.setText(""); //reset what speechText says
        speechTextDisplay.setAlpha(1);

        Log.d(DEBUG_TAG, "Start listening");
        isListening = true;
        mListener.stopSpeaking();
        sr.startListening(recognizerIntent);
        updateListeningIndicator(); //sometimes there's a lag between when you click the button and when it actually starts listening (the sound) so update indicator later
    }

    public void dontListen() {
        Log.d(DEBUG_TAG, "Stop listening.");
        isListening = false;
        updateListeningIndicator();
        sr.stopListening();
    }

    private void updateListeningIndicator() {
        if (isListening)
            listenButton.setImageResource(R.drawable.yes_mic);
        else
            listenButton.setImageResource(R.drawable.no_mic);
    }

    @Override public void speechResultCallback(ArrayList voiceResult) {
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got result, stopped listening.");

        voiceInput = voiceResult;
        String firstResult = voiceInput.get(0).toString();
        speechTextDisplay.setText(firstResult);
        new RedditSearcher(this, firstResult, getActivity().getApplicationContext()).getRedditComment();
    }

    @Override public void partialCallback(ArrayList partialResult) {
        speechTextDisplay.setText(partialResult.get(0).toString());
    }

    @Override public void errorCallback(int errorCode, int numErrors) {
        isListening = false;
        updateListeningIndicator();
        Log.d(DEBUG_TAG, "Got error, stopped listening.");

        if (numErrors == 1) { // to prevent repeating errors
            if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) { // error 7
                //TODO: change this to saying out loud, "please try again"
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error: Speech was not recognized.", Toast.LENGTH_SHORT).show();
                showComment("make sense pls");
            } else if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) { //error 6
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error: Please say something.", Toast.LENGTH_SHORT).show();
                showComment("you have a mouth right");
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Error occurred! Try again.", Toast.LENGTH_SHORT).show();
                showComment("error on our side, sorry :'-(");
            }
        }
    }

    @Override public void commentCallback(String comment) {
        if (comment == null) {
            Log.d(DEBUG_TAG, "No valid comments found");
            Toast.makeText(getContext(), "No valid comments available", Toast.LENGTH_SHORT).show();
            showComment("No valid comments found");
        } else {
            showComment(comment);
        }
    }

    public void showComment(String comment){ //a comment will always show, so do it here
        //show and speak final result:
        //commentText.setText(comment);
        mListener.speak(comment);

        //add full result to history (aka listView):
        allRequests.add(voiceInput.get(0).toString());
        allResponses.add(comment);
        listViewAdapter.notifyDataSetChanged();

        //change footer view
        if(firstResponse == true) {
            //gives you height in pixels, NORA tested for accuracy
            listViewHeight = listView.getHeight();
            firstResponse = false;
        } else {
            updateFooter(listViewAdapter.getLastItemHeight());
        }

        //make listview visible and overlayed input invisible
        listView.setAlpha(1);
        speechTextDisplay.setAlpha(0);
    }

    @Override
    public void itemHeightCallback(int height) {
        itemHeight = height;
        updateFooter(itemHeight);
    }

    public void updateFooter(int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) footerSpacing.getLayoutParams();
        params.height = listViewHeight - height;
        footerSpacing.setLayoutParams(params);
        listView.setSelection(listViewAdapter.getCount() - 1);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity. See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void speak(String comment);
        void stopSpeaking();
    }

}
