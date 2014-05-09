package es.energysistem.etiqueta.ui.activities;

import android.content.Intent;
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
    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash_screen);

        //Comprueba si es la primera vez que se abre la apliación
        firstTime = preferencesManager.isAppFirstTime();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Start the next activity
                //TODO: would be nice to check that the necessary preferences exist
                Intent mainIntent;
                if (firstTime) {  // If it's the first time I open the ConfigActivity
                    mainIntent = new Intent().setClass(SplashScreenActivity.this, ConfigActivity.class);
                    startActivity(mainIntent);
                } else { // If not the first time I open the MainActivity directly.
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
