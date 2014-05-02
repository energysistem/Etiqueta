package es.energysistem.etiqueta.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

import es.energy.myapplication.R;

/**
 * Created by Adrián. on 19/11/13.
 */
public class SplashScreenActivity extends BaseActivity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 6000;
    private boolean primera_vez;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Me espero unos segundos a que acabe de encenderse el tablet
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash_screen);

        //Comprueba si es la primera vez que se abre la apliación
        primera_vez = preferencesManager.isAppFirstTime();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // Start the next activity
                // si es la primera vez, habro el panel de configuración
                //TODO: estaria bien comprobar que exixten las preferencias necesarias
                Intent mainIntent = null;
                if (primera_vez) {
                    mainIntent = new Intent().setClass(SplashScreenActivity.this, ConfigActivity.class);
                    startActivity(mainIntent);
                } else { //si no es la primera vez abro directamente la etiqueta.
                    mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
                }
                startActivity(mainIntent);

                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}