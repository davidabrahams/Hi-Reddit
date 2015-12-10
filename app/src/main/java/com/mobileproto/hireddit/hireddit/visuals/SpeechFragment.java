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
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        RedditSearcher.CommentCallback {
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

    @Bind(R.id.listView) ListView listView;
    @Bind(R.id.listenButton) ImageView listenButton;
    @Bind(R.id.helloReddit) TextView helloReddit;
    @Bind(R.id.speechText) TextView speechTextDisplay;
    //@Bind(R.id.commentText) TextView commentText;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speech, container, false);
        ButterKnife.bind(this, view);

        // listView
        View footerView = ((LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null, false);
        listView.addFooterView(footerView);
//        listView.setOnDetectScrollListener(new OnDetectScrollListener() {
//            @Override
//            public void onUpScrolling() {
//                Log.d(DEBUG_TAG, "Scrolling up");
//            }
//
//            @Override
//            public void onDownScrolling() {
//                Log.d(DEBUG_TAG, "Scrolling down");
//            }
//        });
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == 0) Log.d(DEBUG_TAG, "Not scrolling.");
//                if (view.getId() == listView.getId()) {
//                    int currentFirstVisibileItem = listView.get
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
        listViewAdapter = new ListViewAdapter(getActivity(), allRequests, allResponses);
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

    private int getTotalHeightofListView() {
        return listView.getHeight();     //<-- Gives you height in pixels, NORA tested for accuracy.
    }

    private int getHeightofLastListViewElement() {
        Log.d(DEBUG_TAG, "element you're at: " + (listViewAdapter.getCount() - 1));
        View mView = listViewAdapter.getView(listViewAdapter.getCount() - 1, null, listView);
        mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int itemHeight = mView.getMeasuredHeight() + listView.getDividerHeight();
        return itemHeight;
        //return mView.getHeight();
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
    public void commentCallback(String comment) {
        if (comment == null) {
            Log.d(DEBUG_TAG, "No valid comments found");
            Toast.makeText(getContext(), "No valid comments available", Toast.LENGTH_SHORT).show();
        } else {
            //commentText.setText(comment);
            mListener.speak(comment);
            //only if you get the full request and response, add to history:
            allRequests.add(voiceInput.get(0).toString());
            allResponses.add(comment);
            listViewAdapter.notifyDataSetChanged();

            if(firstResponse == true) {
                listViewHeight = getTotalHeightofListView();
                firstResponse = false;
            } else{
                int lastHeight = getHeightofLastListViewElement();
                Log.d(DEBUG_TAG, "lastHeight: " + lastHeight + ", totalHeight: " + listViewHeight);
                //footerHeight = totalHeight - lastHeight;
                //do magic footer making
                //more finageling
            }

            //go to end of list to see only current response
            listView.smoothScrollToPosition(listViewAdapter.getCount() - 1);

        }
    }

    //D/SpeechFragment Debug: lastHeight: 288, totalHeight: 1920
    //D/SpeechFragment Debug: lastHeight: 204, totalHeight: 1920
    //D/SpeechFragment Debug: lastHeight: 288, totalHeight: 1920
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
