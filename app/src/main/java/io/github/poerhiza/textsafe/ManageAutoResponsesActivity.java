package io.github.poerhiza.textsafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

import io.github.poerhiza.textsafe.adapters.AutoResponseAdapter;
import io.github.poerhiza.textsafe.constants.Constants;
import io.github.poerhiza.textsafe.utilities.AutoResponseDataLoader;
import io.github.poerhiza.textsafe.utilities.SQLHelper;
import io.github.poerhiza.textsafe.utilities.UIMessages;
import io.github.poerhiza.textsafe.valueobjects.AutoResponse;

public class ManageAutoResponsesActivity extends Activity {
    private static final int EDIT_AUTO_RESPONSE_MENU_ID = 3;
    private static final int DELETE_AUTO_RESPONSE_ID = 2;
    private static final int USE_AUTO_RESPONSE_ID = 1;
    private static final int CREATE_AUTO_RESPONSE_ID = 0;
    private static AutoResponse selected_auto_response = null;
    private AutoResponseAdapter autoResponseEntryAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_auto_responses);

        AutoResponseDataLoader.ctx = getBaseContext();

        final ListView autoResponseListView = (ListView) findViewById(R.id.auto_response_list);

        autoResponseListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                selected_auto_response = (AutoResponse) adapter.getItemAtPosition(position);
                openMenu();
            }
        });

        autoResponseEntryAdapter = new AutoResponseAdapter(this, R.layout.auto_response_item);
        autoResponseListView.setAdapter(autoResponseEntryAdapter);
        autoResponseEntryAdapter.addAll(getAutoResponseEntries());
    }

    private List<AutoResponse> getAutoResponseEntries() {
        return AutoResponseDataLoader.getAllData();
    }


    public void bRefresh_click(View v) {
        refreshView();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_manage_auto_responses, menu);
        return true;
    }

    protected void openMenu() {
        openOptionsMenu();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent newItem = null;

        switch (item.getItemId()) {
            case R.id.auto_response_create_new:
                newItem = new Intent(this, EditAutoResponseActivity.class);
                newItem.putExtra(Constants.AUTO_RESPONSE_RETURN_VALUE, EditAutoResponseActivity.STATE_NEW);
                startActivityForResult(newItem, Constants.SET_AUTO_RESPONSE);
                return true;
            case R.id.auto_response_edit:
                newItem = new Intent(this, EditAutoResponseActivity.class);
                newItem.putExtra(Constants.AUTO_RESPONSE_RETURN_VALUE, selected_auto_response.getID());
                startActivityForResult(newItem, Constants.SET_AUTO_RESPONSE);
                return true;
            case R.id.auto_response_delete:
                deleteAutoResponse(selected_auto_response.getID());
                return true;
            case R.id.auto_response_use:
                returnResponse(selected_auto_response);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAutoResponse(int id) {
        final int arID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        builder.setTitle("Remove Response?"); // TODO: move string constants into strings file...gesh...
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SQLHelper sqlHelper = new SQLHelper(getBaseContext());
                sqlHelper.deleteAutoResponse(new AutoResponse(arID, "", ""));
                refreshView();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void refreshView() {
        autoResponseEntryAdapter.clear();
        autoResponseEntryAdapter.addAll(getAutoResponseEntries());
        autoResponseEntryAdapter.notifyDataSetChanged();
    }

    private void returnResponse(AutoResponse autoResponse) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.AUTO_RESPONSE_RETURN_VALUE, autoResponse);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SET_AUTO_RESPONSE) {
            if (resultCode == RESULT_OK) {
                AutoResponse result = (AutoResponse) data.getExtras().get(Constants.AUTO_RESPONSE_RETURN_VALUE);
                returnResponse(result);
            } else if (resultCode == RESULT_FIRST_USER) {
                refreshView();
                UIMessages.toast(getBaseContext(), getString(R.string.auto_response_updated));//TODO: move to strings
            } else if (resultCode == RESULT_CANCELED) {
                UIMessages.toast(getBaseContext(), getString(R.string.edit_auto_response_canceled));//TODO: move to strings
            }
        }
    }

    public boolean onMenuOpened(int featureid, Menu menu) {
        if (menu != null) {
            if (selected_auto_response == null) {
                MenuItem tmp = null;
                tmp = menu.getItem(CREATE_AUTO_RESPONSE_ID);

                if (tmp != null)
                    tmp.setVisible(true);

                tmp = menu.getItem(EDIT_AUTO_RESPONSE_MENU_ID);

                if (tmp != null)
                    tmp.setVisible(false);

                tmp = menu.getItem(USE_AUTO_RESPONSE_ID);

                if (tmp != null)
                    tmp.setVisible(false);

                tmp = menu.getItem(DELETE_AUTO_RESPONSE_ID);

                if (tmp != null)
                    tmp.setVisible(false);

            } else {
                MenuItem tmp = null;
                tmp = menu.getItem(CREATE_AUTO_RESPONSE_ID);

                if (tmp != null)
                    tmp.setVisible(false);

                tmp = menu.getItem(EDIT_AUTO_RESPONSE_MENU_ID);

                if (tmp != null)
                    tmp.setVisible(true);

                tmp = menu.getItem(USE_AUTO_RESPONSE_ID);

                if (tmp != null)
                    tmp.setVisible(true);

                tmp = menu.getItem(DELETE_AUTO_RESPONSE_ID);

                if (tmp != null)
                    tmp.setVisible(true);
            }
        }

        return true;
    }

    public void onPanelClosed(int featureId, Menu menu) {
        selected_auto_response = null;
    }
}