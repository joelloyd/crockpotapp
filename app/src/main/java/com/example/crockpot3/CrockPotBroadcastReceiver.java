package com.example.crockpot3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class CrockPotBroadcastReceiver extends BroadcastReceiver { // class for handling broadcast events

    @Override
    public void onReceive(Context context, Intent intent) {

        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){ // handling changes in connectivity for notifying user
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
            );
            if(noConnectivity){ // if the user has no internet connection, inform the user
                Toast.makeText(context, "No Internet Connection :(", Toast.LENGTH_LONG).show();
            }
            else{ // otherwise, inform the user that their connection is fine
                Toast.makeText(context, "Connected to the Internet :)", Toast.LENGTH_SHORT).show();
            }
        }

        NotificationHelper helper = new NotificationHelper(context);
        NotificationCompat.Builder notifBuilder = helper.getChannelNotification();
        //helper.getManager().notify(1, notifBuilder.build());

    }

}
