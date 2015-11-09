package com.mobileproto.hireddit.hireddit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment
{
    public String spokenString;
    public String importantWords;
    public String postComment;
    private View myFramentView;
    public TextView commentText;

    public MainFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myFramentView = inflater.inflate(R.layout.fragment_main, container, false);
        commentText = (TextView) myFramentView.findViewById(R.id.commentText);
        spokenString = "Why do my hands smell?";

        new GetWordsAsync(postComment, spokenString, importantWords, getActivity().getApplicationContext(), commentText).execute();

    return myFramentView;
    }
}
