package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.speech.SpeechCallback;
import com.mobileproto.hireddit.hireddit.speech.SpeechListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeakFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeakFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeakFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    private static final String DEBUG_TAG = "SpeechFragment Debug";
    private boolean isListening;
    private SpeechListener listener;
    private ArrayList voiceInput;
    private Intent recognizerIntent;
    private SpeechRecognizer sr;

    @Bind(R.id.helloReddit) TextView helloReddit;
    @Bind(R.id.listeningIndicator) TextView listeningIndicator;
    @Bind(R.id.listenButton) Button listenButton;
    @Bind(R.id.speechTextDisplay) TextView speechTextDisplay;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpeakFragment.
     */
    public static SpeakFragment newInstance()
    {
        SpeakFragment fragment = new SpeakFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SpeakFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_speak, container, false);
        ButterKnife.bind(this, view);
        listener = new SpeechListener(new SpeechCallback()
        {
            @Override
            public void callback(ArrayList voiceResult)
            {
                voiceInput = voiceResult;
                speechTextDisplay.setText(voiceInput.get(0).toString());
                isListening = false;
            }

            @Override
            public void errorCallback(int errorCode)
            {
                if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
                    //TODO: change this to saying out loud, "please try again"
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error: Speech was not recognized.", Toast.LENGTH_SHORT).show();
                }
                if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error: Please say something.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error occurred! Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void partialCallback(ArrayList partialResult)
            {
                speechTextDisplay.setText(partialResult.get(0).toString());
            }
        });


        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                1000); // TODO: TEST THIS
        sr = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        sr.setRecognitionListener(listener);


        doListen();

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/volkswagen-serial-bold.ttf");
        listenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    // TODO: CHANGE THIS WHEN WE HAVE A FANCY LISTENING INDICATOR
    private void updateListeningIndicator()
    {
        if (isListening)
            listeningIndicator.setText(R.string.listening_text_indicator);
        else
            listeningIndicator.setText(R.string.not_listening_text_indicator);
    }

    public void doListen()
    {
        Log.d(DEBUG_TAG, "Start listening");
        isListening = true;
        updateListeningIndicator();
        sr.startListening(recognizerIntent);
    }

    public void dontListen()
    {
        Log.d(DEBUG_TAG, "Stop listening.");
        sr.stopListening();
        isListening = false;
        updateListeningIndicator();
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
    public interface OnFragmentInteractionListener
    {
    }

}
