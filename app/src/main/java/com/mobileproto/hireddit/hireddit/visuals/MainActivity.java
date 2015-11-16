package com.mobileproto.hireddit.hireddit.visuals;

import com.mobileproto.hireddit.hireddit.R;
import com.mobileproto.hireddit.hireddit.speech.WordToSpeech;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener, SpeechFragment.OnFragmentInteractionListener {
    public static WordToSpeech speech;

    FragmentManager manager;
    @Bind(R.id.toolbar) Toolbar toolbar;

    public static final int HOME_SCREEN_CLICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        switchFragment(MainFragment.newInstance());
        setSupportActionBar(toolbar);

        speech = new WordToSpeech(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment f) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.commit();
    }

    // We overload the switchFragment function to allow the user to customize the
    // transition between two fragments on a switch if they want. The two functions
    // have identical behavior outside of the animation.
    private void switchFragment(Fragment f, int customAnimationIn, int customAnimationOut) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(customAnimationIn, customAnimationOut);
        transaction.addToBackStack(null);
        transaction.replace(R.id.container, f);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(int transition) {
        switch (transition) {
            case HOME_SCREEN_CLICK:
                SpeechFragment f = SpeechFragment.newInstance();
                switchFragment(f, FragmentTransaction.TRANSIT_NONE,
                        R.anim.slide_out_up);
                break;
        }
    }

}
