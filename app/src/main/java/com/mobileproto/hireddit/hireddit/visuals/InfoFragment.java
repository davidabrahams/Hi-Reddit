package com.mobileproto.hireddit.hireddit.visuals;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.mobileproto.hireddit.hireddit.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Contains all information on the info page.
 */
public class InfoFragment extends Fragment {

    @Bind(R.id.fast) RadioButton fastButton;
    @Bind(R.id.medium) RadioButton mediumButton;
    @Bind(R.id.slow) RadioButton slowButton;

    private static final String DEBUG_TAG = "InfoFragment Debug";


    private NumberCommentsToSearchCallback cb;

    /**
     * @return A new instance of fragment InfoFragment.
     */
    public static InfoFragment newInstance(NumberCommentsToSearchCallback cb) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setCallback(cb);
        return fragment;
    }

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);

        fastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setCommentsToSearch(1);
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setCommentsToSearch(5);
            }
        });

        slowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setCommentsToSearch(10);
            }
        });

        setSelection();

        return view;
    }

    private void setSelection() {
        Log.d(DEBUG_TAG, "Setting selection to :" + cb.getCommentsToSearch());
        switch (this.cb.getCommentsToSearch()) {
            case 1:
                fastButton.setChecked(true);
                break;
            case 5:
                mediumButton.setChecked(true);
                break;
            case 10:
                slowButton.setChecked(true);
                break;

        }
    }


    public interface NumberCommentsToSearchCallback {
        void setCommentsToSearch(int c);

        int getCommentsToSearch();
    }

    public void setCallback(NumberCommentsToSearchCallback cb) {
        this.cb = cb;
    }
}

