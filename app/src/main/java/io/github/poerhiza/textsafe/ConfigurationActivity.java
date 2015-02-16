package io.github.poerhiza.textsafe;

import io.github.poerhiza.textsafe.R;
import io.github.poerhiza.textsafe.constants.Constants;
import io.github.poerhiza.textsafe.utilities.GestureManager;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

public class ConfigurationActivity extends Activity
{
    private SharedPreferences settings;
    private Editor se;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        se = settings.edit();

//=============Toggle Button Drop Incoming Start=============
        ToggleButton toggleDrop = (ToggleButton) findViewById(R.id.toggle_configuration_drop_incoming);

        if(settings.getBoolean(Constants.TOGGLE_DROP_INCOMING, false))
        {
            toggleDrop.setChecked(true);
        }

        toggleDrop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    se.putBoolean(Constants.TOGGLE_DROP_INCOMING, true);
                }
                else
                {
                    se.putBoolean(Constants.TOGGLE_DROP_INCOMING, false);
                }
                se.commit();
            }
        });
//=============Toggle Button Drop Incoming Finish=============

//=============Toggle Button WS Start=============
        ToggleButton toggleForward = (ToggleButton) findViewById(R.id.toggle_configuration_forward_to_ws);

        if(settings.getBoolean(Constants.TOGGLE_FORWARD_TO_WS, false))
        {
            toggleForward.setChecked(true);
        }

        toggleForward.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    se.putBoolean(Constants.TOGGLE_FORWARD_TO_WS, true);
                }
                else
                {
                    se.putBoolean(Constants.TOGGLE_FORWARD_TO_WS, false);
                }
                se.commit();
            }
        });
//=============Toggle Button WS Finish=============

//=============Input EditText WS To Start=============
        final EditText wsToForward = (EditText)findViewById(R.id.input_ws_to_forward_to);

        if(!settings.getString(Constants.INPUT_WS_TO_FORWARD, "").isEmpty())
        {
            wsToForward.setText(settings.getString(Constants.INPUT_WS_TO_FORWARD, ""));
        }

        wsToForward.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    se.putString(Constants.INPUT_WS_TO_FORWARD, wsToForward.getText().toString());
                    se.commit();
                    ((InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(wsToForward.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
//=============Input EditText WS To Finish=============

        final GestureManager gm = new GestureManager();

        gm.RightClass = MainActivity.class;
        gm.ctx = getBaseContext();
        gm.parent = this;

        final GestureDetector gestureDetector = new GestureDetector(gm);
        View mainview = (View) findViewById(R.id.configurationView);

        mainview.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (gestureDetector.onTouchEvent(event))
                {
                    return true;
                }
                return false;
            }
        });
    }

    public void saveWSURL(View v)
    {
        final EditText wsToForward = (EditText)findViewById(R.id.input_ws_to_forward_to);
        se.putString(Constants.INPUT_WS_TO_FORWARD, wsToForward.getText().toString()).commit();
        ((InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(wsToForward.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return true;
    }

}