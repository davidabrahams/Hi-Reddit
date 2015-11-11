package com.mobileproto.hireddit.hireddit;

import android.content.Context;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
=======
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
>>>>>>> refs/remotes/origin/ui
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

<<<<<<< HEAD
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
=======
public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener, SpeakFragment.OnFragmentInteractionListener
{

    FragmentManager manager;

    public static final int HOME_SCREEN_CLICK = 1;

>>>>>>> refs/remotes/origin/ui
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MainFragment mainFragment = new MainFragment();
        ft.replace(R.id.container, mainFragment);
        ft.commit();
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        THIS CODE CAME WITH THE PRE-BUILT ACTIVITY BUT I DON'T THINK WE HAVE A USE FOR IT RIGHT NOW

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
=======
        switchFragment(MainFragment.newInstance());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
>>>>>>> refs/remotes/origin/ui
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    private void switchFragment(Fragment f)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, f).addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(int transition)
    {
        switch (transition)
        {
            case HOME_SCREEN_CLICK:
                switchFragment(SpeakFragment.newInstance());
                break;
        }
    }
}
