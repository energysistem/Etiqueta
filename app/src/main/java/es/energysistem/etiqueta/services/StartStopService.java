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

import es.energysistem.etiqueta.PreferencesManager;
import es.energysistem.etiqueta.ui.activities.MainActivity;
import es.energysistem.etiqueta.ui.activities.SplashScreenActivity;
import es.energysistem.etiqueta.receivers.AdminReceiver;

/**
 * Created by Vicente Giner Tendero on 29/04/2014.
 */
public class StartStopService extends Service {

    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private  KeyguardManager.KeyguardLock keyguardLock;
    private DevicePolicyManager deviceManager;
    private ComponentName componentName;
    private PreferencesManager preferencesManager;

    @Override
    public void onCreate(){
        // Create a timer to check the screen on/off time.
        long tiempoActualizacion = 30000;
        Timer timer = new Timer();
        timer.schedule(task, tiempoActualizacion, tiempoActualizacion);

        // Get the preferences to check the screen on/off time.
        preferencesManager = new PreferencesManager(this);

        // Get the KeyguardLock and WakeLock for put screen on.
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
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
            String hora_encendido = preferencesManager.getStartTime();
            String hora_apagado = preferencesManager.getEndTime();

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
        keyguardLock.reenableKeyguard();
        releaseWakeLock();
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
        if (deviceManager.isAdminActive(componentName)) {
            deviceManager.lockNow();
        }
        Log.d("Screen", "LOCK");
    }

    private void UnlockScreen() {
        keyguardLock.disableKeyguard();
        releaseWakeLock();
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyWakeLock");
        wakeLock.acquire();
        Log.d("Screen", "UNLOCK");
    }

    private void releaseWakeLock() {
        if(wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void OpenApp() {
        // Launch Application
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
