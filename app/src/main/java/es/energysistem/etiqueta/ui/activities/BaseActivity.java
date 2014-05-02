package es.energysistem.etiqueta.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import es.energysistem.etiqueta.DeviceAdmin;

/**
 * Created by Vicente Giner Tendero on 30/04/2014.
 */
public class BaseActivity extends Activity {

    private static final String PREFERENCES_ID = "MisPreferencias";
    private static final String SCREEN_ORIENTATION_PORTRAIT = "SCREEN_ORIENTATION_PORTRAIT";
    private static final String SCREEN_ORIENTATION_REVERSE_PORTRAIT = "SCREEN_ORIENTATION_REVERSE_PORTRAIT";
    protected SharedPreferences preferences;
    private DeviceAdmin deviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
        deviceAdmin = new DeviceAdmin(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOrientation();
        deviceAdmin.registerDeviceAdmin();
    }

    protected void checkOrientation() {
        if (preferences.getString("orientation", SCREEN_ORIENTATION_PORTRAIT).equals(SCREEN_ORIENTATION_PORTRAIT) && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (preferences.getString("orientation", SCREEN_ORIENTATION_REVERSE_PORTRAIT).equals(SCREEN_ORIENTATION_REVERSE_PORTRAIT) && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DeviceAdmin.DEVICE_ADMIN_REQUEST) {
            if (resultCode != RESULT_OK) {
                System.exit(0);
            }
        }
    }
}