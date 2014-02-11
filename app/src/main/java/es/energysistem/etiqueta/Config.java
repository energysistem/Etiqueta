package es.energysistem.etiqueta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import es.energy.myapplication.R;

/**
 * Created by Adrián. on 20/11/13.
 */
public class Config extends Activity {
    private SharedPreferences prefs;
    private TabHost tabHost;
    private Boolean check_multiple=false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        CheckBox checkBoxMultiple =(CheckBox)findViewById(R.id.checkBoxMultiple);
        final EditText text1=(EditText)findViewById(R.id.editText1);
        final EditText text2=(EditText)findViewById(R.id.editText2);
        final EditText text3=(EditText)findViewById(R.id.editText3);
        Button botonBuscar= (Button)findViewById(R.id.buttonBuscar);
        final Button botonConfirmarProduct=(Button)findViewById(R.id.buttonConfirmarProduct);
        final Button botonConfirmarPosicion=(Button)findViewById(R.id.buttonConfirmarPosicion);
        final RadioButton radioButtonDerecha=(RadioButton)findViewById(R.id.radioButtonDerecha);
        final RadioButton radioButtonIzquierda=(RadioButton)findViewById(R.id.radioButtonIzquierda);
        final WebView webViewPre=(WebView) findViewById(R.id.webViewPre);
        WebSettings webSettings = webViewPre.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewPre.setInitialScale(50);
        //Inicializo las preferencias
        prefs= getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


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
                    new AlertDialog.Builder(Config.this)
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
                GuardarValor("Conf_Producto");
                //inserto en las pref que la pestaña ha sido completada
                SharedPreferences.Editor editor= prefs.edit();
                editor.putString("url", webViewPre.getUrl());
                editor.commit();

            }
        });
        botonConfirmarPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EstablecerOrientacion(radioButtonDerecha,radioButtonIzquierda);
                GuardarValor("Conf_Posicion");
            }
        });

    }
    private void EstablecerOrientacion(RadioButton radioButtonDerecha, RadioButton radioButtonIzquierda )
    {
        if(radioButtonDerecha.isChecked())
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
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
        //Toast.makeText(Config.this,text1.getText(), Toast.LENGTH_SHORT).show();
    }
    private void GuardarValor(String pestaña)
    {
        //inserto en las pref que la pestaña ha sido completada
        SharedPreferences.Editor editor= prefs.edit();
        editor.putBoolean(pestaña, true);
        editor.commit();


    }

}
