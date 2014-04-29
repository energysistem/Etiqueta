package es.energysistem.etiqueta.services;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import es.energysistem.etiqueta.SplashScreenActivity;
import es.energysistem.etiqueta.recivers.AdminReceiver;

/**
 * Created by Vicente Giner Tendero on 29/04/2014.
 */
public class StartStopService extends Service {

    private SharedPreferences preferences;
    private final long tiempoActualizacion = 30000;
    private KeyguardManager.KeyguardLock keyguardLock;
    private PowerManager.WakeLock wakeLock;
    private DevicePolicyManager deviceManager;
    private ComponentName componentName;

    public StartStopService() {

    }

    @Override
    public void onCreate(){
        // Create a timer to check the screen on/off time.
        Timer timer = new Timer();
        timer.schedule(task, tiempoActualizacion, tiempoActualizacion);

        // Get the preferences to check the screen on/off time.
        preferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        // Get the KeyguardLock and WakeLock for put screen on.
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        keyguardLock = km.newKeyguardLock("MyKeyguardLock");

        // Get the DevicePolicyManager for lock the screen.
        deviceManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        // Get component name for AdminReceiver class
        componentName = new ComponentName(this, AdminReceiver.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        // To maintain service execution return START_STICKY parameter
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // Get the current time and format it
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String nowString = format.format(c.getTime());

            // Get the preferences on/off time to compare
            String hora_encendido = preferences.getString("hora_encendido", "00:00");
            String hora_apagado = preferences.getString("hora_apagado", "00:00");

            //Compare the times and do the corresponding action
            if(hora_encendido.equals(nowString)) {
                OpenApp();
                UnlockScreen();
            } else if(hora_apagado.equals(nowString)) {
                LockScreen();
            }
        }
    };

    private void LockScreen() {
        // Lock screen
        keyguardLock.reenableKeyguard();
        if (deviceManager.isAdminActive(componentName)) {
            deviceManager.lockNow();
        }
        Log.d("Screen", "LOCK");
    }

    public void UnlockScreen() {
        // Unlock screen
        keyguardLock.disableKeyguard();
        wakeLock.acquire();
        Log.d("Screen", "UNLOCK");
    }

    private void OpenApp() {
        // Launch Application
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
