package d4d.com.atm.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import d4d.com.atm.SplashActivity;
import d4d.com.atm.comunication.WebSocketComunication;

import static d4d.com.atm.comunication.WebSocketComunication.banderaCerrarSesion;

/**
 * Created by JuanPablo on 06/03/2016.
 */
public class SessionManager {

    public static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Data4DecisionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER = "user";
    public static final String KEY_IMEI = "imei";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_IDMNSJ = "idmnsj";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_APELLIDO = "apellido";
    public static final String KEY_CEDULA = "cedula";
    public static final String KEY_COD_AGENTE = "cod_agente";
    public static final String KEY_CANTON = "canton";
    public static final String KEY_ZONA = "zona";
    public static final String KEY_SERIALMASALTO = "serialMasAlto";
    public static final String KEY_VERSIONCOIP = "versionCOIP";
    public static final String KEY_ID_DSP = "id_dsp";
    public static final String KEY_FEDATARIO = "fedatario";
    public static final String KEY_VIDEO_TIEMPO = "videoTiempo";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void createLoginSession(String user, String imei, String uuid, String token,
                                   String idmnsj, String nombre, String apellido, String cedula,
                                   String cod_agente, String canton, String zona, String serialMasAlto,
                                   String versionCOIP, String id_dsp, String fedatario, String videoTiempo){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_IMEI, imei);
        editor.putString(KEY_UUID, uuid);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_IDMNSJ, idmnsj);
        editor.putString(KEY_NOMBRE, nombre);
        editor.putString(KEY_APELLIDO, apellido);
        editor.putString(KEY_CEDULA, cedula);
        editor.putString(KEY_COD_AGENTE, cod_agente);
        editor.putString(KEY_CANTON, canton);
        editor.putString(KEY_ZONA, zona);
        editor.putString(KEY_SERIALMASALTO, serialMasAlto);
        editor.putString(KEY_VERSIONCOIP, versionCOIP);
        editor.putString(KEY_ID_DSP, id_dsp);
        editor.putString(KEY_FEDATARIO, fedatario);
        editor.putString(KEY_VIDEO_TIEMPO, videoTiempo);
        editor.commit();

        Intent i = new Intent(_context, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public static void setVersionCOIP(String versionCOIP){
        editor.putString(KEY_VERSIONCOIP, versionCOIP);
        editor.commit();
    }

    public static String getUser(){
        return pref.getString(KEY_USER, null);
    }

    public static String getImei(){
        return pref.getString(KEY_IMEI, null);
    }

    public static String getUuid(){
        return pref.getString(KEY_UUID, null);
    }

    public static String getToken(){
        return pref.getString(KEY_TOKEN, null);
    }

    public static String getNombre(){
        return pref.getString(KEY_NOMBRE, null);
    }

    public static String getApellido(){
        return pref.getString(KEY_APELLIDO, null);
    }

    public static String getCodAgente(){
        return pref.getString(KEY_COD_AGENTE, null);
    }

    public static String getCanton(){
        return pref.getString(KEY_CANTON, null);
    }

    public static String getZona(){
        return pref.getString(KEY_ZONA, null);
    }

    public static String getSerialMasAlto(){
        return pref.getString(KEY_SERIALMASALTO, null);
    }

    public static String getVersionCOIP(){
        return pref.getString(KEY_VERSIONCOIP, null);
    }

    public static String getIdDsp(){
        return pref.getString(KEY_ID_DSP, null);
    }

    public static String getFedatario(){
        return pref.getString(KEY_FEDATARIO, null);
    }

    public static String getVideoTiempo(){
        return pref.getString(KEY_VIDEO_TIEMPO, null);
    }

    public static void logoutUser(Context context) {
        banderaCerrarSesion=true;
        editor.clear();
        editor.commit();

        if(WebSocketComunication.sNotificationManager!=null) {
            WebSocketComunication.sNotificationManager.cancelAll();
        }
        Intent i = new Intent(context.getApplicationContext(), SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void CloseSessionManager(){
        banderaCerrarSesion=true;
        editor.clear();
        editor.commit();
    }

    public static boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

}