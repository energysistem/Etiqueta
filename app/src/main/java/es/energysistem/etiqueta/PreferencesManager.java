package es.energysistem.etiqueta;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

/**
 * Created by Vicente Giner Tendero on 02/05/2014.
 */
public class PreferencesManager {

    protected static final String PREFERENCES_ID = "MisPreferencias";
    protected static final String ORIENTATION_PREFERENCE_ID = "orientation";
    protected static final String FIRST_TIME_PREFERENCE_ID = "primera_vez";
    protected static final String URL_PREFERENCE_ID = "url";
    protected static final String URL_EXIST_PREFERENCE_ID = "existe_url";
    protected static final String START_TIME_PREFERENCE_ID = "hora_encendido";
    protected static final String END_TIME_PREFERENCE_ID = "hora_apagado";
    protected static final String TIME_CONFIGURED_PREFERENCE_ID = "Conf_Hora";
    protected static final String POSITION_CONFIGURED_PREFERENCE_ID = "Conf_Posicion";
    protected static final String PRODUCT_CONFIGURED_PREFERENCE_ID = "Conf_Producto";

    private SharedPreferences preferences;

    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
    }

    public void setUrl(String url) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(URL_PREFERENCE_ID, url);
        editor.commit();
    }

    public String getUrl() {
        return preferences.getString(URL_PREFERENCE_ID, "http://www.energysistem.com/tools/tiendas/etiquetas/index.html");
    }

    public void setUrlExist(Boolean exist) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(URL_EXIST_PREFERENCE_ID, exist);
        editor.commit();
    }

    public boolean urlExist() {
        return preferences.getBoolean(URL_EXIST_PREFERENCE_ID, false);
    }

    public void setStartTime(String time) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(START_TIME_PREFERENCE_ID, time);
        editor.commit();
    }

    public String getStartTime() {
        return preferences.getString(START_TIME_PREFERENCE_ID, "00:00");
    }

    public void setEndTime(String time) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(END_TIME_PREFERENCE_ID, time);
        editor.commit();
    }

    public String getEndTime() {
        return preferences.getString(END_TIME_PREFERENCE_ID, "00:00");
    }

    public void setOrientation(int orientation) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ORIENTATION_PREFERENCE_ID, orientation);
        editor.commit();
    }

    public int getOrientation() {
        return preferences.getInt(ORIENTATION_PREFERENCE_ID, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void setAppFirtTime(boolean firstTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_TIME_PREFERENCE_ID, firstTime);
        editor.commit();
    }

    public boolean isAppFirstTime() {
        return preferences.getBoolean(FIRST_TIME_PREFERENCE_ID, false);
    }

    public void setTimeConfigured(boolean configured) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(TIME_CONFIGURED_PREFERENCE_ID, configured);
        editor.commit();
    }

    public boolean isTimeConfigured() {
        return preferences.getBoolean(TIME_CONFIGURED_PREFERENCE_ID, false);
    }

    public void setPositionConfigured(boolean configured) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(POSITION_CONFIGURED_PREFERENCE_ID, configured);
        editor.commit();
    }

    public boolean isPositionConfigured() {
        return preferences.getBoolean(POSITION_CONFIGURED_PREFERENCE_ID, false);
    }

    public void setProductConfigured(boolean configured) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PRODUCT_CONFIGURED_PREFERENCE_ID, configured);
        editor.commit();
    }

    public boolean isProductConfigured() {
        return preferences.getBoolean(PRODUCT_CONFIGURED_PREFERENCE_ID, false);
    }
}
