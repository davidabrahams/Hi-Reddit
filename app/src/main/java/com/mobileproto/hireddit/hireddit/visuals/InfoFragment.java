package com.mobileproto.hireddit.hireddit.visuals;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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


    NumberCommentsToSearchCallback cb;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
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

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setCommentsToSearch(10);
            }
        });

        return view;
    }


    public interface NumberCommentsToSearchCallback {
        void setCommentsToSearch(int c);
    }

    public void setCallback(NumberCommentsToSearchCallback cb) {
        this.cb = cb;
    }
}

