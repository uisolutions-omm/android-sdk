package co.onmymobile.wifimodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ${Bharath} on 1/7/2016
 */
public class WifiAppPreferences {

    /**
     * Types of preferences
     */
    public static enum PrefType {
        user_id,
        reg,
        wifi,
        phnum,
        source,
        checkInLater;


    }

    public static void add(PrefType name, int value, Context context) {
        // Context appContext = context.getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(name.toString(), value);
        editor.commit();
    }

    public static void add(PrefType name, float value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(name.toString(), value);
        editor.commit();
    }

    public static void add(PrefType name, String value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name.toString(), value);
        editor.commit();
    }

   /* public static void add(PrefType name, ArrayList<String> value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name.toString(), String.valueOf(value));
        editor.commit();
    }*/

    public static void add(PrefType name, boolean value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name.toString(), value);
        editor.commit();
    }

    public static int getInt(PrefType name, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getInt(name.toString(), 0);
    }

    public static double getDouble(PrefType name, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getFloat(name.toString(), 0f);
    }

    public static String getString(PrefType name, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getString(name.toString(), null);
    }

    public static boolean getBoolean(PrefType name, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getBoolean(name.toString(), false);
    }

    public static boolean hasString(PrefType name, Context context) {
        return (getString(name, context) != null);
    }
}
