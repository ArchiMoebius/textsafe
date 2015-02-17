package io.github.poerhiza.textsafe.utilities;

import android.content.Context;
import android.widget.Toast;

public class UIMessages {

    public static void toast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    } // end toast
}
