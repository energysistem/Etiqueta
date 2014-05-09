package es.energysistem.etiqueta.ui.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.List;

import es.energy.myapplication.R;
import es.energysistem.etiqueta.StatusBarAdmin;
import es.energysistem.etiqueta.services.StartStopService;

public class MainActivity extends BaseActivity {

    private String Url;
    private StatusBarAdmin statusBarAdmin;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activate full screen and keeps the screen on
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setContentView(R.layout.activity_main);

        // Configure WebView
        WebView webView = (WebView) this.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.clearCache(true);
        GenerateUrl();
        webView.loadUrl(Url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("Ha cambiado la url", url);
                Url = url;
                GenerateUrl();
            }

        });
        webView.reload();

        // Crea icono en el escritorio
        // CrearIconoLauncher();

        // Configure hidden button
        Button button = (Button) this.findViewById(R.id.BConfig);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePopupDialog();
            }
        });

        // Start service
        if(!isStartStopServiceRunning()) {
            Intent myIntent = new Intent(this, StartStopService.class);
            startService(myIntent);
        }

        // Register DeviceAdmin
        deviceAdmin.registerDeviceAdmin();
        // Configure StatusBarAdmin
        statusBarAdmin = new StatusBarAdmin();
    }

    private boolean isStartStopServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);

        if (runningServices != null) {
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (StartStopService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void CreatePopupDialog() {

        // Get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // Set dialog message
        alertDialogBuilder.setCancelable(false);
        //TODO: Create string resource for positive button
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get user input and set it to result edit text
                        if(userInput.getText() != null) {
                            String inputText = userInput.getText().toString();

                            if (inputText.compareTo("5603") == 0) {
                                //Open Configuration Activity
                                Intent mainIntent = new Intent().setClass(MainActivity.this, ConfigActivity.class);
                                startActivity(mainIntent);
                            } else {
                                //Password Error
                                Toast.makeText(MainActivity.this, R.string.error_pass, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Password Empty
                            //TODO: Change error string to empty password string
                            Toast.makeText(MainActivity.this, R.string.error_pass, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //TODO: Create string resource for negative button
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );

        // Create alert dialog & show it
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void GenerateUrl() {
        boolean urlExist = preferencesManager.urlExist();

        if (!urlExist) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Show notification bar
        statusBarAdmin.ShowStatusBar();
        Log.d("DEBUG", "On Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Hide notification bar
        statusBarAdmin.HideStatusBar();
    }

    @Override
    public void onBackPressed() {

    }
}