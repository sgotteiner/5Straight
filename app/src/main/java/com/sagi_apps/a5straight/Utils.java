package com.sagi_apps.a5straight;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static void saveName(String name, Activity activity) {

        SharedPreferences sharedPreferences = activity.getSharedPreferences("App_Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("first_name", name);
        editor.commit();
    }

    public static String getName(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("App_Settings", MODE_PRIVATE);
        String name = sharedPreferences.getString("first_name", "");

        return name;
    }

    public static void saveIdPlayer(Activity activity,String idPlayer) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("App_Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id_player", idPlayer);
        editor.commit();
    }
    public static String getIdKeyPlayer(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("App_Settings", MODE_PRIVATE);
        String idKeyPlayer = sharedPreferences.getString("id_player", "");
        return idKeyPlayer;
    }
}
