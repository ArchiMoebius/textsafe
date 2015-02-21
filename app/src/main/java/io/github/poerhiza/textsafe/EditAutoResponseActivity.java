package io.github.poerhiza.textsafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import io.github.poerhiza.textsafe.constants.Constants;
import io.github.poerhiza.textsafe.utilities.SQLHelper;
import io.github.poerhiza.textsafe.valueobjects.AutoResponse;

public class EditAutoResponseActivity extends Activity {
    private int STATE = -1;
    public static final int STATE_NEW = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_auto_response);

        STATE = Integer.valueOf(getIntent().getExtras().get(Constants.AUTO_RESPONSE_RETURN_VALUE).toString());

        if (STATE != STATE_NEW) {
            SQLHelper sqlHelper = new SQLHelper(getBaseContext());
            AutoResponse ar = sqlHelper.getAutoResponse(STATE);

            if (ar != null) {
                ((EditText) findViewById(R.id.text_auto_response_title)).setText(ar.getTitle());
                ((EditText) findViewById(R.id.text_auto_response_response)).setText(ar.getResponse());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_auto_response, menu);
        return true;
    }

    public void applySettings(View v) {
        Intent resultIntent = new Intent();
        SQLHelper sqlHelper = new SQLHelper(getBaseContext());
        ;
        AutoResponse autoResponse = null;

        if (STATE == STATE_NEW) {
            autoResponse = new AutoResponse(
                    -1,
                    ((EditText) findViewById(R.id.text_auto_response_title)).getText().toString(),
                    ((EditText) findViewById(R.id.text_auto_response_response)).getText().toString()
            );

            long id = sqlHelper.addAutoResponse(autoResponse);

            if (id >= 0) // TODO: better error handling
                autoResponse.setID((int) id);
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            autoResponse = new AutoResponse(
                    STATE,
                    ((EditText) findViewById(R.id.text_auto_response_title)).getText().toString(),
                    ((EditText) findViewById(R.id.text_auto_response_response)).getText().toString()
            );

            sqlHelper.updateAutoResponse(autoResponse);
            setResult(Activity.RESULT_FIRST_USER, resultIntent);
        }

        resultIntent.putExtra(Constants.AUTO_RESPONSE_RETURN_VALUE, autoResponse);
        finish();
    }

    public void cancelSettings(View v) {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }
}