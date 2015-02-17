package io.github.poerhiza.textsafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.poerhiza.textsafe.constants.Constants;
import io.github.poerhiza.textsafe.utilities.GestureManager;
import io.github.poerhiza.textsafe.utilities.SQLHelper;
import io.github.poerhiza.textsafe.utilities.UIMessages;
import io.github.poerhiza.textsafe.valueobjects.AutoResponse;


public class MainActivity extends Activity {
    private static boolean AutoResponseEnabled = true;
    private SharedPreferences settings;
    private Editor se;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        se = settings.edit();

        final ImageButton autoResponseImageButton = (ImageButton) findViewById(R.id.imageButton1);

        AutoResponseEnabled = settings.getBoolean(Constants.TOGGLE_AUTO_RESPONSE, true);

        if (AutoResponseEnabled) {
            autoResponseImageButton.setImageResource(R.mipmap.auto_response_on);
            se.putBoolean(Constants.TOGGLE_AUTO_RESPONSE, true).commit();
        } else {
            autoResponseImageButton.setImageResource(R.mipmap.auto_response_on_red);
        }

        autoResponseImageButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Context ctx = getBaseContext();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (AutoResponseEnabled)
                            autoResponseImageButton.setImageResource(R.mipmap.auto_response_off);
                        else
                            autoResponseImageButton.setImageResource(R.mipmap.auto_response_off_red);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (AutoResponseEnabled)
                            autoResponseImageButton.setImageResource(R.mipmap.auto_response_on_red);
                        else
                            autoResponseImageButton.setImageResource(R.mipmap.auto_response_on);

                        AutoResponseEnabled = !AutoResponseEnabled;

                        if (AutoResponseEnabled)
                            UIMessages.toast(ctx, ctx.getString(R.string.toast_notification_auto_response_enabled));
                        else
                            UIMessages.toast(ctx, ctx.getString(R.string.toast_notification_auto_response_disabled));

                        se.putBoolean(Constants.TOGGLE_AUTO_RESPONSE, AutoResponseEnabled).commit();

                        break;
                }

                return false;
            }
        });

        String response = settings.getString(Constants.AUTO_RESPONSE_VALUE, Constants.DEFAULT_AUTO_RESPONSE);

        SQLHelper sqlHelper = null;

        if (!response.equalsIgnoreCase(Constants.DEFAULT_AUTO_RESPONSE)) {
            sqlHelper = new SQLHelper(getBaseContext());
            AutoResponse ar = sqlHelper.getAutoResponse(Integer.valueOf(response));

            if (ar != null)
                response = ar.getResponse();
            else
                response = getString(R.string.click_here_to_manage_your_auto_responses);
        } else {
            response = getString(R.string.click_here_to_manage_your_auto_responses);
        }

        ((TextView) findViewById(R.id.auto_response_in_use)).setText(response);

        final GestureManager gm = new GestureManager();

        gm.LeftClass = ConfigurationActivity.class;
        gm.ctx = getBaseContext();
        gm.parent = this;

        final GestureDetector gestureDetector = new GestureDetector(gm);
        View mainview = (View) findViewById(R.id.mainView);

        mainview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });

        if (!settings.getBoolean(Constants.HAS_APPLICATION_BEEN_SETUP, false)) {
            sqlHelper = new SQLHelper(getBaseContext());
            sqlHelper.addAutoResponse(new AutoResponse(-1, "Driving", "ar: I am currently driving and I am unable to reply; as soon as I can I will..."));
            sqlHelper.addAutoResponse(new AutoResponse(-1, "Busy", "ar: I am currently busy and I am unable to reply; as soon as I can I will..."));
            sqlHelper.addAutoResponse(new AutoResponse(-1, "Speak to the Hand", "ar: Don't bother me..."));
            se.putBoolean(Constants.HAS_APPLICATION_BEEN_SETUP, true).commit();
        }

    }

    public void onAutoResponseClick(View v) {
        startActivityForResult((new Intent(this, ManageAutoResponsesActivity.class)), Constants.SET_AUTO_RESPONSE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SET_AUTO_RESPONSE) {
            if (resultCode == RESULT_OK) {
                AutoResponse response = (AutoResponse) data.getExtras().get(Constants.AUTO_RESPONSE_RETURN_VALUE);
                se.putString(Constants.AUTO_RESPONSE_VALUE, String.valueOf(response.getID())).commit();
                ((TextView) findViewById(R.id.auto_response_in_use)).setText(response.getResponse());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}