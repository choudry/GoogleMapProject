package com.satellite.gps;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by CH_M_USMAN on 28-Jul-17.
 */

public class Utill {

    public static String MyPrefrences = "MyPrefs";
    static SharedPreferences sharedPreferences;

    public static void addDataSP(String key,String value,Context context){
        sharedPreferences = context.getSharedPreferences(MyPrefrences, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getDataSP(String key,Context context){
        if (context!=null){
            sharedPreferences = context.getSharedPreferences(MyPrefrences, 0);
            return  sharedPreferences.getString(key,null);
        }else {
            return null;
        }

    }

    public static void removeDataSP(String key,Context context){
        sharedPreferences = context.getSharedPreferences(MyPrefrences, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean verifyConection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }else
            return false;

    }




    public static void showToast(String value,Context context){
        ((Toast.makeText(context,value,Toast.LENGTH_SHORT))).show();
    }
}
