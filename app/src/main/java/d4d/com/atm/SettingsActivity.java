package d4d.com.atm;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.tscdll.TSCActivity;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import d4d.com.atm.comunication.WebSocketComunication;
import d4d.com.atm.utils.SessionManager;
import d4d.com.atm.utils.Sleeper;
import d4d.com.atm.utils.Utils;
import driver.Contants;


import static d4d.com.atm.comunication.WebSocketComunication.latitud;
import static d4d.com.atm.comunication.WebSocketComunication.longitud;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private static Resources res;
    public static Context contextSettings = null;
    public static AppCompatActivity settingsActivity = null;

    private Button btn_test_impresion;
    private Button btn_update_app;
    private TextView txt_imei;
    private static TextView txt_latlon;
    private TextView txt_versionCOIP;
    private ProgressDialog pk_loading;

    //Impresora Zebra
    private ZebraPrinter printer;
    private Connection connection;

    //Impresora TSC
    TSCActivity TscDll = new TSCActivity();

    //Impresora Hubrox
    private boolean imprime_hubros=false;

    //Bluetooth
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    String printerMac = "";
    private String nombreImpresora="";
    private boolean validaImpresora=false;
    public static Handler handlerUpdate;
    public static Runnable runnableUpdate;
    public static ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsActivity=SettingsActivity.this;
        contextSettings=SettingsActivity.this;

        res = getResources();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(res.getString(R.string.str_acerca_de));

        txt_versionCOIP=(TextView)findViewById(R.id.txt_version_coip);
        txt_imei=(TextView)findViewById(R.id.txt_imei);
        txt_latlon=(TextView)findViewById(R.id.txt_latlon);
        btn_test_impresion=(Button) findViewById(R.id.btn_test_impresion);
        btn_update_app = (Button) findViewById(R.id.btn_update);

        btn_test_impresion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impresion_prueba();
            }
        });

        btn_update_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=null;
                try{
                    progressDialog= ProgressDialog.show(contextSettings, res.getString(R.string.str_sistema),res.getString(R.string.str_update_app_search), false, false);
                }catch (Exception e){

                }

                //Metodo para iniciar conexion Websocket
                WebSocketComunication.sendMessageUpdateApp();

                handlerUpdate = new Handler(Looper.getMainLooper());
                runnableUpdate = new Runnable() {
                    public void run() {
                        try {
                            if(progressDialog!=null){
                                try {
                                    progressDialog.dismiss();
                                }catch (Exception e){

                                }
                            }
                            Utils.alertDialog(res.getString(R.string.str_sistema), res.getString(R.string.str_app_update_ready), contextSettings );
                        } catch (Exception e) {
                            Log.i("Error", e.toString());
                        }
                    }
                };
                handlerUpdate.sendEmptyMessage(0);
                handlerUpdate.postDelayed(runnableUpdate, 15000);
            }
        });

        txt_versionCOIP.setText("Versión de excel COIP "+SessionManager.getVersionCOIP());
        txt_imei.setText("Imei: "+ Utils.getImei(this));
        cargarCoordenadas();

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver1, filter1);
        this.registerReceiver(mReceiver1, filter2);
    }


    public static void getMessageUpdate(JSONObject value){
        try {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            if(handlerUpdate!=null) {
                handlerUpdate.removeCallbacks(runnableUpdate);
            }

            Utils.alertDialog(res.getString(R.string.str_sistema), res.getString(R.string.str_app_update_ready), contextSettings );

            //String msj=value.getString("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //public



    public static void cargarCoordenadas(){
        if(txt_latlon!=null) {
            txt_latlon.setText("(" + latitud + ", " + longitud + ")");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
                    if(MainActivity.hsBluetoothPrintDriver!=null) {
                        MainActivity.hsBluetoothPrintDriver.stop();
                    }
                }else{
                    if(MainActivity2.hsBluetoothPrintDriver!=null) {
                        MainActivity2.hsBluetoothPrintDriver.stop();
                    }
                }
                unregisterReceiver(mReceiver1);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void impresion_prueba(){
        findBT();
        if (validaImpresora == true) {

            new Thread(new Runnable() {
                public void run() {
                    enableTestButton(false);
                    Looper.prepare();
                    Sleeper.sleep(1000);
                    if(nombreImpresora.compareToIgnoreCase("RW220") == 0) {
                        printer = connectZebra(printerMac);
                        int statusPrinter=5;
                        if (printer != null) {
                            statusPrinter=zebraPrinterVal();
                            if (statusPrinter == 0) {

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                                builder.setTitle(res.getString(R.string.str_alerta));
                                builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        enableTestButton(true);
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                                pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                            builder.setTitle(res.getString(R.string.str_alerta));
                            builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    enableTestButton(true);
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                            pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                        }
                    }else if(nombreImpresora.compareToIgnoreCase("HP-RP") == 0 || nombreImpresora.compareToIgnoreCase("RPP20") == 0) {
                        imageHubrox();
                        if(imprime_hubros==true){
                            textHubrox();
                        }

                    }else if(nombreImpresora.compareToIgnoreCase("ALPHA") == 0) {
                        textTSC();
                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle(res.getString(R.string.str_alerta));
                        builder.setMessage(res.getString(R.string.str_valida_impresora));
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                enableTestButton(true);
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                    }

                    Looper.loop();
                    Looper.myLooper().quit();
                }
            }).start();
        }
    }

    //Bluetooth
    private void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            validaImpresora=false;
            String nombre_impresora="";

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }else{
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice device : pairedDevices){
                        int tcd = device.getName().toString().length();
                        if(tcd >= 5){
                            nombre_impresora = device.getName().toString().substring(0, 5);
                        }else{
                            nombre_impresora = device.getName().toString().substring(0, tcd);
                        }

                        if(nombre_impresora.compareToIgnoreCase("RW220") == 0) {
                            if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
                                MainActivity.hsBluetoothPrintDriver.stop();
                            }else{
                                MainActivity2.hsBluetoothPrintDriver.stop();
                            }
                            validaImpresora = true;
                            mmDevice = device;
                            break;
                        }
                        if(nombre_impresora.compareToIgnoreCase("HP-RP") == 0 || nombre_impresora.compareToIgnoreCase("RPP20") == 0) {
                            validaImpresora = true;
                            mmDevice = device;
                            break;
                        }
                        if(nombre_impresora.compareToIgnoreCase("ALPHA") == 0) {
                            if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
                                MainActivity.hsBluetoothPrintDriver.stop();
                            }else{
                                MainActivity2.hsBluetoothPrintDriver.stop();
                            }
                            validaImpresora = true;
                            mmDevice = device;
                            break;
                        }
                    }
                }
                if (validaImpresora == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(res.getString(R.string.str_alerta));
                    builder.setMessage(res.getString(R.string.str_valida_impresora));
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                }else{
                    nombreImpresora=nombre_impresora;
                    validaImpresora=true;
                    printerMac = mmDevice.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            validaImpresora=false;
        }
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                //Log.i("DEVICE","is now connected");
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                //Device has disconnected
                //Log.i("DEVICE","disconnected");
                if(nombreImpresora.compareToIgnoreCase("RW220") == 0) {

                }else if(nombreImpresora.compareToIgnoreCase("HP-RP") == 0 || nombreImpresora.compareToIgnoreCase("RPP20") == 0) {
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(res.getString(R.string.str_alerta));
                    builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });*/

                    if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
                        MainActivity.hsBluetoothPrintDriver.stop();
                    }else{
                        MainActivity2.hsBluetoothPrintDriver.stop();
                    }
                    validaStartHubrox=0;
                    /*AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));*/

                }else if(nombreImpresora.compareToIgnoreCase("ALPHA") == 0) {

                }

            }
        }
    };

    public int zebraPrinterVal() {
        int status = -1;
        try {
            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);
            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();
            if (printerStatus.isReadyToPrint) {
                status = 0;
                byte[] configLabel = getConfigLabelZebra();
                connection.write(configLabel);
                enableTestButton(true);
            } else if (printerStatus.isPaused) {
                status = 1;
                //System.out.println("Cannot Print because the printer is paused.");
            } else if (printerStatus.isHeadOpen) {
                status = 2;
                //System.out.println("Cannot Print because the printer media door is open.");
            } else if (printerStatus.isPaperOut) {
                status = 3;
                //System.out.println("Cannot Print because the paper is out.");
            } else{
                status = 4;
                //System.out.println("Cannot Print.");
            }
        } catch (ConnectionException e) {
            status = 5;
        } finally {
            disconnect();
        }

        return status;
    }

    public ZebraPrinter connectZebra(String mac) {

        connection = null;
        if (connection == null){
            connection = new BluetoothConnection(mac);
        }

        try {
            connection.open();
        } catch (ConnectionException e) {
            disconnect();
        }

        ZebraPrinter printer = null;
        if (connection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(connection);
                String pl = SGD.GET("device.languages", connection);

            } catch (ConnectionException e) {
                printer = null;
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                printer = null;
                disconnect();
            }
        }
        return printer;
    }


    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (ConnectionException e) {
        }
    }

    private byte[] getConfigLabelZebra() {
        byte[] cpcl = null;
        String cpclConfigLabel;
        cpclConfigLabel = "! 0 200 200 "+(310)+" 1\r\n"+
                "PW 400\r\n" +
                "TONE 0\r\n" +
                "SPEED 3\r\n" +
                "ON-FEED IGNORE\r\n" +
                "NO-PACE\r\n" +
                "BAR-SENSE\r\n" +
                "" + "0000000001" + "\r\n" +
                "ENDPDF\r\n" +
                "PCX 10 10 !<ATM.PCX\r\n" +
                "T 5 2 50 "+(220)+" Test Impresión!!"+"\r\n"+
                "PRINT\r\n";
        try {
            cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cpcl;
    }

    //Impresión Hubrox
    private int validaStartHubrox=0;
    private boolean conn = true;
    private void imageHubrox() {

        if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
            if (mmDevice != null) {
                try {
                    if(validaStartHubrox==0) {
                        MainActivity.hsBluetoothPrintDriver.start();
                        MainActivity.hsBluetoothPrintDriver.connect(mmDevice);
                        Sleeper.sleep(2000);
                        MainActivity.hsBluetoothPrintDriver.Begin();
                        Sleeper.sleep(3000);
                        conn = MainActivity.hsBluetoothPrintDriver.IsNoConnection();
                    }
                    if(conn==false) {
                        validaStartHubrox=1;
                        MainActivity.hsBluetoothPrintDriver.SetDefaultSetting();
                        MainActivity.hsBluetoothPrintDriver.SetAlignMode((byte) 0x00);
                        MainActivity.hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x00);
                        MainActivity.hsBluetoothPrintDriver.SetUnderline((byte) 0x00);
                        MainActivity.hsBluetoothPrintDriver.SelChineseCodepage();
                        MainActivity.hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x00);
                        // if (hsBluetoothPrintDriver.printImage(mBitmap, Contants.TYPE_58)) {
                        //}
                        MainActivity.hsBluetoothPrintDriver.printImage(MainActivity.mBitmap, Contants.TYPE_58);
                        Sleeper.sleep(2000);
                        imprime_hubros=true;
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle(res.getString(R.string.str_alerta));
                        builder.setMessage(res.getString(R.string.str_encender_impresora));
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                enableTestButton(true);
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                    }
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(res.getString(R.string.str_alerta));
                    builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enableTestButton(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                    e.printStackTrace();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(res.getString(R.string.str_alerta));
                builder.setMessage(res.getString(R.string.str_impresora_sin_conexion));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
            }
        }else{
            if (mmDevice != null) {
                try {
                    if(validaStartHubrox==0) {
                        MainActivity2.hsBluetoothPrintDriver.start();
                        MainActivity2.hsBluetoothPrintDriver.connect(mmDevice);
                        Sleeper.sleep(2000);
                        MainActivity2.hsBluetoothPrintDriver.Begin();
                        Sleeper.sleep(3000);
                        conn = MainActivity2.hsBluetoothPrintDriver.IsNoConnection();
                    }
                    if(conn==false) {
                        validaStartHubrox=1;
                        MainActivity2.hsBluetoothPrintDriver.SetDefaultSetting();
                        MainActivity2.hsBluetoothPrintDriver.SetAlignMode((byte) 0x00);
                        MainActivity2.hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x00);
                        MainActivity2.hsBluetoothPrintDriver.SetUnderline((byte) 0x00);
                        MainActivity2.hsBluetoothPrintDriver.SelChineseCodepage();
                        MainActivity2.hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x00);
                        // if (hsBluetoothPrintDriver.printImage(mBitmap, Contants.TYPE_58)) {
                        //}
                        MainActivity2.hsBluetoothPrintDriver.printImage(MainActivity2.mBitmap, Contants.TYPE_58);
                        Sleeper.sleep(2000);
                        imprime_hubros=true;
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle(res.getString(R.string.str_alerta));
                        builder.setMessage(res.getString(R.string.str_encender_impresora));
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                enableTestButton(true);
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                    }
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(res.getString(R.string.str_alerta));
                    builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enableTestButton(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                    e.printStackTrace();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(res.getString(R.string.str_alerta));
                builder.setMessage(res.getString(R.string.str_impresora_sin_conexion));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
            }
        }


    }

    //Impresión Hubrox
    private void textHubrox() {
        if (mmDevice != null) {
            try {
                if(SessionManager.getFedatario().compareToIgnoreCase("false")==0) {
                    MainActivity.hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x01);
                    MainActivity.hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x01);
                    MainActivity.hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
                    MainActivity.hsBluetoothPrintDriver.BT_Write("TEST DE IMPRESION!!");
                    MainActivity.hsBluetoothPrintDriver.LF();
                }else{
                    MainActivity2.hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x01);
                    MainActivity2.hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x01);
                    MainActivity2.hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
                    MainActivity2.hsBluetoothPrintDriver.BT_Write("TEST DE IMPRESION!!");
                    MainActivity2.hsBluetoothPrintDriver.LF();
                }
            }catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(res.getString(R.string.str_alerta));
                builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
                e.printStackTrace();
            }
            enableTestButton(true);
            imprime_hubros=false;
        }
    }


    //Impresión TSC
    public void textTSC() {
        TscDll.openport(printerMac);
        String sta=TscDll.status();

        if (sta.compareToIgnoreCase("-1")!=0) {

            //Setup the media size and sensor type info
            try{
                TscDll.setup(80, 29, 1, 4, 0, 0, 0);
                TscDll.clearbuffer();
                TscDll.sendpicture(80, 29, "/sdcard/ATM/atm_logo_gris.jpg");
                //Sleeper.sleep(100);
                TscDll.printlabel(1, 1);
                TscDll.clearbuffer();
                //TscDll.sendcommand("PRINT 1");
                TscDll.sendcommand(getConfigLabelTSC());
                TscDll.closeport();
                enableTestButton(true);

            }catch(Exception e){
                Log.i("Logo","LOG "+e.getMessage());
                TscDll.closeport();
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(res.getString(R.string.str_alerta));
                builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
            }

        }else{
            TscDll.closeport();

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle(res.getString(R.string.str_alerta));
            builder.setMessage(res.getString(R.string.str_encender_impresora));
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    enableTestButton(true);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.red_alert));
            pbutton.setTextColor(ContextCompat.getColor(SettingsActivity.this,R.color.window_background));
        }
    }

    private byte[] getConfigLabelTSC() {
        byte[] cpcl = null;
        String cpclConfigLabel;

        cpclConfigLabel = "! 0 200 200 "+(100)+" 1\r\n"+
                "PW 400\r\n"+
                "TONE 0\r\n"+
                "SPEED 3\r\n"+
                "ON-FEED IGNORE\r\n"+
                "NO-PACE\r\n"+
                "BAR-SENSE\r\n"+
                "T 5 2 50 "+(0)+" Test Impresión!!"+"\r\n"+
                "PRINT\r\n";
        try {
            cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cpcl;
    }

    private void enableTestButton(final boolean enabled) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(enabled==false) {
                    pk_loading = ProgressDialog.show(SettingsActivity.this, res.getString(R.string.str_sistema), res.getString(R.string.str_conectando), false, false);
                }else{
                    pk_loading.dismiss();
                }

            }
        });
    }

}
