package cz.michaelbrabec.fossbakalari;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefHandler {
    public static String getCurrentUserString(Context context){
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("currentUser", "");
    }
    public static void setCurrentUserString(Context context, String bakaurl, String login){
        //TODO: make it automatically get package name
        SharedPreferences prefs = context.getSharedPreferences("cz.michaelbrabec.fossbakalari_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currentUser", bakaurl + "_" + login);
        editor.apply();
        PreferenceManager
                .getDefaultSharedPreferences(context);
    }
    public String getString(Context context, String key){
        SharedPreferences prefs = context.getSharedPreferences("cz.michaelbrabec.fossbakalari_" + getCurrentUserString(context), Context.MODE_PRIVATE);

        return prefs.getString(key, "");
    }
    public static void setString(Context context, String key, String value){
        SharedPreferences prefs = context.getSharedPreferences("cz.michaelbrabec.fossbakalari_" + getCurrentUserString(context), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public Boolean getDefaultBool(Context context, String key){
        return this.getDefaultBool(context, key, false);
    }

    public Boolean getDefaultBool(Context context, String key, Boolean defValue){
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(key, defValue);
    }
}
