package es.energysistem.etiqueta;

/**
 * Created by Adrian on 18/10/13.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i= new Intent(context,SplashScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}
