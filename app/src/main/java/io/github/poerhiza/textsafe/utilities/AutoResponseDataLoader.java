package io.github.poerhiza.textsafe.utilities;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.github.poerhiza.textsafe.valueobjects.AutoResponse;


public class AutoResponseDataLoader {
    public static final String TAG = AutoResponseDataLoader.class.getSimpleName();
    public static Context ctx = null;
    public static SQLHelper sqlHelper = null;

    public static List<AutoResponse> getAllData() {
        if (ctx == null) {
            Log.i(TAG, "You need to give me a context!");
            return null;
        }
        if (sqlHelper == null && ctx != null) {
            sqlHelper = new SQLHelper(ctx);
        }
        List<AutoResponse> res = new ArrayList<AutoResponse>();
        res.addAll(sqlHelper.getAllAutoResponses());
        return res;
    }

    public static List<AutoResponse> getFlattenedData() {
        if (ctx == null) {
            Log.i(TAG, "You need to give me a context!");
            return null;
        }
        if (sqlHelper == null && ctx != null) {
            sqlHelper = new SQLHelper(ctx);
        }
        List<AutoResponse> res = new ArrayList<AutoResponse>();

        res.addAll(sqlHelper.getAllAutoResponses());
        return res;
    }

    public static Pair<Boolean, List<AutoResponse>> getRows(int page, int pageSize) {
        if (ctx == null) {
            Log.i(TAG, "You need to give me a context!");
            return null;
        }

        if (sqlHelper == null && ctx != null) {
            sqlHelper = new SQLHelper(ctx);
        }
        List<AutoResponse> flattenedData = getFlattenedData();

        if (pageSize > flattenedData.size()) {
            pageSize = (pageSize - (pageSize - flattenedData.size()));
        }

        if (page == 1) {
            return new Pair<Boolean, List<AutoResponse>>(
                    true,
                    flattenedData.subList(0, pageSize)
            );
        } else {
            return new Pair<Boolean, List<AutoResponse>>(
                    page * pageSize < flattenedData.size(),
                    flattenedData.subList(
                            (page - 1) * pageSize,
                            Math.min(page * pageSize, flattenedData.size())
                    )
            );
        }
    }
}
