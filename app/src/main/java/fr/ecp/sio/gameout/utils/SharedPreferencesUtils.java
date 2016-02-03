package fr.ecp.sio.gameout.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cquenum on 27/01/16.
 */
public class SharedPreferencesUtils {
    private static final String PREFERENCES_FILE = "gameout_settings";

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }


}
