package es.energysistem.etiqueta.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.energy.myapplication.R;
import es.energysistem.etiqueta.DeviceAdmin;

/**
 * Created by Adrián. on 20/11/13.
 */
public class ConfigActivity extends BaseActivity{
    private TabHost tabHost;
    private Boolean check_multiple=false;
    private TimePicker timePickerEncender;
    private TimePicker timePickerApagado;
    private RadioButton radioButtonDerecha;
    private RadioButton radioButtonIzquierda;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        CheckBox checkBoxMultiple =(CheckBox)findViewById(R.id.checkBoxMultiple);
        final EditText text1=(EditText)findViewById(R.id.editText1);
        final EditText text2=(EditText)findViewById(R.id.editText2);
        final EditText text3=(EditText)findViewById(R.id.editText3);
        Button botonBuscar= (Button)findViewById(R.id.buttonBuscar);
        final Button botonConfirmarProduct=(Button)findViewById(R.id.buttonConfirmarProduct);
        final Button botonConfirmarPosicion=(Button)findViewById(R.id.buttonConfirmarPosicion);
        final Button botonConfirmarHora=(Button)findViewById(R.id.buttonConfirmarHora);
        timePickerEncender=(TimePicker)findViewById(R.id.timePickerEncendido);
        timePickerApagado=(TimePicker)findViewById(R.id.timePickerApagado);
        radioButtonDerecha=(RadioButton)findViewById(R.id.radioButtonDerecha);
        radioButtonIzquierda=(RadioButton)findViewById(R.id.radioButtonIzquierda);
        final WebView webViewPre=(WebView) findViewById(R.id.webViewPre);
        WebSettings webSettings = webViewPre.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewPre.setInitialScale(50);
        //Inicializo las preferencias
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        timePickerApagado.setIs24HourView(true);
        timePickerEncender.setIs24HourView(true);

        tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec1=tabHost.newTabSpec("TAB 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(getApplicationContext().getString(R.string.tab1));


        TabHost.TabSpec spec2=tabHost.newTabSpec("TAB 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator(getApplicationContext().getString(R.string.tab2));



        TabHost.TabSpec spec3=tabHost.newTabSpec("TAB 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator(getApplicationContext().getString(R.string.tab3));


        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        checkBoxMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {

                    text2.setVisibility(View.VISIBLE);
                    text3.setVisibility(View.VISIBLE);
                    check_multiple=true;
                }
                else
                {
                    text2.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                    check_multiple=false;
                }
            }
        });
        botonBuscar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(text1.length() !=5 || ((text2.getVisibility()==View.VISIBLE && text2.length()!=5) || (text3.getVisibility()==View.VISIBLE && text3.length()!=5)) )
                {
                    new AlertDialog.Builder(ConfigActivity.this)
                            .setTitle(getApplicationContext().getString(R.string.atencion))
                            .setMessage(getApplicationContext().getString(R.string.mensaje_codigo))
                            .setPositiveButton(getApplicationContext().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .show();
                }
                else
                {
                    CargarProduct(webViewPre, text1, text2, text3);
                    botonConfirmarProduct.setEnabled(true);

                }
            }
        });
        botonConfirmarProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.setProductConfigured(true);
                //inserto en las pref que la pestaña ha sido completada
                preferencesManager.setUrl(webViewPre.getUrl());
            }
        });
        botonConfirmarPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EstablecerOrientacion(radioButtonDerecha,radioButtonIzquierda);
                preferencesManager.setPositionConfigured(true);
            }
        });
        botonConfirmarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                preferencesManager.setTimeConfigured(true);
                //Log.d("DEBUG",(timePickerEncender.getCurrentHour())+":"+timePickerEncender.getCurrentMinute());
                //Log.d("DEBUG",(timePickerApagado.getCurrentHour())+":"+timePickerApagado.getCurrentMinute());
                //inserto el valor de la hora en settings

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePickerEncender.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePickerEncender.getCurrentMinute());

                preferencesManager.setStartTime(format.format(calendar.getTime()));

                calendar.set(Calendar.HOUR_OF_DAY, timePickerApagado.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePickerApagado.getCurrentMinute());
                preferencesManager.setEndTime(format.format(calendar.getTime()));
                ComprobarValidacionTabs();

            }
        });

        configureTimePickers();
        configureRadioButtonsOrientation();

        DeviceAdmin deviceAdmin = new DeviceAdmin(this);
        deviceAdmin.registerDeviceAdmin();
    }

    private void configureTimePickers() {
        String hora_encendido = preferencesManager.getStartTime();
        String hora_apagado = preferencesManager.getEndTime();

        SimpleDateFormat  format = new SimpleDateFormat("HH:mm");
        Date dateOn = null;
        Date dateOff = null;
        try {
            dateOn = format.parse(hora_encendido);
            dateOff = format.parse(hora_apagado);
        } catch (ParseException e) {
            Log.d("Error", "invalid date");
        }

        Calendar c = Calendar.getInstance();
        c.setTime(dateOn);
        timePickerEncender.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePickerEncender.setCurrentMinute(c.get(Calendar.MINUTE));

        c.setTime(dateOff);
        timePickerApagado.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePickerApagado.setCurrentMinute(c.get(Calendar.MINUTE));
    }

    private void configureRadioButtonsOrientation() {
        int orientation = preferencesManager.getOrientation();

        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            radioButtonDerecha.setChecked(true);
            radioButtonIzquierda.setChecked(false);
        } else {
            radioButtonIzquierda.setChecked(true);
            radioButtonDerecha.setChecked(false);
        }
    }

    private void ComprobarValidacionTabs() {
         //si los valores ya estan
        if(preferencesManager.isTimeConfigured() &&
                preferencesManager.isPositionConfigured() &&
                preferencesManager.isProductConfigured()) {
            Intent mainIntent = new Intent().setClass(ConfigActivity.this, MainActivity.class);
            startActivity(mainIntent);
            preferencesManager.setAppFirtTime(false);
        }
    }

    private void EstablecerOrientacion(RadioButton radioButtonDerecha, RadioButton radioButtonIzquierda )
    {
        if(radioButtonDerecha.isChecked())
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            preferencesManager.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            preferencesManager.setOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
    }

    private void CargarProduct(WebView webViewPre, EditText text1, EditText text2, EditText text3 )
    {
        if(check_multiple==false)
        {
            webViewPre.loadUrl("http://www.energysistem.com/tools/tiendas/etiquetas/product-whitebuttons.html?code="+text1.getText());
        }
        else
        {
            //url completa http://energysistem.com/tools/tiendas/etiquetas/selector.html?number=3&code1=39177&code2=39177&code3=39177&02-12-2013_17:36
            webViewPre.loadUrl("http://energysistem.com/tools/tiendas/etiquetas/selector.html?number=3&code1="+text1.getText()+"&code2="+text2.getText()+"&code3="+text3.getText());
        }
        //Debug:
        //Toast.makeText(ConfigActivity.this,text1.getText(), Toast.LENGTH_SHORT).show();
    }
}
