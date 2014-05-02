package es.energysistem.etiqueta.ui.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
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
import es.energysistem.etiqueta.receivers.AdminReceiver;
import es.energysistem.etiqueta.services.StartStopService;

public class MainActivity extends BaseActivity {

    final Context context = this;
    private ProgressDialog pDialog;
    private ProgressBar pbarProgreso;
    public boolean valor = true;
    public boolean apagar_encenter = true;
    public int contador = 1;
    //WebView webView;
    String Url;
    private int defTimeOut;
    private static final int DELAY = 1;
    private static final int DELAYORIG = 1800000;
    String hora_apagado;
    String hora_encendido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Pantalla completa y mantiene la pantalla encendida
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setContentView(R.layout.activity_main);

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
//        CrearIconoLauncher();

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

        DevicePolicyManager deviceManager = (DevicePolicyManager)getSystemService(
                DEVICE_POLICY_SERVICE);

        ComponentName componentName = new ComponentName(this, AdminReceiver.class);

        if (!deviceManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, "Etiqueta Tienda Enregy");
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,  "Necesario para que la aplicacion funcione.");
            startActivity(intent);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
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
                                //Toast.makeText(MainActivity.this, userInput.getText().toString(), Toast.LENGTH_SHORT).show();

                                if (userInput.getText().toString().compareTo("5603") == 0) {
                                    //abre el panel de configuración
                                    //error en la contraseña
                                    //Toast.makeText(MainActivity.this, "CONTRASEÑA CORRECTA!", Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent().setClass(MainActivity.this, ConfigActivity.class);
                                    startActivity(mainIntent);
                                } else {
                                    //error en la contraseña
                                    Toast.makeText(MainActivity.this, R.string.error_pass, Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {

    }

    private void GenerarUrl() {
        boolean Url_existe = preferencesManager.urlExist();

        if (Url_existe == false) {
            //inserto en las pref que la nueva url
            preferencesManager.setUrlExist(true);
            Url = "http://www.energysistem.com/tools/tiendas/etiquetas/index.html";
            preferencesManager.setUrl(Url);

        } else {
            if (Url == null)
                Url = preferencesManager.getUrl();
            else {
                preferencesManager.setUrl(Url);
            }

        }
    }

//    private void CrearIconoLauncher() {
//        boolean valor_icono = prefs.getBoolean("icono_launcher", false);
//        if (valor_icono == false) {
//            //where this is a context (e.g. your current activity)
//            final Intent shortcutIntent = new Intent(this, MainActivity.class);
//
//            final Intent intent = new Intent();
//            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//            // Sets the custom shortcut's title
//            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
//            // Set the custom shortcut icon
//            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
//            // add the shortcut
//            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//            sendBroadcast(intent);
//
//            //inserto en las pref que ya hay un icono en el escritorio
//            editor.putBoolean("icono_launcher", true);
//            editor.commit();
//        }
//    }

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