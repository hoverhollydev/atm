package d4d.com.atm.comunication;

//import android.annotation.SuppressLint;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
//import android.location.Location;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
//import android.support.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
/*import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;*/
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import d4d.com.atm.LoginActivity;
import d4d.com.atm.MainActivity;
import d4d.com.atm.MainActivity2;
import d4d.com.atm.R;
import d4d.com.atm.SettingsActivity;
import d4d.com.atm.SplashActivity;
import d4d.com.atm.utils.SessionManager;
import d4d.com.atm.utils.Utils;
import d4d.com.atm.utils.Version;
import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.interfaces.IWebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
import io.crossbar.autobahn.websocket.types.WebSocketOptions;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class WebSocketComunication extends Service {

    public static final String BLUETOOTH_PRIVILEGED="android.permission.BLUETOOTH_PRIVILEGED";
    private static String wsuri = "ws://10.9.10.18:3119"; //Server data4decision 3112 - 3119
    private static final String TAG = "com.data4decision";
    private static String[] protocol = {"com-protocolo"};
    private static boolean isMC67 = false;
    public static int valida_login = 0;
    private static int cnd = 0;
    public static boolean banderaCerrarSesion = false;
    private static Resources res;

    public static WebSocketConnection webSocketConnection;
    //public static WebSocket.ConnectionHandler messageWebSocket;
    private static IWebSocketConnectionHandler wsHandler;
    private static WebSocketOptions webSocketOptions;

    public static Context contextWs;
    public static Service serviceWebsocket;
    private Thread workerThread = null;
    private static ProgressDialog pk_conexion_websocket;

    //Intensidad de la señal GSM 3g o 4g
    TelephonyManager mTelephonyManager;
    MyPhoneStateListener mPhoneStatelistener;
    int signalSupport = 0;

    //Usuario
    private static SessionManager session;
    private static Version version;
    public static String tipo = "NO_HAY";


    private static String speed;
    public static String latitud = "-2.2071271"; //Coordenadas por default si no hay GPS en playa Chocoltera Salinas
    public static String longitud = "-81.0134809";
    public static String aux_latitud = "-2.2071271";
    public static String aux_longitud = "-81.0134809";
    //Obtiene la ubicación del dispositivo mediante el GPS MapBox
    //private static LocationEngine locationEngine;
    //private static WebSocketComunicationLocationCallback callback = new WebSocketComunicationLocationCallback();
    private static long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    public static int timeSendTelemetria=5000;
    public static int cont_location=5;
    public static boolean validaConfirmacion = true;

    //Estado de Batería
    private static int levelaux=-1;
    //Intensidad de la señal GSM 3g o 4g
    public static String banda="";
    public static String senal="";
    public static String extra="";
    public static String manufactura ="";
    public static String modelo="";
    public static String version_app ="";
    public static String imei ="";
    public static String serial_chip="";
    public static String operador_celular="";
    public static String ping_time_response="";
    private static Handler handlerMsj;
    public static NotificationManager sNotificationManager = null;

    private static void runOnUiThread(Runnable runnable) {
        handlerMsj.post(runnable);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    BroadcastReceiver miBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.i("TAG", "ACTION : "+ intent.getAction());
            switch (intent.getAction()){
                case Intent.ACTION_BATTERY_CHANGED:
                    int level = intent.getIntExtra("level", -1);
                    levelaux = level;
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "SERVICIOINICIADO");
        contextWs=getApplicationContext();
        handlerMsj=new Handler();
        //Intensidad de la señal GSM 3g o 4g
        mPhoneStatelistener = new MyPhoneStateListener();
        getCoordenadas();
        super.onStartCommand(intent, flags, startId);
        if(workerThread == null || !workerThread.isAlive()){
            workerThread = new Thread(new Runnable() {
                public void run() {
                    res = getResources();
                    session = new SessionManager(contextWs);
                    version = new Version(contextWs);
                    //unregisterReceiver miBroadcast items botones, acciones
                    if(miBroadcast!=null) {
                        if(miBroadcast.isInitialStickyBroadcast()) {
                            unregisterReceiver(miBroadcast);
                        }
                    }
                    //estado de bateréa en porcentaje
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = () -> {
                        try {
                            registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    };
                    handler.sendEmptyMessage(0);
                    handler.postDelayed(myRunnable, 5000);

                    //Intensidad de la señal GSM 3g o 4g
                    mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    //getLocationMapBox();
                        if (MainActivity.getMyInstance() == null && session.isLoggedIn() == true) {
                            WebSocketComunication.valida_login = 2;
                            WebSocketComunication.banderaCerrarSesion = false;

                            Intent imain;
                            if(session.getFedatario().compareToIgnoreCase("false")==0) {
                                imain = new Intent(contextWs, MainActivity.class);
                            }else{
                                imain = new Intent(contextWs, MainActivity2.class);
                            }
                            imain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(imain);
                            startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                        } else {
                            if (LoginActivity.getMyInstance() == null) {
                                Intent iSpl = new Intent(contextWs, SplashActivity.class);
                                iSpl.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplication().startActivity(iSpl);
                            }
                        }

                    String NOTIFICATION_CHANNEL_ID = "my_channel_01";// The id of the channel.
                    Intent resultIntent;
                    resultIntent = new Intent(contextWs, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(contextWs, 5, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(contextWs, NOTIFICATION_CHANNEL_ID);
                    mBuilder.setSmallIcon(R.mipmap.ic_icon_conectado);
                    mBuilder.setColor(ContextCompat.getColor(contextWs, R.color.colorPrimary))
                            .setContentTitle("CExia se está ejecutando")
                            .setContentText("Servicio Iniciado.")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Servicio Iniciado."))
                            .setTicker("CExia")
                            .setOngoing(true)
                            .setAutoCancel(false)
                            .setWhen(0)
                            .setLights(Color.BLUE, 1, 1);
                    sNotificationManager = (NotificationManager) contextWs.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                        notificationChannel.enableLights(false);
                        notificationChannel.setLightColor(Color.BLUE);
                        assert sNotificationManager != null;
                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                        sNotificationManager.createNotificationChannel(notificationChannel);
                    }
                    sNotificationManager = (NotificationManager) contextWs.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notif = mBuilder.build();
                    notif.flags |= Notification.FLAG_NO_CLEAR;
                    sNotificationManager.notify(5, notif);
                    startForeground(5, notif);
                    serviceWebsocket=WebSocketComunication.this;
                    validarConexion();
                    pk_conexion_websocket=null;
                    try{
                        pk_conexion_websocket= ProgressDialog.show(contextWs, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
                    }catch (Exception e){
                    }
                    Handler handler2 = new Handler(Looper.getMainLooper());
                    Runnable myRunnable2 = () -> {
                        if(pk_conexion_websocket!=null){
                            try {
                                pk_conexion_websocket.dismiss();
                            }catch (Exception e){
                            }
                        }
                    };
                    handler2.sendEmptyMessage(0);
                    handler2.postDelayed(myRunnable2, 4000);
                    validaConexionWebsocket();
                }
            });
            workerThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sNotificationManager!=null) {
            sNotificationManager.cancelAll();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String id = "my_channel_01";
                sNotificationManager.deleteNotificationChannel(id);
            }
        }
        if(this.miBroadcast!=null || this.miBroadcast.isInitialStickyBroadcast()) {
            unregisterReceiver(this.miBroadcast);
        }
        //Prevent leaks
        /*if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }*/

        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }

    public static void startConnectionWebsocket(){
        webSocketConnection = new WebSocketConnection();
        wsHandler = new IWebSocketConnectionHandler() {
            @Override
            public void onConnect(ConnectionResponse response) {
                Log.i(TAG, "Protocolo:" + response.protocol);

            }
            @Override
            public void onOpen() {
                Log.i(TAG, "Status: Connected to " + wsuri);
                if (valida_login == 0 || valida_login == 2) {
                    if (webSocketConnection != null) {
                        if (webSocketConnection.isConnected() && session.isLoggedIn() == true) {
                            JSONObject jsonSendReady = new JSONObject();
                            if(session==null) {
                                session = new SessionManager(contextWs);
                            }
                            try {
                                jsonSendReady = new JSONObject().
                                        accumulate("tipo", "App Lista").
                                        accumulate("token", session.getToken()).
                                        accumulate("uuid", session.getUuid());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i("ON_OPEN -> RECONECTADO", jsonSendReady.toString());
                            Handler handler = new Handler(Looper.getMainLooper());
                            JSONObject finalJsonSendReady = jsonSendReady;
                            Runnable myRunnable = new Runnable() {
                                public void run() {
                                    try {
                                        webSocketConnection.sendMessage(finalJsonSendReady.toString());
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                }
                            };
                            handler.sendEmptyMessage(0);
                            handler.postDelayed(myRunnable, 500);
                        }
                    }
                }
                webSocketConnection.sendMessage("hola");
                cnd = 0;
                if(pk_conexion_websocket!=null){
                    try {
                        pk_conexion_websocket.dismiss();
                    }catch (Exception e){
                    }
                }
            }
            @Override
            public void onClose(int i, String s) {
                Log.i(TAG, "ON CLOSE" + wsuri + ",num:" + i + ",s:" + s);
                if (cnd == 0) {
                    if (valida_login == 2) {
                        if (banderaCerrarSesion==false) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (contextWs != null) {
                                        Toast.makeText(contextWs, "Se ha perdido la conexión con el servidor, por favor revise sus conexiones.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                }
                //Cuando Gregor recibe el mensaje de cerrar sesion cierra el websocket y me envia eel numero 1000 como codigo no como mensaje de websocket
                if (banderaCerrarSesion==false && valida_login == 2) {
                    if (i != 1) {
                        Log.i("Reconectar", "WEBSOKCET " + i + ", " + s);
                        Handler handler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = () -> {
                            try {
                                startConnectionWebsocket();
                                desconectadoWebS=0;
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        };
                        handler.sendEmptyMessage(0);
                        handler.postDelayed(myRunnable, 3000);
                    }
                }
                cnd = 1;
            }

            @Override
            public void onMessage(String payload) {
                Log.i(TAG, "ServerJS: " + payload);
                JSONObject msjSendOK = null;
                JSONObject msjReciveServer = null;
                try {
                    msjReciveServer = new JSONObject(payload);
                    if (valida_login == 2) {
                        //Envio de confirmación al recibir mensajes de consultas e ingresos de datos
                        if (msjReciveServer.has("idmnsj")) {
                            try {
                                msjSendOK = new JSONObject();
                                msjSendOK.accumulate("token", SessionManager.getToken()).
                                        accumulate("tipo", "confirmacion").
                                        accumulate("uuid", SessionManager.getUuid()).
                                        accumulate("idmnsj", msjReciveServer.getString("idmnsj"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (webSocketConnection.isConnected()) {
                                webSocketConnection.sendMessage(msjSendOK.toString());
                                //Log.i("CONFIRMACION ENVIADA", msjSendOK.toString());
                            }
                        }
                        tipo = (msjReciveServer.has("tipo")) ? msjReciveServer.getString("tipo") : "NO_HAY";

                        String cerrarSesion = (msjReciveServer.has("respuesta")) ? msjReciveServer.getString("respuesta") : "NO_HAY";
                        if (cerrarSesion.compareToIgnoreCase("OK") == 0) {
                            banderaCerrarSesion = true;
                        }

                        if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
                            if (MainActivity.context != null) {
                                MainActivity.mensajeRecibido(payload);
                            }
                        }else{
                            if (MainActivity2.context != null) {
                                MainActivity2.mensajeRecibido(payload);
                            }
                        }

                        if(tipo.compareToIgnoreCase("getUpdateAppRespuesta")==0) {
                            if (SettingsActivity.contextSettings != null) {
                                SettingsActivity.getMessageUpdate(msjReciveServer);
                            }
                        }
                    }
                    if (valida_login == 1) {
                        LoginActivity.recibirMensajeLogin(payload);
                    }
                } catch (JSONException e) {
                    Log.e(" Error WEBSOCK", e.getMessage().toString());
                }
            }
            @Override
            public void onMessage(byte[] payload, boolean isBinary) {
                //Log.i(TAG, "isBinary payload: " + payload.length);
                if (SettingsActivity.contextSettings != null) {
                    if (SettingsActivity.progressDialog != null) {
                        SettingsActivity.progressDialog.dismiss();
                    }
                    if (SettingsActivity.handlerUpdate != null) {
                        SettingsActivity.handlerUpdate.removeCallbacks(SettingsActivity.runnableUpdate);
                    }
                    SettingsActivity.settingsActivity.finish();
                }

                if(session.getFedatario().compareToIgnoreCase("false")==0) {
                    if (MainActivity.getMyInstance() == null) {
                        Intent intent = new Intent(contextWs, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        contextWs.startActivity(intent);
                    }
                    MainActivity.recibirActualizacion(payload);
                } else{
                    if (MainActivity2.getMyInstance() == null) {
                        Intent intent = new Intent(contextWs, MainActivity2.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        contextWs.startActivity(intent);
                    }
                    MainActivity2.recibirActualizacion(payload);
                }
            }
            @Override
            public void onPing() {

            }
            @Override
            public void onPing(byte[] payload) {
                String str;
                try {
                    str = new String(payload, "UTF-8");
                    //("onPing",str+ "PING");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onPong() {
            }
            @Override
            public void onPong(byte[] payload) {
            }
            @Override
            public void setConnection(WebSocketConnection connection) {
            }
        };
        try {
            WebSocketOptions webSocketOptions = new WebSocketOptions();
            //webSocketOptions.setAutoPingTimeout(36);

            webSocketOptions.setAutoPingInterval(15);
            if (!isMC67) {

                webSocketOptions.setMaxMessagePayloadSize(10485760);
                webSocketOptions.setMaxFramePayloadSize(10485760);
            } else {
                webSocketOptions.setMaxMessagePayloadSize(3145728);
                webSocketOptions.setMaxFramePayloadSize(3145728);
            }
            //Log.i("webSocketOptions",webSocketOptions.getMaxFramePayloadSize()+" -----"+webSocketOptions.getMaxMessagePayloadSize());
            webSocketConnection.connect(wsuri, protocol, wsHandler, webSocketOptions, null);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    //Tipo de Conexión Datos o Wifi
    public static String type = "mobile";
    public static int desconectadoWebS = 0;
    public static int count_bateria = 6;
    public static void validaConexionWebsocket(){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable task1 = () -> {
            if(session.isLoggedIn()) {
                if (isOnlineInternet()) {
                    if (webSocketConnection != null) {
                        if (webSocketConnection.isConnected()) {
                            desconectadoWebS=0;
                            if (session.isLoggedIn()) {
                                JSONObject gpsJSON = new JSONObject();
                                JSONObject jsonGPSSend = new JSONObject();
                                try {
                                    manufactura = Utils.getManufacturer();
                                    modelo=Utils.obtenerModelo();
                                    version_app = Utils.versionActualAPK(contextWs.getResources());
                                    imei = Utils.getImei(contextWs);
                                    serial_chip=Utils.getSerialChip(contextWs);
                                    operador_celular=Utils.getOperadorCelular(contextWs);
                                    gpsJSON.put("estado", "CONECTADO");
                                    gpsJSON.put("incidente", "");
                                    gpsJSON.put("manufactura", manufactura);
                                    gpsJSON.put("modelo", modelo);
                                    gpsJSON.put("imei", imei);
                                    gpsJSON.put("lati", latitud);
                                    gpsJSON.put("longi", longitud);
                                    gpsJSON.put("speed", speed);
                                    gpsJSON.put("bateria_movil", levelaux);
                                    gpsJSON.put("tipo_conexion", type);
                                    gpsJSON.put("extra", extra);
                                    gpsJSON.put("operadora", operador_celular);
                                    gpsJSON.put("serial_chip", serial_chip);
                                    gpsJSON.put("banda_fono", banda);
                                    gpsJSON.put("senal_fono", senal);
                                    gpsJSON.put("situacion", "En Operativo");
                                    gpsJSON.put("tipo", "handheld");
                                    gpsJSON.put("usuario", session.getUser());
                                    gpsJSON.put("uuid", session.getUuid());
                                    gpsJSON.put("id_dsp", session.getIdDsp());
                                    gpsJSON.put("zona", session.getZona());
                                    gpsJSON.put("version_app", version_app);
                                    jsonGPSSend.accumulate("tipo", "estado").accumulate("token", session.getToken())
                                            .accumulate("uuid", session.getUuid())
                                            .accumulate("parametros", gpsJSON);
                                    if (cont_location >= 5) {
                                        webSocketConnection.sendMessage(jsonGPSSend.toString());
                                        //Log.i("estado",jsonGPSSend.toString());
                                        //Log.i("UbicaciónMAPB", "(" + latitud + ", " + longitud + "), velocidad=" + speed + " meters/second");
                                        cont_location = 0;
                                    }

                                    if(count_bateria>=6){
                                        tipoConexionMovil();

                                        JSONObject batteryJSON = new JSONObject();
                                        final JSONObject jsonBatterySend = new JSONObject();
                                        try {
                                            batteryJSON.put("level", levelaux);
                                            jsonBatterySend.accumulate("tipo", "BatteryStatus")
                                                    .accumulate("token", session.getToken())
                                                    .accumulate("uuid", session.getUuid())
                                                    .accumulate("parametros", batteryJSON);
                                            webSocketConnection.sendMessage(jsonBatterySend.toString());
                                            //Log.i("Batería:",jsonBatterySend.toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                            levelaux = -1;
                                        } finally {
                                        }
                                        count_bateria=0;
                                    }
                                    count_bateria++;
                                    cont_location++;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.i("Websoket", "DESCONECTADO = "+desconectadoWebS);
                            desconectadoWebS++;
                            if (desconectadoWebS >= 5) {
                                startConnectionWebsocket();
                                desconectadoWebS=0;
                            }
                        }
                    } else {
                        //Log.i("Websoket", "NULL");
                        startConnectionWebsocket();
                    }
                } else {
                    Toast.makeText(contextWs, "No está conectado a Internet. Por favor, revise la conexión.", Toast.LENGTH_SHORT).show();
                    //Log.i("Websoket", "SIN INTERNET");
                    cnd = 1;
                }
            }
            validaConexionWebsocket();
        };
        handler.sendEmptyMessage(0);
        handler.postDelayed(task1, timeSendTelemetria);
    }

    public void validarConexion(){
        if (isOnlineInternet()) {
            if (webSocketConnection != null) {
                if (webSocketConnection.isConnected()) {
                } else {
                    Log.i("Websoket", "DESCONECTADO");
                    startConnectionWebsocket();
                }
            } else {
                //Log.i("Websoket", "NULL");
                startConnectionWebsocket();
            }
        } else {
            cnd = 1;
        }
    }

    public static boolean isOnlineInternet() {
        NetworkInfo netInfo=null;
        if(contextWs!=null) {
            ConnectivityManager cm = (ConnectivityManager) contextWs.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        }
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static void sendMessageUpdateApp(){
        if (isOnlineInternet()) {
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "getUpdateApp");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("version", Utils.versionActualAPK(contextWs.getResources()));
                json.put("timestamp", Utils.getCurrentTimeStamp());
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    //Log.i("getUpdateApp",json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }

    //Tipo de Conexión Datos o Wifi
    public static void tipoConexionMovil() {
        JSONObject connectionJSON = new JSONObject();
        JSONObject jsonConnectionSend = new JSONObject();
        try {
            connectionJSON.put("typeConnection", type);
            jsonConnectionSend.accumulate("tipo", "ConnectionStatus")
                    .accumulate("token", session.getToken())
                    .accumulate("uuid", session.getUuid())
                    .accumulate("parametros", connectionJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       if(webSocketConnection!=null) {
           if (webSocketConnection.isConnected() && session.isLoggedIn()) {
               webSocketConnection.sendMessage(jsonConnectionSend.toString());
           }
       }

    }

    //Ubicación Mapbox poder
    /*@SuppressLint("MissingPermission")
    private static void getLocationMapBox() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(contextWs);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, contextWs.getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private static class WebSocketComunicationLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        @Override
        public void onSuccess(LocationEngineResult result) {
            Location location = result.getLastLocation();
            if (location == null) {
                return;
            }
            // Pass the new location to the Maps SDK's LocationComponent
            if (result.getLastLocation() != null) {
                speed=result.getLastLocation().getSpeed()+"";
                aux_latitud = result.getLastLocation().getLatitude()+"";
                aux_longitud = result.getLastLocation().getLongitude()+"";
                if(latitud.compareToIgnoreCase(aux_latitud)!=0 || longitud.compareToIgnoreCase(aux_longitud)!=0){
                    latitud=aux_latitud;
                    longitud=aux_longitud;
                    cont_location=6;
                }
                if(speed.compareToIgnoreCase("0.0")!=0){
                    cont_location=6;
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
        }
    }*/



    //Obtiene la ubicación del dispositivo mediante el GPS
    private static LocationManager locationManager;
    private static LocationListener listener;

    private static long TInicio, TFin, tiempo; //Variables para determinar el tiempo de ejecución

    public static void getCoordenadas() {
        if (ActivityCompat.checkSelfPermission(contextWs, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contextWs, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        //Log.i("getCoordenadas","getCoordenadas");
        locationManager = (LocationManager) contextWs.getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location== null){
                    return;
                }

                if (location != null) {
                    speed=location.getSpeed()+"";
                    aux_latitud = location.getLatitude()+"";
                    aux_longitud = location.getLongitude()+"";
                    if(latitud.compareToIgnoreCase(aux_latitud)!=0 || longitud.compareToIgnoreCase(aux_longitud)!=0){
                        latitud=aux_latitud;
                        longitud=aux_longitud;
                        cont_location=6;
                    }
                    if(speed.compareToIgnoreCase("0.0")!=0){
                        cont_location=6;
                    }
                }
                //Log.i("Coordenadas", "Lat:" + latitud + " , Lon:" + longitud);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(contextWs, res.getString(R.string.str_activar_gps), Toast.LENGTH_SHORT).show();
            }
        };
//        locationManager.requestLocationUpdates("gps", 10000, 0, listener);
        //.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, listener);
        //Toast.makeText(this, "Network provider started running", Toast.LENGTH_LONG).show();
    }

    //Detectar conexión 3g o 4g e intensidad de la señal
    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            String ssignal = signalStrength.toString();
            String[] parts = ssignal.split(" ");
            ConnectivityManager manager =(ConnectivityManager) contextWs.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (null != activeNetwork) {
                type=activeNetwork.getTypeName();
                extra=activeNetwork.getExtraInfo();
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) contextWs.getSystemService(Context.WIFI_SERVICE);
                    //int numberOfLevels = 5;
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                    banda="2.4G/5G";
                    senal=wifiInfo.getRssi()+"";
                }
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager tm = (TelephonyManager)contextWs.getSystemService(Context.TELEPHONY_SERVICE);
                    if ( tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
                        //senal Excelente
                        banda="LTE";
                        // For Lte SignalStrength: dbm = ASU - 140.
                        //Log.i("signalSupport",parts[9]+" ___");
                        //signalSupport = Integer.parseInt(parts[9]);
                        signalSupport=80;
                        senal=signalSupport+"";
                    }
                    else{
                        switch (tm.getNetworkType()) {
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                //senal Mala
                                banda= "2G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                //senal Buena
                                banda= "3G";
                                break;
                            default:
                                banda= "Unknown";
                                break;
                        }
                        if (signalStrength.isGsm()) {
                            // For GSM Signal Strength: dbm =  (2*ASU)-113.
                            if (signalStrength.getGsmSignalStrength() != 99) {
                                signalSupport = -113 + 2 * signalStrength.getGsmSignalStrength();
                                senal=signalSupport+"";
                            }
                        }
                    }

                }
            }
        }
    }
}
