package es.energysistem.etiqueta.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import es.energysistem.etiqueta.PreferencesManager;
import es.energysistem.etiqueta.DeviceAdmin;

/**
 * Created by Vicente Giner Tendero on 30/04/2014.
 */
public class BaseActivity extends Activity {

    protected DeviceAdmin deviceAdmin;
    protected PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceAdmin = new DeviceAdmin(this);
        preferencesManager = new PreferencesManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOrientation();
    }

    protected void checkOrientation() {
        int orientation = preferencesManager.getOrientation();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == DeviceAdmin.DEVICE_ADMIN_REQUEST) {
//            if (resultCode != RESULT_OK) {
//                System.exit(0);
//            }
//        }
//    }
}