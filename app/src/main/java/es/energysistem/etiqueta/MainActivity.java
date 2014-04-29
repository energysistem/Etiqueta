package es.energysistem.etiqueta;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import es.energy.myapplication.R;
import es.energysistem.etiqueta.services.StartStopService;

public class MainActivity extends Activity {

    final Context context = this;
    private ProgressDialog pDialog;
    private ProgressBar pbarProgreso;
    public boolean valor = true;
    public boolean apagar_encenter = true;
    public int contador = 1;
    private SharedPreferences prefs;
    //WebView webView;
    String Url;
    private PowerManager pm;
    KeyguardManager km;
    KeyguardManager.KeyguardLock kl;
    PowerManager.WakeLock wl;
    private int defTimeOut;
    private static final int DELAY = 1;
    private static final int DELAYORIG = 1800000;
    String hora_apagado;
    String hora_encendido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*try {
            //Me espero unos segundos a que acabe de encenderse el tablet
            Thread.sleep(9000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("MyKeyguardLock");
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

        //Pantalla completa y mantiene la pantalla encendida
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setContentView(R.layout.activity_main);
        //Inicializo las preferencias
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        //establecer orientación
        if (prefs.getString("orientation", "SCREEN_ORIENTATION_PORTRAIT") == "SCREEN_ORIENTATION_PORTRAIT") {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (prefs.getString("orientation", "SCREEN_ORIENTATION_REVERSE_PORTRAIT") == "SCREEN_ORIENTATION_REVERSE_PORTRAIT") {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }

        //webView = new WebView(this);

        WebView webView = (WebView) this.findViewById(R.id.webView);
        Button button = (Button) this.findViewById(R.id.BConfig);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.clearCache(true);
//        tarea1 = new MiTareaAsincrona();
//        tarea1.execute();

        GenerarUrl();
        webView.loadUrl(Url);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // can get Here
                view.getUrl();
                //Now set to TExtView

                Log.i("Ha cambiado la url", url);
                Url = url;
                GenerarUrl();

            }

        });

        webView.reload();
        //Elimina la barra inferior
        KillStatusBar();
        //Crea icono en el escritorio
        CrearIconoLauncher();

        /*//desbloquea y mantiene encendida la pantalla
        kl.disableKeyguard();
        wl.acquire();
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, DELAYORIG);*/


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VentanaEmergente();
            }
        });

        if(!isMyServiceRunning()) {
            Intent myIntent = new Intent(context, StartStopService.class);
            startService(myIntent);
        }

        kl.disableKeyguard();
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StartStopService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void VentanaEmergente() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                //result.setText(userInput.getText());
                                Toast.makeText(MainActivity.this, userInput.getText().toString(), Toast.LENGTH_SHORT).show();

                                if (userInput.getText().toString().compareTo("5603") == 0) {
                                    //abre el panel de configuración
                                    //error en la contraseña
                                    //Toast.makeText(MainActivity.this, "CONTRASEÑA CORRECTA!", Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent().setClass(MainActivity.this, Config.class);
                                    startActivity(mainIntent);
                                } else {
                                    //error en la contraseña
                                    Toast.makeText(MainActivity.this, findViewById(R.string.error_pass).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            event.startTracking(); // Needed to track long presses
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            //event.startTracking(); // Needed to track long presses
            return true;
        }
        Log.d("DEBUG", KeyEvent.keyCodeToString(keyCode));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            VentanaEmergente();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private void GenerarUrl() {
        SharedPreferences.Editor editor = prefs.edit();
        boolean Url_existe = prefs.getBoolean(("existe_url"), false);

        if (Url_existe == false) {
            //inserto en las pref que la nueva url
            editor.putBoolean("existe_url", true);
            Url = "http://www.energysistem.com/tools/tiendas/etiquetas/index.html";
            editor.putString("url", Url);
            editor.commit();

        } else {
            if (Url == null)
                Url = prefs.getString(("url"), "http://www.energysistem.com/tools/tiendas/etiquetas/index.html");
            else {
                editor.putString("url", Url);
                editor.commit();
            }

        }
    }

    private void CrearIconoLauncher() {
        SharedPreferences.Editor editor = prefs.edit();

        boolean valor_icono = prefs.getBoolean("icono_launcher", false);
        if (valor_icono == false) {
            //where this is a context (e.g. your current activity)
            final Intent shortcutIntent = new Intent(this, MainActivity.class);

            final Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            // Sets the custom shortcut's title
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            // Set the custom shortcut icon
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
            // add the shortcut
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            sendBroadcast(intent);

            //inserto en las pref que ya hay un icono en el escritorio
            editor.putBoolean("icono_launcher", true);
            editor.commit();
        }
    }

    private void KillStatusBar() {
        Process proc = null;

        String ProcID = "79"; //HONEYCOMB AND OLDER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ProcID = "42"; //ICS AND NEWER
        }

        try {
            proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity " + ProcID + " s16 com.android.systemui"});
        } catch (IOException e) {
            Log.d("TAG", "Failed to kill task bar (1).");
            e.printStackTrace();
        }

        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            Log.d("TAG", "Failed to kill task bar (2).");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DEBUG", "On Pause");
    }
}