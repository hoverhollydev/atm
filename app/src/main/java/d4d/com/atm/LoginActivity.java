package d4d.com.atm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import d4d.com.atm.comunication.WebSocketComunication;
import d4d.com.atm.utils.SessionManager;
import d4d.com.atm.utils.Utils;
import d4d.com.atm.utils.Version;

import static d4d.com.atm.comunication.WebSocketComunication.webSocketConnection;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class LoginActivity extends AppCompatActivity {

    //Usuario
    private static SessionManager session;
    private static Version version;
    private Resources res;
    private EditText txt_password;
    private EditText txt_user;
    private Button btnIniciar;
    private static ProgressDialog pk_Login;
    private JSONObject userJSON;
    private JSONObject jsonConsultaLogin;
    private File folder;
    private static AppCompatActivity activity = null;
    public static Context contextLogin = null;
    private static String imei="";
    private static String password="";
    private static String user="";
    private boolean banderamsj=false;
    private static String versionCOIP="";
    private static LoginActivity myInstanceL;
    private static ProgressDialog pk_Loginl;

    public static LoginActivity getMyInstance() {
        return myInstanceL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myInstanceL = LoginActivity.this;
        activity = this;
        contextLogin = this;
        res = getResources();

        Intent myService = new Intent(this, WebSocketComunication.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(myService);
        } else {
            startService(myService);
        }
        txt_user = (EditText)findViewById(R.id.txt_usuario);
        txt_password = (EditText)findViewById(R.id.txt_contrasena);
        btnIniciar = (Button)findViewById(R.id.btn_iniciar_sesion);
        inicializarVariable();
        txt_user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_user.getText().length() == 0) {
                    txt_user.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_password.getText().length() == 0) {
                    txt_password.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });

        txt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(WebSocketComunication.isOnlineInternet()){
                        WebSocketComunication.startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                        WebSocketComunication.banderaCerrarSesion = false;
                        pk_Loginl=null;
                        try{
                            pk_Loginl= ProgressDialog.show(contextLogin, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
                        }catch (Exception e){

                        }
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        //start();
                                        if(pk_Loginl!=null){
                                            try {
                                                pk_Loginl.dismiss();
                                            }catch (Exception e){

                                            }
                                        }
                                        if (webSocketConnection.isConnected()) {
                                            ejecutar_evento();
                                        }else{
                                            if(contextLogin!=null) {
                                                Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_servidor), contextLogin);
                                            }
                                        }
                                    }}, 5000
                        );
                    } else {
                        String msgToasts=res.getString(R.string.sin_conexion);
                        Toast.makeText(contextLogin, msgToasts, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(WebSocketComunication.isOnlineInternet()){
                    WebSocketComunication.startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                    WebSocketComunication.banderaCerrarSesion = false;
                    pk_Loginl=null;
                    try{
                        pk_Loginl= ProgressDialog.show(contextLogin, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
                    }catch (Exception e){

                    }
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    //start();
                                    if(pk_Loginl!=null){
                                        try {
                                            pk_Loginl.dismiss();
                                        }catch (Exception e){

                                        }
                                    }
                                    if (webSocketConnection.isConnected()) {
                                        ejecutar_evento();
                                    }else{
                                        if(contextLogin!=null) {
                                            Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_servidor), contextLogin);
                                        }
                                    }
                                }}, 6000
                    );
                } else {
                    String msgToasts=res.getString(R.string.sin_conexion);
                    Toast.makeText(contextLogin, msgToasts, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inicializarVariable(){
        imei = Utils.getImei(this);
       // imei= "356776480019020"; //imei ION MOTOROLA
        //imei= "860046031410717"; //imei one plus 3t
        Log.i("Imei",imei+" ____IMEI");

        folder =  new File("/sdcard/ATM");
        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    static Handler handler; // declared before onCreate
    static Runnable myRunnable;
    private static boolean msjLogin=true;
        private void ejecutar_evento(){
            user = txt_user.getText().toString();
            password = txt_password.getText().toString();
            if (user.equals("") || password.equals(""))
                Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_usuario_incorrecto), contextLogin);
            else {
                pk_Login=null;
                pk_Login= ProgressDialog.show(contextLogin, res.getString(R.string.str_sistema),res.getString(R.string.str_iniciando_sesion), false, false);
                iniciar();
                handler=  new Handler();
                myRunnable = new Runnable() {
                    public void run() {
                        if(pk_Login!=null){
                            pk_Login.dismiss();
                        }
                        if(msjLogin==true){
                            if(WebSocketComunication.valida_login==1) {
                                WebSocketComunication.valida_login = 0;
                                Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_datos), contextLogin);
                            }
                        }else {
                            msjLogin = true;
                        }
                    }
                };
                handler.postDelayed(myRunnable,6000);
            }
        }

    public void iniciar() {
        try{
            String path = "/sdcard/ATM/MultasElectronica.xls";
            try {
                versionCOIP=Utils.getVersionCoip(path);
            }catch (Exception e){
                //versionCOIP="27.08.2017";
            }finally {
                if(versionCOIP.compareToIgnoreCase("")==0) {
                    versionCOIP = "27.08.2017";
                }
            }
        } catch (Exception e){
            banderamsj=true;
            Utils.alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_sin_excel_actual), contextLogin);
        } finally {
            if(versionCOIP.compareToIgnoreCase("")==0) {
                versionCOIP = "27.08.2017";
            }
            enviarMensajeLogin();
        }

        if(banderamsj==true){
            if(pk_Login!=null){
                WebSocketComunication.valida_login=0;
                pk_Login.dismiss();
                handler.removeCallbacks(myRunnable);
            }
        }
    }

    public void enviarMensajeLogin() {
        userJSON = new JSONObject();
        jsonConsultaLogin = new JSONObject();
        try {
            userJSON.put("uuid_dsp", imei);
            userJSON.put("user",user);
            userJSON.put("pass", password);
            userJSON.put("versionCOIP", versionCOIP);
            userJSON.put("versionAPK", Utils.versionActualAPK(res));
            jsonConsultaLogin = new JSONObject().accumulate("tipo","login").accumulate("parametros", userJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (webSocketConnection.isConnected()) {
            //webSocketConnection.sendMessage("hola");
            webSocketConnection.sendMessage(jsonConsultaLogin.toString());
            Log.i("LOGIN ",jsonConsultaLogin.toString());
        }
        WebSocketComunication.valida_login = 1;
    }

    public static void recibirMensajeLogin(String message) {
        Log.i("RESP LOGIN ",message);
        JSONObject messageRecive = null;
        try {
            messageRecive = new JSONObject(message);
            String status =(messageRecive.has("error"))?messageRecive.getJSONObject("error").getString("status"):"100";
            String respuesta =(messageRecive.has("respuesta"))?messageRecive.getString("respuesta"):"NO_HAY";

            if(status.compareToIgnoreCase("0")==0) {
                if(respuesta.compareToIgnoreCase("NO_HAY")==0 ) {
                    msjLogin=false;
                    String uuid = messageRecive.getString("uuid");
                    String token = messageRecive.getString("token");
                    String idmnsj = messageRecive.getString("idmnsj");
                    String nombre = messageRecive.getString("nombre");
                    String apellidos = messageRecive.getString("apellidos");
                    String cedula = messageRecive.getString("cedula");
                    String cod_agent = messageRecive.getString("codigo");
                    String canton = messageRecive.getString("canton");
                    String zona = messageRecive.getString("zona");

                    String id_dsp = messageRecive.getString("id_dsp").toString();
                    int td = id_dsp.length();
                    String aux_id_dsp = id_dsp.substring((td-4), td);

                    String serialMasAlto = (messageRecive.has("serialMasAlto")) ? messageRecive.get("serialMasAlto").toString() : aux_id_dsp + "000000";
                    try {
                        int t = serialMasAlto.length();
                        Log.i("serialMasAlto", serialMasAlto);
                        serialMasAlto = serialMasAlto.substring((t - 6), t);
                        Log.i("SERIAL", serialMasAlto);
                    }catch (Exception e){
                        serialMasAlto="000000";
                        //Log.i("SERIAL E", serialMasAlto);
                    }

                    String fedatario=(messageRecive.has("fedatario")) ? messageRecive.get("fedatario").toString() : "false";
                    String videoTiempo = (messageRecive.has("videoTiempo")) ? messageRecive.get("videoTiempo").toString() : "5000";

                    //String fedatario=messageRecive.getString("fedatario");
                    Log.i("fedatario", fedatario);

                    //Crear sesión Usuario
                    session = new SessionManager(contextLogin);
                    session.createLoginSession(user, imei, uuid, token, idmnsj, nombre, apellidos, cedula, cod_agent, canton, zona, serialMasAlto, versionCOIP, id_dsp, fedatario, videoTiempo);

                    //Crear veriones
                    version =new Version(contextLogin);
                    version.datosVersion(Utils.versionActualAPK(contextLogin.getResources()), Utils.versionActualAPK(contextLogin.getResources()));
                    WebSocketComunication.valida_login=2;

                    Intent intent;

                    if(fedatario.compareToIgnoreCase("false")==0){
                        intent = new Intent(contextLogin, MainActivity.class);
                    }else{
                        intent = new Intent(contextLogin, MainActivity2.class);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    contextLogin.startActivity(intent);
                    if(pk_Login!=null){
                        pk_Login.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                    activity.finish();
                }
            }else {
                if (status.compareToIgnoreCase("46") == 0) {
                    if(pk_Login!=null){
                        pk_Login.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                    Utils.alertDialog(contextLogin.getResources().getString(R.string.str_alerta), contextLogin.getResources().getString(R.string.str_no_usuario), contextLogin);
                }else{
                    if(status.compareToIgnoreCase("44")==0) {
                        if(pk_Login!=null){
                            pk_Login.dismiss();
                            handler.removeCallbacks(myRunnable);
                        }
                        Utils.alertDialog(contextLogin.getResources().getString(R.string.str_sistema),contextLogin.getResources().getString(R.string.str_status_44), contextLogin);
                    }else{
                        if(pk_Login!=null){
                            pk_Login.dismiss();
                            handler.removeCallbacks(myRunnable);
                        }
                        //Log.i("ERROR",message);
                        if(contextLogin!=null)
                        Utils.alertDialog(contextLogin.getResources().getString(R.string.str_sistema), contextLogin.getResources().getString(R.string.str_status_all), contextLogin);
                    }
                }
                WebSocketComunication.valida_login = 0;
            }
        } catch (JSONException e) {
            if(pk_Login!=null){
                pk_Login.dismiss();
                WebSocketComunication.valida_login = 0;
                handler.removeCallbacks(myRunnable);
            }
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i("onDestroy", "onDestroy");
        super.onDestroy();
        if(session !=null) {
            if (session.isLoggedIn() == false) {
                if (WebSocketComunication.serviceWebsocket != null) {
                    WebSocketComunication.serviceWebsocket.stopForeground(true);
                    WebSocketComunication.serviceWebsocket.stopSelf();
                }
                System.exit(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
