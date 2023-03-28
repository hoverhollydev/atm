package d4d.com.atm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tscdll.TSCActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import d4d.com.atm.comunication.WebSocketComunication;
import d4d.com.atm.data_base.DBParqueoLite;
import d4d.com.atm.models.Citacion;
import d4d.com.atm.utils.SessionManager;
import d4d.com.atm.utils.Sleeper;
import d4d.com.atm.utils.Utils;
import d4d.com.atm.utils.Validacion;
import d4d.com.atm.utils.Version;
import de.hdodenhof.circleimageview.CircleImageView;
import driver.Contants;
import driver.HsBluetoothPrintDriver;

import static d4d.com.atm.comunication.WebSocketComunication.banderaCerrarSesion;
import static d4d.com.atm.comunication.WebSocketComunication.latitud;
import static d4d.com.atm.comunication.WebSocketComunication.longitud;
import static d4d.com.atm.comunication.WebSocketComunication.validaConfirmacion;
import static d4d.com.atm.comunication.WebSocketComunication.webSocketConnection;

public class MainActivity extends AppCompatActivity {

    //Usuario
    private static SessionManager session;
    private DrawerLayout drawerLayout;
    private static Resources res;
    private CircleImageView imgPerfil;
    private TextView txtMail,txtName;
    private NavigationView navigationView;
    private Menu menu;
    private MenuItem mnuContadorCitacion;
    private MenuItem nav_usuario;
    private static MainActivity myInstance;
    public static Context context = null;
    //foto
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int SELECT_PICTURE = 300;
    private String mCurrentPhotoPath="";
    private static int contFoto = 0;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    // File of the photo taken with camera
    private File mFilePhotoTaken;
    Intent intentC;
    String packUri="";
    private Uri mUriPhotoTaken;
    //UI controles
    private Toolbar toolbar;
    private static EditText txt_placa;
    private static TextView txt_descripcion_vehiculo;
    private static EditText txt_placa_add;
    private static LinearLayout lnl_edit_text;
    private static TextInputLayout text_input;
    private static EditText txt_lugar_ocurencia;
    private static EditText txt_observacion;
    private static AutoCompleteTextView autoCompleteTextView;
    private ImageView img_consultar_placa;
    private static ImageView img_foto_infraccion;
    private static ImageView img_foto_infraccion2;
    private FloatingActionsMenu expandableMenuInfraction;
    private FloatingActionButton menuItemTakePhoto;
    private FloatingActionButton menuItemSendPrinter;
    private static ProgressDialog pk_loading;
    private static ProgressDialog pk_loading_rc;
    private static CheckBox ckb_coip;
    private static CheckBox ckb_ordenanza;
    private static CheckBox ckb_resolucion;
    //Variables
    private static int numero_secuencial= 0;
    private String autoIncremento= "";
    private String idDisp= "";
    private static String num_boleta= "";
    private static String nombre_agente = "";
    private static String cod_agente = "";
    private static String ley="";
    private static String referencia="";
    private static String articulo="";
    private static String numeral="";
    private static String fecha= "";
    private static String direccion= "";
    private static String zona= "";
    private static String provincia= "";
    private static String localidad= "";
    private static String institucion= "";
    private static String observacion= "";
    private static String imagen1= "";
    private static String imagen2= "";
    private static String descripcion = "";
    private static String descripcionH = "";
    private static String descripcion_p = "";
    private static String descripcion_p2 = "";
    private static String observacion_impresion_ticket = "";
    private static String estado = "";
    private static String id_citacionDB="";
    private static String valor="";
    private static String nombreUsuario="";
    private static String licencia_placa="";
    private static String solo_fecha="";
    private static String tipo_licencia="";
    private static String identificacion="";
    private String nombreUsuarioImp="";
    private String licenciaPlacaImp="";
    private String canton="";
    private static String auxLat="0.00";
    private static String auxLon="0.00";
    private static String placa = "";
    private static boolean valida_guardar=true;
    private static boolean banderamsjplaca=false;
    private boolean validaPlaca = false;
    private static boolean notificacion =false;
    public static boolean responseOK = false;
    //Procesos
    private static Citacion citacion;
    //GuardarMulta
    private static boolean isLicencia=false;
    private static JSONObject jsonCitation;
    private static JSONObject jsonSendCitation;
    private static String ws_enviar_inccidente = "WS_ingresoCitaciones";
    private static String ts = "";
    //Impresora Zebra
    private ZebraPrinter printer;
    private Connection connection;
    private int espd=25;
    private int espc=25;
    private int espcp=25;
    //Impresora TSC
    TSCActivity TscDll = new TSCActivity();
    //Impresora Hubrox
    private boolean imprime_hubros=false;
    public static Bitmap mBitmap=null;
    public static HsBluetoothPrintDriver hsBluetoothPrintDriver;
    //Bluetooth
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    String printerMac = "";
    private static String nombreImpresora="";
    private boolean validaImpresora=false;
    //SQLlite
    private static DBParqueoLite admin;
    private static SQLiteDatabase bd_citacion;
    //Busqueda AutoComplete
    private static ArrayAdapter<String> adapter;
    private static final List<String> ltsInfraccion = new ArrayList<String>();
    public static MainActivity getMyInstance() {
        return myInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myInstance = MainActivity.this;
        context=MainActivity.this;
        res = getResources();
        session = new SessionManager(context);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        View header = navigationView.getHeaderView(0);
        imgPerfil=(CircleImageView)header.findViewById(R.id.circle_image);
        txtName=(TextView)header.findViewById(R.id.username);
        txtMail=(TextView)header.findViewById(R.id.email);
        mnuContadorCitacion=menu.getItem(0);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
            cargarDatosLogin();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu_drawer);
        getSupportActionBar().setTitle("CExIA ,  ");

        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try{
                        actualizaHoraFecha();
                        Thread.sleep(1000);
                    }
                    catch ( Throwable th ) {}
                }
            }
        }).start();

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.txt_buscar_infraccion);
        autoCompleteTextView.setHorizontallyScrolling(false);
        autoCompleteTextView.setMaxLines(Integer.MAX_VALUE);
        txt_placa = (EditText) findViewById(R.id.txt_placa);
        txt_placa.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txt_descripcion_vehiculo = (TextView) findViewById(R.id.txt_descripcion_vehiculo);
        img_consultar_placa = (ImageView) findViewById(R.id.img_consultar_placa);
        txt_lugar_ocurencia = (EditText) findViewById(R.id.txt_lugar_ocurencia);
        txt_observacion = (EditText) findViewById(R.id.txt_observacion);
        img_foto_infraccion = (ImageView) findViewById(R.id.img_foto_infraccion);
        img_foto_infraccion2 = (ImageView) findViewById(R.id.img_foto_infraccion2);
        expandableMenuInfraction = (FloatingActionsMenu) findViewById(R.id.expanded_menuInfraction);
        menuItemTakePhoto = (FloatingActionButton) findViewById(R.id.addPhoto);
        menuItemSendPrinter = (FloatingActionButton) findViewById(R.id.sendPrinter);
        lnl_edit_text = (LinearLayout) findViewById(R.id.ll_add_edit_text);

        ckb_coip = (CheckBox) findViewById(R.id.ckb_coip);
        ckb_ordenanza = (CheckBox) findViewById(R.id.ckb_ordenanza);
        ckb_resolucion = (CheckBox) findViewById(R.id.ckb_resolucion);

        inicialeVariables();

        //Bluethooh
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
    }


    private String dia, mes, año, hora, minutos, segundos;
    private Calendar calendario = new GregorianCalendar();
    public void actualizaHoraFecha() {
        Date fechaHoraActual = new Date();
        calendario.setTime(fechaHoraActual);
        hora = String.valueOf(calendario.get(Calendar.HOUR_OF_DAY));
        minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE) : "0" + calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND) : "0" + calendario.get(Calendar.SECOND);
        dia = calendario.get(Calendar.DATE) > 9 ? "" + calendario.get(Calendar.DATE) : "0" + calendario.get(Calendar.DATE);
        mes = calendario.get(Calendar.MONTH) > 9 ? "" + calendario.get(Calendar.MONTH) : "0" + calendario.get(Calendar.MONTH);
        año = calendario.get(Calendar.YEAR) > 9 ? "" + calendario.get(Calendar.YEAR) : "0" + calendario.get(Calendar.YEAR);
        Calendar ahora = Calendar.getInstance();
        final int anioAc= ahora.get(Calendar.YEAR);
        int numeroMesAc =ahora.get(Calendar.MONTH)+ 1;
        final int diaAc =ahora.get(Calendar.DAY_OF_MONTH);
        int diaNumero = ahora.get(Calendar.DAY_OF_WEEK) - 1;

        String diaDe="",mesDe="";
        switch (diaNumero){
            case 1: diaDe = "Lunes";break;
            case 2: diaDe = "Martes";break;
            case 3: diaDe = "Miércoles";break;
            case 4: diaDe = "Jueves";break;
            case 5: diaDe = "Viernes";break;
            case 6: diaDe = "Sábado";break;
            case 7: diaDe = "Domingo";break;}
        switch (numeroMesAc){
            case 1: mesDe = "Enero";break;
            case 2: mesDe = "Febrero";break;
            case 3: mesDe = "Marzo";break;
            case 4: mesDe = "Abril";break;
            case 5: mesDe = "Mayo";break;
            case 6: mesDe = "Junio";break;
            case 7: mesDe = "Julio";break;
            case 8: mesDe = "Agosto";break;
            case 9: mesDe = "Septiembre";break;
            case 10: mesDe = "Octubre";break;
            case 11: mesDe = "Noviembre";break;
            case 12: mesDe = "Diciembre";break;}
        final String finalDiaDe = diaDe;
        final String finalMesDe = mesDe;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //getSupportActionBar().setTitle("ATM, "+"Hoy es "+ finalDiaDe.toLowerCase() +" "+diaAc+" de "+ finalMesDe.toLowerCase()+" de "  + anioAc+", son las "+ hora + ":" + minutos + ":" + segundos);
                getSupportActionBar().setTitle("CExIA "+" "+diaAc+"/"+ finalMesDe.toLowerCase().substring(0,3)+"/"  + anioAc);
            }
        });
    }

    public void cargarDatosLogin() {
        if (session.isLoggedIn() == true) {
            String[] nombre = session.getNombre().split(" ");
            String[] apellido = session.getApellido().split(" ");
            String nombre_completo = nombre[0].toUpperCase()+" "+apellido[0].toUpperCase();
            String cod ="Cod: " + session.getCodAgente();

            if (cod.compareToIgnoreCase("Cod: ") != 0) {
                txtName.setText(nombre_completo);
                txtMail.setText(cod);
            } else {
                txtName.setText("Usuario");
                txtMail.setText(cod);
            }

            nav_usuario = menu.findItem(R.id.nav_salir);
            nav_usuario.setTitle(R.string.cerrar_sesion);
        }
    }

    public static String cargarExcelCoip(){
        String versionCoip="";
        try{
            String path = "/sdcard/ATM/MultasElectronica.xls";
            File file = new File(path);
            if (file.exists()){
                Utils.readExcelFile(path, SessionManager.getFedatario());
                try {
                    versionCoip=Utils.getVersionCoip(path);
                }catch (Exception e){
                }finally {
                    if(versionCoip.compareToIgnoreCase("")==0) {
                        versionCoip = "27.08.2017";
                    }
                }

            }else{
                Utils.alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_sin_excel_actual), context);
            }
        } catch (Exception e){
            Log.i("ERROR","Excel lectura "+e.getMessage());
            //Utils.alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_sin_excel_actual), context);
        }
        session.setVersionCOIP(versionCoip);
        return versionCoip;
    }

    public void inicialeVariables(){
        //Para eliminar filas con estado enviada
        Utils.eliminarCitacionesOffline(context);
        cargarExcelCoip();
        //Inicializando interfaz Hubrox
        InputStream ims = null;
        try {
            ims = getResources().getAssets().open("atm_logo_gris.jpg");
            mBitmap = BitmapFactory.decodeStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
            alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_sin_logo));
        }
        hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        cargarListaInfracion();
        drawerLayout.setOnTouchListener(new  View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(drawerLayout.isInTouchMode()) {
                    consultarCitacionesRealizadas();
                }
                return false;
            }
        });

        ckb_coip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarCitacionesRealizadas();
                ckb_coip.setChecked(true);
                ckb_ordenanza.setChecked(false);
                ckb_resolucion.setChecked(false);
                cargarListaInfracion();
                //consultarCitacionesOffline();
            }
        });

        ckb_ordenanza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ckb_ordenanza.setChecked(true);
                ckb_coip.setChecked(false);
                ckb_resolucion.setChecked(false);
                cargarListaInfracion();
            }
        });

        ckb_resolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ckb_resolucion.setChecked(true);
                ckb_ordenanza.setChecked(false);
                ckb_coip.setChecked(false);
                cargarListaInfracion();
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.setText("");
                ley="";
                referencia="";
                descripcion="";
                descripcion_p="";
                descripcion_p2="";
                descripcionH="";
                articulo="";
                numeral="";
                notificacion=false;
                valor="";
                observacion_impresion_ticket="";
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String s = parent.getItemAtPosition(position).toString();
                    String cadena = s.substring(0, 8);
                    int pos_fin_art=0;
                    int pos_fin_num=0;
                    for(int i=0;i<cadena.length();i++){
                        if (cadena.charAt(i) == '-') {
                            pos_fin_art=i;
                        }
                        if (cadena.charAt(i) == ' ') {
                            pos_fin_num=i;
                        }
                    }

                    if(pos_fin_num==0){
                        pos_fin_num=cadena.length();
                    }
                    articulo = cadena.substring(0,pos_fin_art);
                    numeral = cadena.substring(pos_fin_art+1,pos_fin_num);

                    if(licencia_placa.length()>2) {

                        if (Utils.excelCoipOrdenanza.containsKey(articulo + "-" + numeral + "des")) {
                            notificacion = false;
                            controlarSiNotificacion(articulo, numeral);

                            ley = Utils.excelCoipOrdenanza.get(articulo + "-" + numeral + "ley");
                            referencia = Utils.excelCoipOrdenanza.get(articulo + "-" + numeral + "ref");
                            descripcion = Utils.excelCoipOrdenanza.get(articulo + "-" + numeral + "des");
                            valor = Utils.excelCoipOrdenanza.get(articulo + "-" + numeral + "val");
                            observacion_impresion_ticket = Utils.excelCoipOrdenanza.get(articulo + "-" + numeral + "obs");

                            //Log.i("observacion_impresion_ticket",observacion_impresion_ticket+" ____");

                            if (valor != null) {
                                String val = valor.replace(",", ".");
                                valor = val;
                            }
                            descripcion_p = cadena_sin_tildes_caracter(descripcion);
                            descripcion_p2 = descripcion;
                            if (ley.compareToIgnoreCase("COIP") == 0) {
                                descripcionH = ley.toUpperCase() + (" Art." + articulo + " Num." + numeral + " " + descripcion);
                                descripcion = (ley + " Art." + articulo + " Num." + numeral + " " + descripcion).toUpperCase();
                            } else if (ley.compareToIgnoreCase("ORDENANZA") == 0) {
                                descripcionH = ley.toUpperCase() + (" " + referencia + " Art." + articulo + " Num." + numeral + " " + descripcion);
                                descripcion = (ley + " " + referencia + " Art." + articulo + " Num." + numeral + " " + descripcion.toUpperCase());
                            } else {
                                descripcionH = ley.toUpperCase() + (" " + referencia + " " + articulo + ", " + numeral + " " + descripcion);
                                descripcion = (ley + " " + referencia + " " + articulo + ", " + numeral + " " + descripcion.toUpperCase());
                            }

                            Log.i("descripcion", descripcion + " ____");
                            Log.i("descripcionH", descripcionH + " ____");
                        } else {
                            alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_error_excel));
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(res.getString(R.string.app_name));
                        builder.setMessage("Por favor primero ingrese una licencia o placa");
                        builder.setCancelable(false);
                        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                autoCompleteTextView.setText("");
                                ley="";
                                referencia="";
                                descripcion="";
                                descripcion_p="";
                                descripcion_p2="";
                                descripcionH="";
                                articulo="";
                                numeral="";
                                notificacion=false;
                                valor="";
                                observacion_impresion_ticket="";
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_button));
                        pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e){
                }

            }
        });

        txt_placa.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    ejecutar_evento_placa();
                    expandableMenuInfraction.collapse();
                }
                return false;
            }
        });

        txt_placa.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        img_consultar_placa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ejecutar_evento_placa();
                expandableMenuInfraction.collapse();
            }
        });

        img_foto_infraccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contFoto = 0;
                showOptions();
                expandableMenuInfraction.collapse();
            }
        });

        img_foto_infraccion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contFoto = 1;
                showOptions();
                expandableMenuInfraction.collapse();
            }
        });

        menuItemTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    expandableMenuInfraction.collapse();
                    takeFoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        menuItemSendPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableMenuInfraction.collapse();
                direccion=txt_lugar_ocurencia.getText().toString().toUpperCase();
                observacion=txt_observacion.getText().toString().toUpperCase();
                Validacion validar;
                boolean ban_pla;
                boolean ban_lic;
                String placa_aux="";
                boolean ban_pla2=false;

                Log.i("citacion impre",licencia_placa);
                validar =  new Validacion(licencia_placa);
                ban_pla=validar.placaAuto();
                ban_lic=validar.cedulaRUC();
                if(text_input!=null) {
                    placa_aux = txt_placa_add.getText().toString();
                    validar = new Validacion(placa_aux);
                    ban_pla2 = validar.placaAuto();
                    if(ban_pla2){
                        placa=placa_aux;
                    }
                }

                //Identificado Bluetooth MAC
                if(licencia_placa.length()>5) {
                    if(descripcion.compareToIgnoreCase("")!=0) {
                        if (ban_pla == true || ban_lic == true) {
                            if (direccion.length() > 2) {
                                if (imagen1.length() > 2 || imagen2.length() > 2) {
                                    validar = new Validacion(placa);
                                    //Log.i("licencia_placa44", placa+"___");
                                    if (ban_pla2 == true || placa_aux.length()==0 || ban_pla==true || ban_lic == true) {
                                        //Log.i("licencia_placa5", licencia_placa);
                                        imprimirCitacion();
                                    } else {
                                        if(text_input!=null) {
                                            alertDialog(res.getString(R.string.str_atm), res.getString(R.string.str_placa_correcta_add));
                                            txt_placa_add.requestFocus();
                                        }
                                    }
                                } else {
                                    alertDialog(res.getString(R.string.str_atm), res.getString(R.string.str_valida_foto));
                                }
                            } else {
                                alertDialog(res.getString(R.string.str_atm), res.getString(R.string.str_valida_lugar));
                                txt_lugar_ocurencia.requestFocus();
                            }
                        } else {
                            alertDialog(res.getString(R.string.str_atm), res.getString(R.string.str_valida_placa));
                            txt_placa.requestFocus();
                        }
                    } else {
                        alertDialog(res.getString(R.string.str_atm), res.getString(R.string.str_valida_infracion));
                        autoCompleteTextView.requestFocus();
                    }
                } else {
                    alertDialog(res.getString(R.string.str_atm), res.getString(R.string.str_valida_placa));
                    txt_placa.requestFocus();
                }
            }
        });
    }

    private static ProgressDialog pk_ConsultaLP;
    private static boolean msjConsultaLP=true;
    static Handler handler; // declared before onCreate
    static Runnable myRunnable;

    private void ejecutar_evento_placa(){
        notificacion=false;
        articulo="";
        numeral="";
        limpiarMainActivityPlaca();
        licencia_placa = txt_placa.getText().toString();
        isLicencia=false;
        if(licencia_placa.length()>5 && licencia_placa.length()<14 ) {
            Validacion validar;
            validar = new Validacion(licencia_placa);
            //try {
                if ((validar.placaAuto() == true) || (validar.cedulaRUC() == true || licencia_placa.equals("mensajeRecibido"))) {
                    licencia_placa = licencia_placa.trim();
                    if (licencia_placa.length() == 7 || licencia_placa.length() == 6 || licencia_placa.length() == 5) {
                        isLicencia = false;
                        if (text_input != null) {
                            String fontFamily = "null";
                            try {
                                fontFamily = String.valueOf(txt_placa_add.getTag());
                            } catch (Exception e) {
                                fontFamily = "null";
                            } finally {
                                if (fontFamily.compareToIgnoreCase("placa_add") == 0) {
                                    //Log.i("NOMBRE EDITTEXT", fontFamily + " DDDD");
                                    lnl_edit_text.removeViewAt(0);
                                    text_input = null;
                                }
                            }
                        }
                        identificacion = "";
                        tipo_licencia = "";
                        nombreUsuario = "";
                        txt_descripcion_vehiculo.setText("");
                        placa = licencia_placa;
                    }
                    if (licencia_placa.length() == 10) {
                        isLicencia = true;
                        if (text_input == null) {
                            text_input = (TextInputLayout) LayoutInflater.from(context).inflate(R.layout.campo_placa, null);
                            text_input.setHint(res.getString(R.string.str_placa));
                            //text_input.set
                            lnl_edit_text.addView(text_input, 0);
                            txt_placa_add = (EditText) lnl_edit_text.findViewById(R.id.txt_placa_add);
                            txt_placa_add.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                            txt_placa_add.setTag("placa_add");
                            txt_placa_add.setTextSize(16);
                        }
                        placa = "";
                        txt_descripcion_vehiculo.setText("");
                        identificacion = licencia_placa;
                        tipo_licencia = "";
                        nombreUsuario = "";
                    }

                    if (isLicencia == false) {
                        consultarPlaca(placa);
                        pk_ConsultaLP = null;
                        pk_ConsultaLP = ProgressDialog.show(context, res.getString(R.string.str_consulta_placa), res.getString(R.string.str_esperar), false, false);
                        handler = new Handler();
                        myRunnable = new Runnable() {
                            public void run() {
                                if (pk_ConsultaLP != null) {
                                    pk_ConsultaLP.dismiss();
                                }
                                if (msjConsultaLP == true) {
                                    txt_descripcion_vehiculo.setText("");
                                    alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_datos_inestable_placa));
                                    //onStop();
                                } else {
                                    msjConsultaLP = true;
                                }
                            }
                        };
                        handler.postDelayed(myRunnable, 5000);
                    } else {
                        consultarLicencia(identificacion);
                        pk_ConsultaLP = null;
                        pk_ConsultaLP = ProgressDialog.show(context, res.getString(R.string.str_consulta_licencia), res.getString(R.string.str_esperar), false, false);
                        //pk_loading.setCancelable(true);
                        handler = new Handler();
                        myRunnable = new Runnable() {
                            public void run() {
                                if (pk_ConsultaLP != null) {
                                    pk_ConsultaLP.dismiss();
                                }
                                if (msjConsultaLP == true) {
                                    txt_descripcion_vehiculo.setText("");
                                    alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_datos_inestable_placa));
                                } else {
                                    msjConsultaLP = true;
                                }
                            }
                        };
                        handler.postDelayed(myRunnable, 5000);
                    }
                    responseOK = false;
                    //Oculta el teclado
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    validaPlaca = true;
                } else {
                    if (validaPlaca == true) {
                        //Toast.makeText(getActivity(),res.getString(R.string.intentar_de_nuevo) , Toast.LENGTH_SHORT).show();
                        txt_descripcion_vehiculo.setText("");
                    }
                    if (validaPlaca == false) {
                        Log.i("ValidaPlaca3", "3");
                        alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_placa_correcta));
                    }
                    validaPlaca = false;
                }
            //} catch (Exception e) {
               // Log.i("ValidaPlaca2", "2");
                //alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_placa_correcta));
           // }
        }else{
            alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_placa_correcta));
            Log.i("ValidaPlaca1", "1");
            licencia_placa="";
            placa = "";
            txt_descripcion_vehiculo.setText("");
            identificacion = "";
            tipo_licencia = "";
            nombreUsuario = "";
        }
    }

    public String getCurrentTimeStamp() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdsf=new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        solo_fecha=sdsf.format(calendar.getTime());
        Long tsl = calendar.getTimeInMillis();
        ts = tsl.toString();
        String date=sdf.format(calendar.getTime());
        return date;
    }

    public void enviarCitacion() {

        if (session.getToken() != null && session.getUuid() != null) {
            jsonCitation = new JSONObject();
            jsonSendCitation = new JSONObject();
            try {
                jsonCitation.put("Usuario", "Envio");
                //jsonCitation.put("notifica", notificacion);
                jsonCitation.put("Ticket", citacion.getJsonCitacion());
                jsonSendCitation = new JSONObject().
                        accumulate("tipo", "ws").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid()).
                        accumulate("timestamp", ts).
                        accumulate("ws", ws_enviar_inccidente).
                        accumulate("parametros", jsonCitation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        validaConfirmacion=false;


        if(webSocketConnection!=null) {
            Log.i("citacion",jsonSendCitation.toString());
            if (webSocketConnection.isConnected() && banderaCerrarSesion == false) {
                webSocketConnection.sendMessage(jsonSendCitation.toString());
            }
        }
    }
    /*
    {
  tipo: "controlarSiNotificacion",
  uuid: "id de usuario",
  token: "token de acceso",
  parametros:{
    placa: ""           // si placa no cedula
    cedula: "",  // si cedula no placa
    articulo: "",
    literal: "",
    id: ""  // sera repitido en la respuesta
  }
}
     */

    public void controlarSiNotificacion(String art, String num) {
        Log.i("LLamadi TOKEN",session.getToken()+" "+num);
        Log.i("LLamadi UUID",session.getUuid()+" "+num);
        Log.i("LLamadi VALNOTI",art+" "+num);

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        Long tsl = calendar.getTimeInMillis();
        String tsNoti = tsl.toString();

        JSONObject jsonParametros;
        JSONObject jsonNoti = null;

        if (session.getToken() != null && session.getUuid() != null) {
            jsonParametros = new JSONObject();
            jsonNoti = new JSONObject();
            try {
                if(!isLicencia){
                    jsonParametros.put("placa", licencia_placa);}
                else{
                    jsonParametros.put("cedula", licencia_placa);
                }

                jsonParametros.put("articulo", art);
                jsonParametros.put("literal", num);
                jsonParametros.put("id", tsNoti);
                jsonNoti = new JSONObject().
                        accumulate("tipo", "controlarSiNotificacion").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid()).
                        accumulate("parametros", jsonParametros);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        validaConfirmacion=false;


        if(webSocketConnection!=null) {
            if (webSocketConnection.isConnected() && banderaCerrarSesion == false) {
                Log.i("Notificacion",jsonNoti.toString()+"  _HHHHHHH");
                webSocketConnection.sendMessage(jsonNoti.toString());
            }
        }
    }

    public static void controlarSiNotificacionRespuesta(String message) {
        JSONObject messageRecive = null;
        try {
            messageRecive = new JSONObject(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.i("LLamadi VALNOTI",art+" "+num);
    }

    @Override
    public void onResume() {
        super.onResume();
        conteoMensaje=0;
        WebSocketComunication.valida_login=2;
        WebSocketComunication.banderaCerrarSesion = false;
        if(WebSocketComunication.contextWs==null) {
            Intent myService = new Intent(this, WebSocketComunication.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(myService);
                Log.i("startForegroundService", "ANDROID OREO O SUPERIOR");
            } else {
                startService(myService);
                Log.i("startService", "ANDROID NOUGAT O MARSMELLOW");
            }
        }
    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Elija una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    takeFoto();
                }else if(option[which] == "Elegir de galería"){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static void cargarDatosLicencia(String licenciaDatos){
        licenciaDatos=licenciaDatos.replace(":", ": ");
        licenciaDatos=licenciaDatos.replace("{", "");
        licenciaDatos=licenciaDatos.replace("}", "");
        licenciaDatos=licenciaDatos.replace(",\"", "\n");
        licenciaDatos=licenciaDatos.replace("\"", "");
        licenciaDatos=licenciaDatos.replace("identifica", "licencia");
        licenciaDatos=licenciaDatos.replace("tipo_Licen", "tipo licencia");
        licenciaDatos=licenciaDatos.replace("licenciasCaducadas", "licencias caducadas");
        licenciaDatos=licenciaDatos.replace(",", ", ");
        txt_descripcion_vehiculo.setText(licenciaDatos);
    }

    public static void cargarDatosVehiculo(String vehiculoDatos){
        vehiculoDatos=vehiculoDatos.replace(":", ": ");
        vehiculoDatos=vehiculoDatos.replace("{", "");
        vehiculoDatos=vehiculoDatos.replace("}", "");
        vehiculoDatos=vehiculoDatos.replace(",\"", "\n");
        vehiculoDatos=vehiculoDatos.replace("\"", "");
        vehiculoDatos=vehiculoDatos.replace("manufacturing", "año de fabricación");
        vehiculoDatos=vehiculoDatos.replace("tipo_Servicio", "tipo de servicio");
        vehiculoDatos=vehiculoDatos.replace("fecha_vigencia", "fecha de vigencia");
        vehiculoDatos=vehiculoDatos.replace("fechaCaducidad", "fecha de caducidad");
        vehiculoDatos=vehiculoDatos.replace("fechaMatricula", "fecha de matrícula");
        vehiculoDatos=vehiculoDatos.replace("perm_oper", "permiso de operación");
        vehiculoDatos=vehiculoDatos.replace("tipoTran", "tipo de transporte");
        vehiculoDatos=vehiculoDatos.replace("unidad_vinculada", "unidad vinculada");
        vehiculoDatos=vehiculoDatos.replace("unidad_gps", "unidad GPS");
        vehiculoDatos=vehiculoDatos.replace("fecha_hora_gps", "fecha y hora de GPS");
        vehiculoDatos=vehiculoDatos.replace(",", ", ");
        txt_descripcion_vehiculo.setText(vehiculoDatos);
    }

    //Imagen Base64
    public static String getStringImage(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgString= Base64.encodeToString(b, Base64.NO_WRAP);
        return imgString;
    }

    /*private void eliminarArchivoImagen(){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = storageDir.getPath() + "/fotos_citaciones";
        File temp = new File(path);
        if(temp.exists()) {
            File[] files = temp.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            temp.delete();
        }
    }*/

    public String establecerNumeroSecuencial(){

        idDisp=session.getIdDsp().substring(2, 6);
        numero_secuencial= Integer.parseInt(consultarAutoIncrement());
        int numero_secuencial_plataforma= Integer.parseInt(session.getSerialMasAlto());
        Log.i("numeroPlataforma","NSPA "+numero_secuencial_plataforma+">"+numero_secuencial+ " NSAA");
        if(numero_secuencial_plataforma>numero_secuencial){
            numero_secuencial=(numero_secuencial_plataforma+1);
        }else{
            numero_secuencial=numero_secuencial+1;
        }
        autoIncremento = Utils.getNumberCitation(numero_secuencial);
        num_boleta=idDisp+autoIncremento;
        //Log.i("num_boleta",num_boleta);
        Log.i("AUTOINCREMENTO",num_boleta+" ");

        return num_boleta;
    }

    public void cargarDatosCitacion(){
        if(isLicencia==true) {
            placa = txt_placa_add.getText().toString();
        }else{
            placa=licencia_placa;
        }

        //Datos Citación
        num_boleta=establecerNumeroSecuencial();
        String[] nombre = session.getNombre().split(" ");
        String[] apellido = session.getApellido().split(" ");
        nombre_agente = nombre[0].toUpperCase()+" "+apellido[0].toUpperCase();
        cod_agente =session.getCodAgente();
        fecha=getCurrentTimeStamp();
        zona=session.getZona().toUpperCase();
        provincia="GUA";
        localidad=session.getCanton().toUpperCase();//"GUA";
        canton="GUAYAQUIL";
        institucion="ATM";
        uuid=session.getUuid();
        transmision="online";
        precision="alta";
        cad_cordenadas();
        Log.i("citacion",identificacion+", "+placa);
        citacion = new Citacion(nombre_agente, "", identificacion, placa, cod_agente, articulo, numeral, fecha, direccion, zona,
                provincia, localidad, "", "", "", institucion, observacion, num_boleta, tipo_licencia, "",
                descripcion_p2, auxLat, auxLon, imagen1, imagen2,transmision, precision, notificacion);
    }

    public String cad_direccion(String text){
        int tam_cad=text.length();
        int tam_lin=29;
        int tam_ini_cad=0;
        int tam_fin_cad=0;
        String parrafo=text;
        String linea;
        String parrafo_final="";
        int tlineas=0;
        int ac=29;
        for(int k=0;k<tam_cad;k++){
            if(k>ac){
                tlineas=tlineas+1;
                ac=ac+29;
            }
        }
        int tam_aux_parrafo;
        for(int i=0;i<tlineas;i++) {
            for(int j=0;j<tam_lin;j++) {
                if (parrafo.charAt(j) == ' ') {
                    tam_fin_cad = j;
                }
            }
            linea = parrafo.substring(tam_ini_cad, tam_fin_cad+1);
            parrafo_final = parrafo_final + linea +"\n";
            parrafo=parrafo.substring(tam_fin_cad+1);

            tam_aux_parrafo=parrafo.length();
            if(tam_aux_parrafo<29){
                linea = parrafo.substring(tam_ini_cad, tam_aux_parrafo);
                parrafo_final = parrafo_final + linea;
            }
            if((i+1)==tlineas && tam_aux_parrafo>=29){
                tlineas=tlineas+1;
            }
            espd=espd+25;
        }
        return parrafo_final;
    }

    public String cad_descripcion(String text){
        int tam_cad=text.length();
        int tam_lin=29;
        int tam_ini_cad=0;
        int tam_fin_cad=0;
        String parrafo=text;
        String linea;
        String parrafo_final="";
        int tlineas=0;
        int ac=29;
        for(int k=0;k<tam_cad;k++){
            if(k>ac){
                tlineas=tlineas+1;
                ac=ac+29;
            }
        }
        int tam_aux_parrafo;
        for(int i=0;i<tlineas;i++) {
            for(int j=0;j<tam_lin;j++) {
                if (parrafo.charAt(j) == ' ') {
                    tam_fin_cad = j;
                }
            }
            linea = parrafo.substring(tam_ini_cad, tam_fin_cad+1);
            parrafo_final = parrafo_final + linea +"\n";
            parrafo=parrafo.substring(tam_fin_cad+1);

            tam_aux_parrafo=parrafo.length();
            if(tam_aux_parrafo<29){
                linea = parrafo.substring(tam_ini_cad, tam_aux_parrafo);
                parrafo_final = parrafo_final + linea;
            }
            if((i+1)==tlineas && tam_aux_parrafo>=29){
                tlineas=tlineas+1;
            }
            espc=espc+25;
        }
        return parrafo_final;
    }

    public String cad_descripcion_p(String text){
        int tam_cad=text.length();
        int tam_lin=43;
        int tam_ini_cad=0;
        int tam_fin_cad=0;
        String parrafo=text;
        String linea;
        String parrafo_final="";

        int tlineas=0;
        int ac=43;
        for(int k=0;k<tam_cad;k++){
            if(k>ac){
                tlineas=tlineas+1;
                ac=ac+43;
            }
        }
        int tam_aux_parrafo;
        for(int i=0;i<tlineas;i++) {
            for(int j=0;j<tam_lin;j++) {
                if (parrafo.charAt(j) == ' ') {
                    tam_fin_cad = j;
                }
            }
            linea = parrafo.substring(tam_ini_cad, tam_fin_cad+1);
            parrafo_final = parrafo_final + linea +"\n";
            parrafo=parrafo.substring(tam_fin_cad+1);

            tam_aux_parrafo=parrafo.length();
            if(tam_aux_parrafo<29){
                linea = parrafo.substring(tam_ini_cad, tam_aux_parrafo);
                parrafo_final = parrafo_final + linea;
            }
            if((i+1)==tlineas && tam_aux_parrafo>=43){
                tlineas=tlineas+1;
            }
            espcp=espcp+25;
        }
        return parrafo_final;
    }


    private int espcf=25;
    public String cad_descripcion_p_fedatario(String text){
        int tam_cad=text.length();
        int tam_lin=29;
        int tam_ini_cad=0;
        int tam_fin_cad=0;
        String parrafo=text;
        String linea;
        String parrafo_final="";

        int tlineas=0;
        int ac=29;
        for(int k=0;k<tam_cad;k++){
            if(k>ac){
                tlineas=tlineas+1;
                ac=ac+29;
            }
        }
        int tam_aux_parrafo;
        for(int i=0;i<tlineas;i++) {
            for(int j=0;j<tam_lin;j++) {
                if (parrafo.charAt(j) == ' ') {
                    tam_fin_cad = j;
                }
            }
            linea = parrafo.substring(tam_ini_cad, tam_fin_cad+1);
            parrafo_final = parrafo_final + linea +"\n";
            parrafo=parrafo.substring(tam_fin_cad+1);

            tam_aux_parrafo=parrafo.length();
            if(tam_aux_parrafo<29){
                linea = parrafo.substring(tam_ini_cad, tam_aux_parrafo);
                parrafo_final = parrafo_final + linea;
            }
            if((i+1)==tlineas && tam_aux_parrafo>=29){
                tlineas=tlineas+1;
            }
            espcf=espcf+25;
        }
        return parrafo_final;
    }

    private int espcfLeyImp=25;
    public String cad_descripcion_p_fedatarioLeyImp(String text){
        int tam_cad=text.length();
        int tam_lin=31;
        int tam_ini_cad=0;
        int tam_fin_cad=0;
        String parrafo=text;
        String linea;
        String parrafo_final="";

        int tlineas=0;
        int ac=31;
        for(int k=0;k<tam_cad;k++){
            if(k>ac){
                tlineas=tlineas+1;
                ac=ac+31;
            }
        }
        int tam_aux_parrafo;
        for(int i=0;i<tlineas;i++) {
            for(int j=0;j<tam_lin;j++) {
                if (parrafo.charAt(j) == ' ') {
                    tam_fin_cad = j;
                }
            }
            linea = parrafo.substring(tam_ini_cad, tam_fin_cad+1);
            parrafo_final = parrafo_final + linea +"\n";
            parrafo=parrafo.substring(tam_fin_cad+1);

            tam_aux_parrafo=parrafo.length();
            if(tam_aux_parrafo<31){
                linea = parrafo.substring(tam_ini_cad, tam_aux_parrafo);
                parrafo_final = parrafo_final + linea;
            }
            if((i+1)==tlineas && tam_aux_parrafo>=31){
                tlineas=tlineas+1;
            }
            espcfLeyImp=espcfLeyImp+25;
        }
        return parrafo_final;
    }

    ProgressDialog pk_loadingCitacion;
    public void imprimirCitacion(){
        if(valida_guardar==true) {
            cargarDatosCitacion();
            //Guardar Citación
            if((articulo.compareToIgnoreCase("387")==0)&&(numeral.compareToIgnoreCase("1")==0)) {
                //Log.i("Mensaje", "Enviada387");
                imagen1="123";
                imagen2="123";
                estado = "enviada";
                guardarCitacionBDLocal();
                cargarImpresora();
                valida_guardar = false;
            }else{
                pk_loadingCitacion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_enviando_citacion2), false, false);
                enviarCitacion();
                responseOK = false;
                menuItemSendPrinter.setEnabled(false);
                new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (pk_loadingCitacion != null) {
                                pk_loadingCitacion.dismiss();
                            }

                            try {
                                if (validaConfirmacion == true) {
                                    //Log.i("RespuestaHilo1", "Confirmada");
                                    estado = "enviada";
                                } else {
                                    //Log.i("RespuestaHilo1", "No confirmada");
                                    estado = "no enviada";
                                }
                                guardarCitacionBDLocal();
                            } catch (Throwable th) {
                                //Log.i("ERROR", "ERROR AL GUARDAR EN LA BASE DE DATOS");
                            }
                            //Log.i("tag", "This'll run 4000 milliseconds later");
                            cargarImpresora();
                            valida_guardar = false;
                        }
                    }, 4000
                );
            }
        }else{
            cargarImpresora();
        }

    }
    private static boolean banderaTimeLargo=true;
    public void cargarImpresora(){
        findBT();
        if (validaImpresora == true) {
            //Imprimir Citacion
            new Thread(new Runnable() {
                public void run() {
                    enableTestButton(false);
                    Looper.prepare();
                    if(nombreImpresora.compareToIgnoreCase("RW220") == 0) {
                        printer = connectZebra(printerMac);
                        int statusPrinter=5;
                        if (printer != null) {
                            statusPrinter=zebraPrinterVal();
                            if (statusPrinter == 0) {

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(res.getString(R.string.str_alerta));
                                builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                                builder.setCancelable(false);
                                builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        enableTestButton(true);
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                                pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(res.getString(R.string.str_alerta));
                            builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                            builder.setCancelable(false);
                            builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    enableTestButton(true);
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                        }
                    }else if(nombreImpresora.compareToIgnoreCase("HP-RP") == 0 || nombreImpresora.compareToIgnoreCase("RPP20") == 0) {
                        //hsBluetoothPrintDriver.stop();

                        hsBluetoothPrintDriver.stop();
                        Sleeper.sleep(1000);

                        validaStartHubrox=0;
                        imageHubrox();
                        if(imprime_hubros==true){

                            if(session.getFedatario().compareToIgnoreCase("true")==0){
                                if((articulo.compareToIgnoreCase("387")==0)&&(numeral.compareToIgnoreCase("1")==0)){
                                    //textHubroxArt387();
                                    textHubroxFedatario();
                                }else{
                                    textHubroxFedatario();
                                }
                            }else {
                                if((articulo.compareToIgnoreCase("387")==0)&&(numeral.compareToIgnoreCase("1")==0)){
                                    textHubroxArt387();
                                    //textHubroxFadetario();
                                }else{
                                    if(notificacion) {
                                        textHubroxNoti();
                                    }else{
                                        textHubrox();
                                    }

                                }
                            }
                        }
                        //Log.i("IMPIENDO","HUBROX");
                    }else if(nombreImpresora.compareToIgnoreCase("ALPHA") == 0 || nombreImpresora.compareToIgnoreCase("BT-SP")==0) {
                        textTSC();
                        Sleeper.sleep(3000);
                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(res.getString(R.string.str_alerta));
                        builder.setMessage(res.getString(R.string.str_valida_impresora));
                        builder.setCancelable(false);
                        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                enableTestButton(true);
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                    }
                    Looper.loop();
                    Looper.myLooper().quit();
                }
            }).start();
        }
    }

    public void reImprimir(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("Mensaje");
        builder.setMessage("Desea reimprimir?");
        builder.setCancelable(false);
        builder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cargarImpresora();
                    }
                });

        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        limpiarMainActivity();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public static void limpiarMainActivity(){
        if(isLicencia==true) {
            if (text_input != null) {
                String fontFamily="null";
                try {
                    fontFamily = String.valueOf(txt_placa_add.getTag());
                }catch (Exception e){
                    fontFamily="null";
                }
                finally {
                    if (fontFamily.compareToIgnoreCase("placa_add") == 0) {
                        //Log.i("NOMBRE EDITTEXT", fontFamily + " DDDD");
                        lnl_edit_text.removeViewAt(0);
                        text_input = null;
                    }
                }
                placa="";
            }
        }
        citacion = new Citacion("", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", false);

        //Eliminación de Citaciones
        Utils.eliminarCitacionesOffline(context);

        isLicencia=false;
        txt_descripcion_vehiculo.setText("");
        txt_lugar_ocurencia.setText("");
        txt_observacion.setText("");
        img_foto_infraccion.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.img_background_photo, null));
        img_foto_infraccion2.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.img_background_photo, null));
        autoCompleteTextView.setText("");
        ckb_coip.setChecked(true);
        ckb_ordenanza.setChecked(false);
        ckb_resolucion.setChecked(false);
        cargarListaInfracion();
        txt_placa.setText("");

        valida_guardar = true;
        licencia_placa="";
        placa="";
        identificacion="";
        tipo_licencia="";
        descripcion="";
        descripcion_p="";
        descripcion_p2="";
        descripcionH="";
        direccion="";
        observacion="";
        imagen1="";
        imagen2="";
        estado="";
        contFoto=0;
        num_boleta="";
        numero_secuencial=0;

        //eliminarArchivoImagen();
        validaConfirmacion = true;
        banderaTimeLargo=true;

        notificacion=false;
        txt_placa.requestFocus();
    }

    public static void limpiarMainActivityPlaca(){
        if(isLicencia==true) {
            if (text_input != null) {
                String fontFamily="null";
                try {
                    fontFamily = String.valueOf(txt_placa_add.getTag());
                }catch (Exception e){
                    fontFamily="null";
                }
                finally {
                    if (fontFamily.compareToIgnoreCase("placa_add") == 0) {
                        //Log.i("NOMBRE EDITTEXT", fontFamily + " DDDD");
                        lnl_edit_text.removeViewAt(0);
                        text_input = null;
                    }
                }
                placa="";
            }
        }
        citacion = new Citacion("", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", false);

        //Eliminación de Citaciones
        Utils.eliminarCitacionesOffline(context);

        //isLicencia=false;
        txt_descripcion_vehiculo.setText("");
        txt_lugar_ocurencia.setText("");
        txt_observacion.setText("");
        img_foto_infraccion.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.img_background_photo, null));
        img_foto_infraccion2.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.img_background_photo, null));
        autoCompleteTextView.setText("");
        ckb_coip.setChecked(true);
        ckb_ordenanza.setChecked(false);
        ckb_resolucion.setChecked(false);
        cargarListaInfracion();
       // txt_placa.setText("");

        valida_guardar = true;
        //licencia_placa="";
        placa="";
        identificacion="";
        tipo_licencia="";
        descripcion="";
        descripcion_p="";
        descripcion_p2="";
        descripcionH="";
        direccion="";
        observacion="";
        imagen1="";
        imagen2="";
        estado="";
        contFoto=0;
        num_boleta="";
        numero_secuencial=0;

        //eliminarArchivoImagen();
        validaConfirmacion = true;
        banderaTimeLargo=true;

        notificacion=false;


        //txt_placa.requestFocus();
    }

    private static String uuid="",transmision="",precision="alta";

    public static void guardarCitacionBDLocal(){
            DBParqueoLite admin = new DBParqueoLite(context, "administracion", null, 2);
            SQLiteDatabase bd = admin.getWritableDatabase();

            try {
                bd.execSQL("ALTER TABLE citacion_no_enviadas ADD COLUMN notifica TEXT");
            }catch (Exception e){

            }

            ContentValues registro = new ContentValues();
            registro.put("usuario", nombre_agente);
            registro.put("tipo_identificacion", "");
            registro.put("identificacion", identificacion);
            registro.put("placa", placa);
            registro.put("cod_agente", cod_agente);
            registro.put("articulo", articulo);
            registro.put("literal", numeral);
            registro.put("fecha", fecha);
            registro.put("direccion", direccion);
            registro.put("zona", zona);
            registro.put("provincia", provincia);
            registro.put("localidad", localidad);
            registro.put("distrito", "");
            registro.put("circuito", "");
            registro.put("subCircuito", "");
            registro.put("institucion", institucion);
            registro.put("observacion", observacion);
            registro.put("numBoleta", num_boleta);
            registro.put("tipoLicencia", tipo_licencia);
            registro.put("numCitacion", "");
            registro.put("descripcion", descripcion_p2);
            registro.put("latitud", auxLat);
            registro.put("longitud", auxLon);
            registro.put("imagen", imagen1);
            registro.put("imagen2", imagen2);
            registro.put("estado", estado);
            registro.put("timestamp", ts);
            registro.put("solofecha", solo_fecha);

            registro.put("valor", valor);
            if(estado.compareToIgnoreCase("enviada")==0){
                transmision="online";
            }else{
                transmision="offline";
            }
            registro.put("transmision", transmision);
            registro.put("precision", precision);
            registro.put("nombre_licencia", nombreUsuario);
            registro.put("descripcion_p", descripcion_p);
            registro.put("descripcion_p2", descripcion);
            registro.put("descripcionH", descripcionH);
            String sNotificacion=Boolean.toString(notificacion);
            Log.i("NOTIFICA",sNotificacion+" ____HHHJ");
            registro.put("notifica", sNotificacion);

            bd.insert("citacion_no_enviadas", null, registro);
            bd.close();

        mostrarContadorCitacionesNoEnviadas();
    }


    public static void mostrarContadorCitacionesNoEnviadas(){
        getMyInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(totalCitacionesNoenviadas()==0){
                    txtViewCount.setVisibility(View.GONE);
                    txtViewCount.setVisibility(View.INVISIBLE);
                }else{
                    txtViewCount.setText(totalCitacionesNoenviadas()+"");
                    txtViewCount.setVisibility(View.GONE);
                    txtViewCount.setVisibility(View.VISIBLE);
                }            }
        });
    }

    public boolean consultarDatosNumBoleta(Context context, TextView txtBoleta, String boleta) {
        boolean valida_ctacion=false;
        int num1=Utils.consultarNumeroCitaciones(context);
        //Log.i("ELIMINARCITACION",num1+" >>>>>>>>");
        if(num1>0) {
            Cursor fila = Utils.bd_citacionE.rawQuery("select *  from citacion_no_enviadas where numBoleta='"+boleta+"'", null);
            int tam = fila.getCount();
            if (fila != null) {
                if (fila.moveToFirst()) {
                    for (int i = 0; i < tam; i++) {
                        //String f0 = fila.getString(0);
                        String f1 = fila.getString(1);
                        String f3 = fila.getString(3);
                        String f4 = fila.getString(4);
                        String f5 = fila.getString(5);
                        //String f6 = fila.getString(6);
                       // String f7 = fila.getString(7);
                        String f8 = fila.getString(8);
                        String f9 = fila.getString(9);
                        //String f10 = fila.getString(10);
                        //String f11 = fila.getString(11);
                        String f12 = fila.getString(12);
                        //String f16 = fila.getString(16);
                        //String f17 = fila.getString(17);

                        String f18 = fila.getString(18);
                       // String f19 = fila.getString(19);
                        String f21 = fila.getString(21);
                        String f22 = fila.getString(22);
                        String f23 = fila.getString(23);
                        //String f24 = fila.getString(24);
                        String f25 = fila.getString(25);

                        String f29 = fila.getString(29);
                        //String f30 = fila.getString(30);
                        //String f31 = fila.getString(31);
                        String f32 = fila.getString(32);

                        String f33 = fila.getString(33);
                        String f34 = fila.getString(34);
                        String f35 = fila.getString(35);
                        String noti="si";
                        try{
                            String f36 = fila.getString(36);
                            notificacion=Boolean.parseBoolean(f36);
                        }catch (Exception e){
                            notificacion=false;
                        }

                        if(notificacion){
                            noti="si";
                        }else{
                            noti="no";
                        }



                        //Log.i("NUMERO BOLETA REENVIO",f18+"");

                        //Log.i("Usuario",f5+"");
                        /*Log.i("Tipo_Iden","");
                        Log.i("Identificacion",f3+"");
                        Log.i("Placa",f4+"");
                        Log.i("AgenteTransito",f5+"");
                        Log.i("Articulo",f6+"");
                        Log.i("Literal",f7+"");
                        Log.i("Fecha",f8+"");
                        Log.i("LugarInfraccion",f9+"");
                        Log.i("Zona",f10+"");
                        Log.i("Provincia",f11+"");
                        Log.i("Localidad",f12+"");
                        Log.i("Distrito","");
                        Log.i("Circuito","");
                        Log.i("SubCircuito","");
                        Log.i("Institucion",f16+"");
                        Log.i("Observacion",f17+"");
                        Log.i("Boleta",f18+"");
                        Log.i("TipoLicencia",f19+"");
                        Log.i("NumCitacion","");
                        Log.i("Descripcion",f21+"");
                        Log.i("Latitud",f22+"");
                        Log.i("Longitud",f23+"");
                        //Log.i("Imagen",f24+"");
                        //Log.i("Imagen2",f25+"");

                        Log.i("Valor",f29+"");
                        Log.i("transmision",f30+"");
                        Log.i("Precision",f31+"");
                        Log.i("Nombre_licencia",f32+"");
                        Log.i("descripcion_p",f33+"");
                        Log.i("descripcion_p2",f34+"");
                        Log.i("descripcionH",f35+"");*/

                        txtBoleta.setText("Número de citación: " + f18 +
                                "\nLicencia/Placa: " + f3 +" " + f4+
                                "\nNombre: " + f32+
                                "\nFecha/Hora: " + f8+
                                "\nLugar: " + f9+ " ("+f22+","+ f23+")" +
                                "\nConcepto: "+ f34 +
                                "\nValor: " + f29+
                                "\nAgente: " + f1 +
                                "\nCódigo: " + f5 +
                                "\nCantón: " + f12 +
                                "\nNotificación: " + noti);
                        //Toast.makeText(context, "No: " + numBoleta[i], Toast.LENGTH_SHORT).show();
                        try{
                            if(f3.length()==10){
                                isLicencia=true;
                            }else{
                                isLicencia=false;
                            }
                        }catch (Exception e){
                            isLicencia=false;
                        }

                        num_boleta=f18;

                        if (isLicencia==true) {
                            nombreUsuario=f32;
                            identificacion=f3;
                            placa=f4;
                            nombreUsuarioImp = "A: "+nombreUsuario;
                            licenciaPlacaImp = "1.IDENTIFICACIÓN:"+identificacion;
                        }else{
                            placa=f4;
                            nombreUsuarioImp = "";
                            licenciaPlacaImp = "1.VEHÍCULO PLACA:"+ placa;
                        }

                        fecha=f8;
                        direccion=f9;
                        auxLat=f22;
                        auxLon=f23;
                        descripcion=f34;
                        descripcion_p=f33;
                        descripcion_p2=f21;
                        descripcionH=f35;
                        valor=f29;
                        cod_agente=f5;
                        nombre_agente=f1;
                        canton=f12;
                        valida_ctacion=true;
                        fila.moveToNext();
                    }
                } else {
                    //Toast.makeText(context, "No existe ninguna citacion", Toast.LENGTH_SHORT).show();
                }
            }
        }

        Utils.bd_citacionE.close();
        return valida_ctacion;
    }

    public String consultarAutoIncrement() {
        String n="000000";
        DBParqueoLite admin = new DBParqueoLite(MainActivity.this, "administracion", null, 2);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select max(numBoleta) from citacion_no_enviadas", null);
        if (fila.moveToFirst()) {
            n = fila.getString(0);

            try {
                int t = n.length();
                n = n.substring(t - 6, t);
                Log.i("SERIAL", n);
            }catch (Exception e){
                n="000000";
                Log.i("SERIAL E", n);
            }
        }
        bd.close();
        return n;
    }

    public static void alertDialog(final String title, final String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        if(alert!=null) {
            try {
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_button));
                pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
            }catch (Exception e){

            }
        }
    }

    public void consultarPlaca(String pla){
        String ws_consulta_vehiculo = "WS_ConsultaVehiculoMP2propietario";
        JSONObject jsonRequestVehiculo = null;
        JSONObject jsonConsulta = null;
        banderamsjplaca=false;
        notificacion=false;

        if (session.getToken() != null && session.getUuid() != null){
            try {
                jsonRequestVehiculo = new JSONObject();
                jsonRequestVehiculo.put("Placa", pla);
                jsonRequestVehiculo.put("Usuario", "Consulta");

                jsonConsulta = new JSONObject().accumulate("tipo","ws").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid()).accumulate("ws", ws_consulta_vehiculo).
                        accumulate("parametros", jsonRequestVehiculo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(webSocketConnection!=null) {
                if (webSocketConnection.isConnected() && banderaCerrarSesion == false) {
                    webSocketConnection.sendMessage(jsonConsulta.toString());
                    Log.i("Consulta Placa", jsonConsulta.toString());

                    if (articulo != null && numeral != null) {
                        if (articulo.length() > 0 && numeral.length() > 0) {
                            controlarSiNotificacion(articulo, numeral);
                        }
                    }
                } else {
                    banderamsjplaca = true;
                }
            }else {
                banderamsjplaca = true;
            }
        }
    }

    public void consultarLicencia(String lic){
        String ws_consulta_vehiculo = "WS_SOLICITUD_LICENCIAMP2";
        JSONObject jsonRequestVehiculo = null;
        JSONObject jsonConsulta = null;
        banderamsjplaca=false;
        notificacion=false;

        if (session.getToken() != null && session.getUuid() != null){

            try {
                jsonRequestVehiculo = new JSONObject();
                jsonRequestVehiculo.put("Licencia", lic);
                jsonRequestVehiculo.put("Usuario", "Consulta");

                jsonConsulta = new JSONObject().accumulate("tipo","ws").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid()).accumulate("ws", ws_consulta_vehiculo).
                        accumulate("parametros", jsonRequestVehiculo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(webSocketConnection!=null) {
                if (webSocketConnection.isConnected() && banderaCerrarSesion == false) {
                    webSocketConnection.sendMessage(jsonConsulta.toString());
                    Log.i("Consulta Licencia", jsonConsulta.toString());
                    if (articulo != null && numeral != null) {
                        if (articulo.length() > 0 && numeral.length() > 0) {
                            controlarSiNotificacion(articulo, numeral);
                        }
                    }
                } else {
                    banderamsjplaca = true;
                }
            }else {
                banderamsjplaca = true;
            }
        }
    }

    private static Vibrator vibration() {
        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 0, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        v.vibrate(pattern, -1);
        // v.vibrate(5000);
        return v;

    }


    static int conteoMensaje=0;
    public static void mensajeRecibido(String message) {

        boolean valida_estado=false;
        JSONObject messageRecive, licencia_vehiculo;
        String decription, etatus, version_nuevaAPK, version_actualAPK, nombreAPK, tipo;

        try {
            messageRecive = new JSONObject(message);
            //status 0 --> OK
            etatus = (messageRecive.has("error"))?messageRecive.getJSONObject("error").getString("status"):"ninguno";
            decription = (messageRecive.has("ws"))?messageRecive.getString("ws"):"ninguno";
            tipo=(messageRecive.has("tipo"))?messageRecive.getString("tipo"):"ninguno";

            if(tipo.compareToIgnoreCase("Mensaje")==0 || tipo.compareToIgnoreCase("Notificacion")==0){
                vibration();
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if(defaultSoundUri!=null) {
                    try {
                        final MediaPlayer mp = MediaPlayer.create(context, defaultSoundUri);
                        if (mp != null) {
                            mp.start();
                        }
                    }catch (Exception e){

                    }
                }

                String asunto,mensaje,de_usuario;
                asunto=(messageRecive.has("parametros"))?messageRecive.getJSONObject("parametros").getString("Asunto"):"ninguno";
                mensaje=(messageRecive.has("parametros"))?messageRecive.getJSONObject("parametros").getString("Mensaje"):"ninguno";
                de_usuario=(messageRecive.has("parametros"))?messageRecive.getJSONObject("parametros").getString("usuario"):"ninguno";

                mensaje="Asunto: "+asunto+"\nMensaje: "+mensaje+"\nDe: "+de_usuario;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(res.getString(R.string.str_cuartel));
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_button));
                pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
            }

            if(tipo.compareToIgnoreCase("controlarSiNotificacionRespuesta")==0){
                    Log.i("NOTIFICACION 1", etatus+" ____VALOR SERVER");
                    if(etatus.compareToIgnoreCase("0")==0) {
                        notificacion = messageRecive.getBoolean("notificacion");
                        //Log.i("NOTIFICACION", notificacion+" ____VALOR SERVER");
                        //if(notificacion==false){
                            //notificacion=true;
                        //}

                        if (notificacion == true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(res.getString(R.string.app_name));
                            builder.setMessage("Infracción por primera vez, se emitirá una notificación");
                            builder.setCancelable(false);
                            builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_button));
                            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                        }
                    }
            }

            switch (decription) {
                case "WS_SOLICITUD_LICENCIAMP2":
                    if(etatus.compareToIgnoreCase("0")==0) {
                        if(handler!=null) {
                            handler.removeCallbacks(myRunnable);
                        }
                        licencia_vehiculo = messageRecive.getJSONObject("respuesta").getJSONObject("Licencia");
                        String nombres_ws = (licencia_vehiculo.has("nombres"))?licencia_vehiculo.get("nombres").toString():"";

                        tipo_licencia= (licencia_vehiculo.has("tipo_Licen"))?licencia_vehiculo.get("tipo_Licen").toString():"";
                        nombreUsuario=nombres_ws;
                        cargarDatosLicencia(licencia_vehiculo.toString());

                    }else{
                        alertDialog(res.getString(R.string.str_alerta),res.getString(R.string.str_no_licencia));

                    }
                    //procesos(false, res.getString(R.string.str_finalizando), res.getString(R.string.str_esperar));
                    if(pk_ConsultaLP!=null) {
                        pk_ConsultaLP.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                    break;

                case "WS_ConsultaVehiculoMP2propietario":
                    if(etatus.compareToIgnoreCase("0")==0) {
                        if(handler!=null) {
                            handler.removeCallbacks(myRunnable);
                        }
                        //licencia_vehiculo=null;
                        licencia_vehiculo = messageRecive.getJSONObject("respuesta").getJSONObject("Vehiculo");
                        String nombre_ws = (licencia_vehiculo.has("nombre"))?licencia_vehiculo.get("nombre").toString():"";
                        String apellido1_ws = (licencia_vehiculo.has("apellido1"))?licencia_vehiculo.get("apellido1").toString():"";
                        String apellido2_ws = (licencia_vehiculo.has("apellido2"))?licencia_vehiculo.get("apellido2").toString():"";

                        nombreUsuario= nombre_ws+" "+apellido1_ws+" "+apellido2_ws;

                        cargarDatosVehiculo(licencia_vehiculo.toString());
                    }else{
                        alertDialog(res.getString(R.string.str_alerta),res.getString(R.string.str_no_placa));
                    }
                    if(pk_ConsultaLP!=null) {
                        pk_ConsultaLP.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                    break;

                case "WS_ingresoCitaciones":
                    if(etatus.compareToIgnoreCase("0")==0) {
                        String time_stamp = messageRecive.getString("timestamp");

                        if (responseOK == false) {
                            if(time_stamp.compareToIgnoreCase(ts)==0) {
                                estado = "enviada";
                                validaConfirmacion=true;
                                modificarEstadoTimestamp(time_stamp, estado);
                                mostrarContadorCitacionesNoEnviadas();
                            }else{
                                estado = "no enviada";
                            }

                        }

                        if (responseOK == true) {
                            if(time_stamp.compareToIgnoreCase(ts)==0) {
                                estado = "enviada";
                                modificarEstado(estado);
                                limpiarMainActivity();
                            }
                        }
                    }
                    break;

                default:
                    break;
            }

            switch (etatus) {
                case "10":
                    alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_10));
                    valida_estado=true;
                    break;
                case "20":
                    alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_20));
                    valida_estado=true;
                    break;
                case "30":
                    alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_30));
                    if(pk_ConsultaLP!=null) {
                        pk_ConsultaLP.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                    valida_estado=true;
                    break;
                case "31":
                    alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_31));
                    if(pk_ConsultaLP!=null) {
                        pk_ConsultaLP.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                    valida_estado=true;
                    break;
                case "43":
                    if(conteoMensaje==0) {
                        session.CloseSessionManager();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(res.getString(R.string.str_sistema));
                        builder.setMessage(res.getString(R.string.str_status_43));
                        builder.setCancelable(false);
                        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onlyCloseSessionUser();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
                        conteoMensaje=1;
                    }
                    valida_estado=true;
                    break;
                case "44":
                    alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_44));
                    valida_estado=true;
                    break;
                case "45":
                    //alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_45));
                    valida_estado=true;
                    break;
                //case "46":
                    //nombre Contraseña falso
                    //break;
                case "47":
                    if(conteoMensaje==0) {
                        session.CloseSessionManager();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(res.getString(R.string.str_sistema));
                        builder.setMessage(res.getString(R.string.str_status_47));
                        builder.setCancelable(false);
                        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onlyCloseSessionUser();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
                        conteoMensaje=1;
                    }
                    valida_estado=true;
                    break;
                case "48":
                    if(conteoMensaje==0) {
                        session.CloseSessionManager();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(res.getString(R.string.str_sistema));
                        builder.setMessage(res.getString(R.string.str_status_48));
                        builder.setCancelable(false);
                        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onlyCloseSessionUser();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
                        conteoMensaje=1;
                    }
                    valida_estado=true;
                    break;
                case "50":
                    alertDialog(res.getString(R.string.str_sistema),res.getString(R.string.str_status_50));
                    valida_estado=true;
                    break;
                //case "60":
                    //datos no encontrado (por ejemplo el webservice de consultar placas no encuentra nada para esta placa)
                    //break;
                case "70":
                    version_nuevaAPK =(messageRecive.getJSONObject("error").has("versionAPK"))?messageRecive.getJSONObject("error").getString("versionAPK"):Utils.versionActualAPK(res);

                    nombreAPK=(messageRecive.getJSONObject("error").has("nombreAPK"))?messageRecive.getJSONObject("error").getString("nombreAPK"):"ATM Update.apk";

                    version_actualAPK=Utils.versionActualAPK(res);
                    if(version_actualAPK.compareToIgnoreCase(version_nuevaAPK)!=0) {
                        Version.setVersionNuevaAPK(version_nuevaAPK);
                        Version.setNombreNuevaAPK(nombreAPK);

                        String actualizarTimer =(messageRecive.getJSONObject("error").has("actualizarTimer"))?messageRecive.getJSONObject("error").getString("actualizarTimer"):"si";

                        //actualizaTimer
                        // valor=si :cuando se envia el mensaje binario de la actualizacion de la app;
                        // valor=no :cuando se envia solo el mensaje para reiniciar la sesion del usuario;
                        if(actualizarTimer.compareToIgnoreCase("no")==0) {
                            //Log.i("REINICIO","MENSAJE 70");
                            reinicioSesionUpdate();
                        }
                    }
                    valida_estado=true;
                    break;
                default:
                    break;
            }
            if(valida_estado==true){
                if(pk_loading!=null)
                    pk_loading.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void modificarEstadoTimestamp(String timestamp, String est){
        DBParqueoLite admin = new DBParqueoLite(context, "administracion", null, 2);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("estado", est);
        bd.update("citacion_no_enviadas", registro, "timestamp=" + timestamp, null);
        bd.close();
    }

    private static void modificarEstado(String estado){
        admin = new DBParqueoLite(context, "administracion", null, 2);
        bd_citacion = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("estado", estado);

        bd_citacion.update("citacion_no_enviadas", registro, "id=" + id_citacionDB, null);
        bd_citacion.close();

        consultarCitacionesNoenviadas();
        if(responseOK==true) {
            reenviarCitacionesNoenviadas();
        }else{
            pk_loading_rc.dismiss();
            //txtViewCount.setText(0);
            txtViewCount.setVisibility(View.GONE);
            txtViewCount.setVisibility(View.INVISIBLE);
        }
    }

    private static void actulizarEstadoTime(String estado, String boleta){
        admin = new DBParqueoLite(context, "administracion", null, 2);
        bd_citacion = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("estado", estado);

        bd_citacion.update("citacion_no_enviadas", registro, "numBoleta=" + boleta, null);
        bd_citacion.close();
    }



    private static int totalCitacionesNoenviadas() {
        DBParqueoLite admin = new DBParqueoLite(context, "administracion", null, 2);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select count(*)  from citacion_no_enviadas where estado=" + "'" + "no enviada"+"'", null);
        int tam=0;
        if (fila.moveToFirst()) {
            tam = fila.getInt(0);
        }else{
            tam=0;
        }
        bd.close();
        return tam;
    }

    /*public void consultarCitacionesOffline() {
        DBParqueoLite admin = new DBParqueoLite(MainActivity.this, "administracion", null, 2);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select *  from citacion_no_enviadas where estado="+"'"+"no enviada"+"' LIMIT 15", null);
        int tam=fila.getCount();
        if (fila!=null) {
            if (fila.moveToFirst()) {
                for (int i = 0; i < tam; i++) {
                    String f0 = fila.getString(0);
                    String f3 = fila.getString(3);
                    String f4 = fila.getString(4);
                    String f26 = fila.getString(26);
                    String f27 = fila.getString(27);

                    Toast.makeText(MainActivity.this, "No: " + f0 + ", est: " + f26 + ", lic: " + f3+", placa: " + f4, Toast.LENGTH_SHORT).show();
                    fila.moveToNext();
                }

            } else {
                Toast.makeText(MainActivity.this, "No existe ninguna citacion", Toast.LENGTH_SHORT).show();
            }
        }
        bd.close();
    }*/

    // Consultar Citaciones no enviadas en base de datos SQLite
    public static void consultarCitacionesNoenviadas() {
        DBParqueoLite admin = new DBParqueoLite(context, "administracion", null, 2);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select *  from citacion_no_enviadas where estado="+"'"+"no enviada"+"' LIMIT 15", null);
        if (fila.moveToFirst()) {
            responseOK = true;
        }else{

            responseOK = false;
        }
        bd.close();
    }

    // Reenviar Citaciones no enviadas en base de datos SQLite
    public static void reenviarCitacionesNoenviadas() {
        limpiarMainActivity();
        admin = new DBParqueoLite(context, "administracion", null, 2);
        bd_citacion = admin.getWritableDatabase();
        Cursor fila = bd_citacion.rawQuery("select *  from citacion_no_enviadas where estado="+"'"+"no enviada"+"' LIMIT 15", null);
        if (fila.moveToFirst()) {
            id_citacionDB = fila.getString(0);
            String f1 = fila.getString(1);
            String f3 = fila.getString(3);
            String f4 = fila.getString(4);
            String f5 = fila.getString(5);
            String f6 = fila.getString(6);
            String f7 = fila.getString(7);
            String f8 = fila.getString(8);
            String f9 = fila.getString(9);
            String f10 = fila.getString(10);
            String f11 = fila.getString(11);
            String f12 = fila.getString(12);
            String f16 = fila.getString(16);
            String f17 = fila.getString(17);

            String f18 = fila.getString(18);
            String f19 = fila.getString(19);
            String f21 = fila.getString(21);
            String f22 = fila.getString(22);
            String f23 = fila.getString(23);
            String f24 = fila.getString(24);
            String f25 = fila.getString(25);

            String f30 = fila.getString(30);
            String f31 = fila.getString(31);

            Log.i("citacion",f3+", "+f4);

            if((f3.compareToIgnoreCase("")!=0) || (f4.compareToIgnoreCase("")!=0)) {
                //Toast toast = Toast.makeText(contextWs, "citacion"+f3+", "+f4, Toast.LENGTH_LONG);
                //toast.show();
                final String f27 = fila.getString(27);
                try{
                    String f36 = fila.getString(36);
                    notificacion=Boolean.parseBoolean(f36);
                }catch (Exception e){
                    notificacion=false;
                }


                citacion = new Citacion(f1, "", f3, f4, f5, f6, f7, f8, f9, f10,
                        f11, f12, "", "", "", f16, f17, f18, f19, "",
                        f21, f22, f23, f24, f25, f30, f31, notificacion);
                //Log.i("Time__Stamp",f27+"");
                reenviarCitacion(f27);
            }else{
                //reenviarCitacionesNoenviadas();
            }
        }
        bd_citacion.close();
    }

    public static void reenviarCitacion(String timestamp){
        if (session.getToken() != null && session.getUuid() != null) {
            jsonCitation = new JSONObject();
            jsonSendCitation = new JSONObject();
            ts=timestamp;
            try {
                jsonCitation.put("Usuario", "Envio");
                jsonCitation.put("Ticket", citacion.getJsonCitacion());
                jsonSendCitation = new JSONObject().
                        accumulate("tipo", "ws").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid()).
                        accumulate("timestamp", timestamp).
                        accumulate("ws", ws_enviar_inccidente).
                        accumulate("parametros", jsonCitation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (webSocketConnection.isConnected() && banderaCerrarSesion==false){
            webSocketConnection.sendMessage(jsonSendCitation.toString());
            Log.d("Mensaje", "enviada");
        }else{
            if(pk_loading_rc!=null) {
                pk_loading_rc.dismiss();
            }
        }
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Log.i("DEVICE","is now connected");
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
               if(nombreImpresora.compareToIgnoreCase("HP-RP") == 0 || nombreImpresora.compareToIgnoreCase("RPP20") == 0) {
                    validaStartHubrox=0;
                }
            }
        }
    };

    // Bluetooth
    private void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            validaImpresora=false;
            String nombre_impresora="";

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
                menuItemSendPrinter.setEnabled(true);
            }else{
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice device : pairedDevices){
                        int tcd = device.getName().toString().length();
                        //Log.i("Name Printer","|"+device.getName().toString()+"|");
                        if(tcd >= 5){
                            nombre_impresora = device.getName().toString().substring(0, 5);
                        }else{
                            nombre_impresora = device.getName().toString().substring(0, tcd);
                        }

                        if(nombre_impresora.compareToIgnoreCase("RW220") == 0) {
                            validaImpresora = true;
                            mmDevice = device;
                            break;
                        }
                        if(nombre_impresora.compareToIgnoreCase("HP-RP") == 0 || nombre_impresora.compareToIgnoreCase("RPP20") == 0) {

                            validaImpresora = true;
                            mmDevice = device;
                            break;
                        }
                        //Log.i("Name Printer","|"+device.getName().toString()+"|");
                        if(nombre_impresora.compareToIgnoreCase("ALPHA") == 0  || nombre_impresora.compareToIgnoreCase("BT-SP") == 0) {
                            validaImpresora = true;
                            mmDevice = device;
                            //Log.i("Name Printer","|"+device.getName().toString()+"|"+device.toString());
                            break;
                        }
                    }
                }
                if (validaImpresora == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(res.getString(R.string.str_alerta));
                    builder.setMessage(res.getString(R.string.str_valida_impresora));
                    builder.setCancelable(false);
                    builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enableTestButton(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.window_background));
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

    //Progress Dialog
    private void enableTestButton(final boolean enabled) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(enabled==false) {
                    pk_loading = ProgressDialog.show(MainActivity.this, res.getString(R.string.str_sistema), res.getString(R.string.str_imprimiendo2), false, false);
                }else{
                    if(pk_loading!=null) {
                        pk_loading.dismiss();
                    }
                }
                menuItemSendPrinter.setEnabled(enabled);
                if(enabled==true){
                    reImprimir();
                }
            }
        });
    }

    //Impresión si es licencia o placa
    public void licencia_placa_imp(){
        if (isLicencia==true) {
            nombreUsuarioImp = "A: "+nombreUsuario;
            licenciaPlacaImp = "1.IDENTIFICACIÓN:"+identificacion;
        }else{
            nombreUsuarioImp = "";
            licenciaPlacaImp = "1.VEHÍCULO PLACA:"+ placa;
        }
    }

    //Impresión Zebra
    public int zebraPrinterVal() {
        int status = -1;
        try {
            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);
            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();
            if (printerStatus.isReadyToPrint) {
                status = 0;
                byte[] configLabel = null;
                if(session.getFedatario().compareToIgnoreCase("true")==0){
                    if ((articulo.compareToIgnoreCase("387") == 0) && (numeral.compareToIgnoreCase("1") == 0)) {
                        //configLabel = getConfigLabelZebraArt387();
                        configLabel = getConfigLabelZebraFedatario();
                    } else {
                        configLabel = getConfigLabelZebraFedatario();
                    }
                }else {
                    if ((articulo.compareToIgnoreCase("387") == 0) && (numeral.compareToIgnoreCase("1") == 0)) {
                        configLabel = getConfigLabelZebraArt387();
                    } else {
                        if(notificacion) {
                            configLabel=getConfigLabelZebraNoti();
                        }else{
                            configLabel = getConfigLabelZebra();
                        }

                    }
                }
                try {
                    connection.write(configLabel);
                    enableTestButton(true);
                }catch (Exception e){
                    //status = 4;
                    Toast.makeText(context, "Revise la impresora Zebra", Toast.LENGTH_LONG).show();
                }
            } else if (printerStatus.isPaused) {
                status = 1;
                System.out.println("Cannot Print because the printer is paused.");
            } else if (printerStatus.isHeadOpen) {
                status = 2;
                System.out.println("Cannot Print because the printer media door is open.");
            } else if (printerStatus.isPaperOut) {
                status = 3;
                System.out.println("Cannot Print because the paper is out.");
            } else{
                status = 4;
                System.out.println("Cannot Print.");
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

    public void cad_cordenadas(){
        auxLat=latitud;
        auxLon=longitud;
        if(latitud.length()>=13) {
            auxLat=latitud.substring(0,13);
        }
        if(longitud.length()>=13) {
            auxLon=longitud.substring(0,13);
        }
    }

    //CPCL Impresora Zebra
    private byte[] getConfigLabelZebra() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p;
        espcp=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>42){
            v_descripcion_p=cad_descripcion_p(v_descripcion_p);
        }
        int pos=220;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(1640+espd+espc)+" 1\r\n"+
            //cpclConfigLabel = "! 0 200 200 "+(50+espc)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + num_boleta + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg+espp)+" "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+

                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 16 "+(acu=acu+espc)+" 5.VALOR DE LA MULTA: $"+valor+ "\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"LOS RECARGOS POR MORA EN EL PAGO DE LA MULTA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SE LOS CALCULARA EN EL  MOMENTO  DEL PAGO DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA PRESTACION, EN LOS TERMINOS  PREVISTOS EN\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  ARTICULO  179  DE  LA  LEY  ORGANICA  DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"TRASPORTE  TERRESTRE, TRANSITO  Y  SEGURIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"VIAL.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"EL   PAGO   DE   LA   MULTA   POR   CONCEPTO\r\n"+
                    "ML 25\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" "+v_descripcion_p.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 0 2 20 "+(acu=acu+espcp)+"POR  EL  VALOR DE $"+valor+", DEBERA EFECTUARLO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DENTRO DE LOS 10 DIAS  HABILES POSTERIORES A\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA FECHA DE  ESTA NOTIFICACION.  VENCIDO ESE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PLAZO  DEBERA  CANCELAR UNA  MULTA ADICIONAL\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL  DOS  POR  CIENTO  (2%)  SOBRE  EL VALOR\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PRINCIPAL  POR CADA MES O FRACCION DE MES DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"MORA  HASTA UN 100% DEL VALOR DE LA MULTA, Y\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SI FUERE  NECESARIO  SE  RECAUDARA  LA DEUDA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PENDIENTE  MEDIANTE  PROCEDIMIENTO  COACTIVO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  INFRACTOR  DE  SER  EL CASO PODRA DENTRO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL PLAZO  DE 3 DIAS HABILES  IMPUGNAR  ESTA \r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"CITACION  DE  TRANSITO   ANTE  LA  AUTORIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"COMPETENTE.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else{
            cpclConfigLabel = "! 0 200 200 "+(1680+espd+espc)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + num_boleta + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 5 0 16 "+(acu=acu+espg+espp)+" "+nombreUsuarioImp.toUpperCase()+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg)+" "+ licenciaPlacaImp + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 16 "+(acu=acu+espc)+" 5.VALOR DE LA MULTA: $"+valor+ "\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"LOS RECARGOS POR MORA EN EL PAGO DE LA MULTA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SE LOS CALCULARA EN EL  MOMENTO  DEL PAGO DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA PRESTACION, EN LOS TERMINOS  PREVISTOS EN\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  ARTICULO  179  DE  LA  LEY  ORGANICA  DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"TRASPORTE  TERRESTRE, TRANSITO  Y  SEGURIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"VIAL.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"EL   PAGO   DE   LA   MULTA   POR   CONCEPTO\r\n"+
                    "ML 25\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" "+v_descripcion_p.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 0 2 20 "+(acu=acu+espcp)+"POR  EL  VALOR DE $"+valor+", DEBERA EFECTUARLO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DENTRO DE LOS 10 DIAS  HABILES POSTERIORES A\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA FECHA DE  ESTA NOTIFICACION.  VENCIDO ESE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PLAZO  DEBERA  CANCELAR UNA  MULTA ADICIONAL\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL  DOS  POR  CIENTO  (2%)  SOBRE  EL VALOR\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PRINCIPAL  POR CADA MES O FRACCION DE MES DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"MORA  HASTA UN 100% DEL VALOR DE LA MULTA, Y\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SI FUERE  NECESARIO  SE  RECAUDARA  LA DEUDA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PENDIENTE  MEDIANTE  PROCEDIMIENTO  COACTIVO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  INFRACTOR  DE  SER  EL CASO PODRA DENTRO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL PLAZO  DE 3 DIAS HABILES  IMPUGNAR  ESTA \r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"CITACION  DE  TRANSITO   ANTE  LA  AUTORIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"COMPETENTE.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return cpcl;
    }

    private byte[] getConfigLabelZebraNoti() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p;
        espcp=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>42){
            v_descripcion_p=cad_descripcion_p(v_descripcion_p);
        }
        int pos=220;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(1130+espd+espc)+" 1\r\n"+
                    //cpclConfigLabel = "! 0 200 200 "+(50+espc)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + num_boleta + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 2 70 "+(acu=pos)+" Notificación"+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg+espp+espp)+" 1.Nombre: "+"\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+nombreUsuario.toUpperCase()+", \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" identificación "+identificacion+" \r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.Vehiculo placa: "+placa+" \r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.Fecha y hora de infraccion: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.Lugar de infraaccion:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 5.Concepto:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+
                    //"T 7 0 16 "+(acu=acu+espc)+" 5.VALOR DE LA MULTA: $"+valor+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espc)+" 6.Mensaje institucional\r\n"+
                    //"T 0 2 20 "+(acu=acu+espg)+"LOS RECARGOS POR MORA EN EL PAGO DE LA MULTA\r\n"+
                    //"T 0 2 20 "+(acu=acu+esppp)+"SE LOS CALCULARÁ EN EL  MOMENTO  DEL PAGO DE\r\n"+
                    //"T 0 2 20 "+(acu=acu+esppp)+"LA PRESTACIÓN, EN LOS TÉRMINOS  PREVISTOS EN\r\n"+
                    //"T 0 2 20 "+(acu=acu+esppp)+"EL  ARTÍCULO  179  DE  LA  LEY  ORGÁNICA  DE\r\n"+
                    //"T 0 2 20 "+(acu=acu+esppp)+"TRASPORTE  TERRESTRE, TRÁNSITO  Y  SEGURIDAD\r\n"+
                    //"T 0 2 20 "+(acu=acu+esppp)+"VIAL.\r\n"+

                    "T 7 0 16 "+(acu=acu+espg)+" 7.Agente civil de transito: \r\n"+
                    "T 7 0 38 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 38 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 38 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+
                    "T 7 0 14 "+(acu=acu+espp+espp)+" www.atm.gob.ec"+"\r\n"+
                    //"IL 20 "+(acu)+" 200 "+(acu)+" 22\r\n"+

                    "PRINT\r\n";
            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return cpcl;
    }

    //CPCL Impresora Zebra
    private byte[] getConfigLabelZebraArt387() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p;
        espcp=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>42){
            v_descripcion_p=cad_descripcion_p(v_descripcion_p);
        }
        int pos=220;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(1130+espd+espc)+" 1\r\n"+
                    //cpclConfigLabel = "! 0 200 200 "+(50+espc)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + num_boleta + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg+espp)+" "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+

                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 0 2 20 "+(acu=acu+espc+espg)+" RESPONDER PARTE DE TRANSITO. ACERCARSE A LA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" UNIDAD JUDICIAL COMPETENTE. VIGENCIA DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" DOCUMENTO 10 DIAS.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else{
            cpclConfigLabel = "! 0 200 200 "+(1160+espd+espc)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + num_boleta + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 5 0 16 "+(acu=acu+espg+espp)+" "+nombreUsuarioImp.toUpperCase()+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg)+" "+ licenciaPlacaImp + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 0 2 20 "+(acu=acu+espc+espg)+" RESPONDER PARTE DE TRANSITO. ACERCARSE A LA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" UNIDAD JUDICIAL COMPETENTE. VIGENCIA DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" DOCUMENTO 10 DIAS.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return cpcl;
    }

    //CPCL Impresora Zebra
    private byte[] getConfigLabelZebraFedatario() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p;
        espcf=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>28){
            v_descripcion_p=cad_descripcion_p_fedatario(v_descripcion_p);
        }

        //Leyenda Impresion
        String leyenda_impresion=observacion_impresion_ticket;
        Log.i("LEYENDA", leyenda_impresion);
        espcfLeyImp=25;
        int tleyimp=v_descripcion_p.length();
        Log.i("tleyimp", tleyimp+" __");
        if(tleyimp>31){
            leyenda_impresion=cad_descripcion_p_fedatarioLeyImp(leyenda_impresion);
            Log.i("LEYENDA", leyenda_impresion);
        }


        int pos=220;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(1150+espd+espc+espcf+espcfLeyImp)+" 1\r\n"+
                    //cpclConfigLabel = "! 0 200 200 "+(50+espc)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + "PARQUEO POSITIVO" + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 1 16 "+(acu=pos)+" NOTIFICACION PERIODO"+"\r\n"+

                    "T 5 1 16 "+(acu=acu+espp+espp)+ "SOCIALIZACION\r\n"+

                    "T 5 1 16 "+(acu=acu+espp+espp+espp)+ "NO ES MULTA\r\n"+

                    "T 5 1 16 "+(acu=acu+espg+espp)+" No."+num_boleta+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp+espp+espp)+" a) "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" b) FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" c) LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" d) PARQUÍMETRO NÚMERO: "+observacion+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" e) CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion_p+"\r\n"+
                    "ENDML\r\n"+


                    "T 7 0 16 "+(acu=acu+espcf)+" f) BASE LEGAL:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+

                    //"T 7 0 16 "+(acu=acu+espc)+" g) VALOR DE LA MULTA: $"+valor+ "\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 20 "+(acu=acu+espg+espc)+" "+leyenda_impresion+"\r\n"+
                    "ENDML\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg+espg+espcfLeyImp)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 40 "+(acu=acu+esppp)+" FIRMA CAPTURADOR DE EVENTO\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+



                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else{
            cpclConfigLabel = "! 0 200 200 "+(1160+espd+espc+espcf+espcfLeyImp)+" 1\r\n"+
                    "PW 400\r\n" +
                    "TONE 0\r\n" +
                    "SPEED 3\r\n" +
                    "ON-FEED IGNORE\r\n" +
                    "NO-PACE\r\n" +
                    "BAR-SENSE\r\n" +
                    "" + "PARQUEO POSTITIVO" + "\r\n" +
                    "ENDPDF\r\n" +
                    "PCX 10 10 !<ATM.PCX\r\n" +

                    "T 5 1 16 "+(acu=pos)+" NOTIFICACION PERIODO"+"\r\n"+

                    "T 5 1 16 "+(acu=acu+espp+espp)+ "SOCIALIZACION\r\n"+

                    "T 5 1 16 "+(acu=acu+espp+espp+espp)+ "NO ES MULTA\r\n"+

                    "T 5 2 16 "+(acu=pos+espp+espp+espg+espg+espg+espp)+" No."+num_boleta+"\r\n"+

                    "T 5 0 16 "+(acu=acu+espg+esppp)+" "+nombreUsuarioImp.toUpperCase()+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp+espp)+" a) "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" b) FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" c) LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" d) PARQUÍMETRO NÚMERO: "+observacion+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" e) CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion_p+"\r\n"+
                    "ENDML\r\n"+


                    "T 7 0 16 "+(acu=acu+espcf)+" f) BASE LEGAL:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+

                    //"T 7 0 16 "+(acu=acu+espc)+" g) VALOR DE LA MULTA: $"+valor+ "\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 20 "+(acu=acu+espg+espc)+" "+leyenda_impresion+"\r\n"+
                    "ENDML\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg+espg+espcfLeyImp)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 40 "+(acu=acu+esppp)+" FIRMA CAPTURADOR DE EVENTO\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return cpcl;
    }

    //Impresión Hubrox
    private static int validaStartHubrox=0;
    private boolean conn = true;
    private void imageHubrox() {
        if (mmDevice != null) {
            try {
                if(validaStartHubrox==0) {
                    hsBluetoothPrintDriver.start();
                    hsBluetoothPrintDriver.connect(mmDevice);
                    Sleeper.sleep(2000);
                    hsBluetoothPrintDriver.Begin();
                    Sleeper.sleep(3000);
                    conn = hsBluetoothPrintDriver.IsNoConnection();
                }
                if(conn==false) {
                    validaStartHubrox=1;
                    hsBluetoothPrintDriver.SetDefaultSetting();
                    hsBluetoothPrintDriver.SetAlignMode((byte) 0x00);
                    hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x00);
                    hsBluetoothPrintDriver.SetUnderline((byte) 0x00);
                    hsBluetoothPrintDriver.SelChineseCodepage();
                    hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x00);
                    // if (hsBluetoothPrintDriver.printImage(mBitmap, Contants.TYPE_58)) {
                    //}
                    hsBluetoothPrintDriver.printImage(mBitmap, Contants.TYPE_58);
                    Sleeper.sleep(2000);
                    imprime_hubros=true;
                }else{
                    enableTestButton(true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(res.getString(R.string.str_alerta));
                    builder.setMessage(res.getString(R.string.str_encender_impresora));
                    builder.setCancelable(false);
                    builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enableTestButton(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                    //Log.i("HUBROS CONN, VALIDA",conn+","+validaStartHubrox);
                    //Log.i("IMPIENDO ERROR3","HUBROX1");
                }
            } catch (Exception e) {
                enableTestButton(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(res.getString(R.string.str_alerta));
                builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                builder.setCancelable(false);
                builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                e.printStackTrace();
                //Log.i("IMPIENDO ERROR2","HUBROX");
            }
        }else{
            enableTestButton(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(res.getString(R.string.str_alerta));
            builder.setMessage(res.getString(R.string.str_impresora_sin_conexion));
            builder.setCancelable(false);
            builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    enableTestButton(true);
                }
            });
            //Log.i("IMPIENDO ERROR3","HUBROX");
            AlertDialog alert = builder.create();
            alert.show();
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
        }
    }

    private void textHubrox() {
        String tmcde = descripcionH;
        byte[] descripcionb = tmcde.getBytes();

        String tmcdep = descripcion_p2;
        byte[] descripcionbp = tmcdep.getBytes();

        String tmpd = direccion;
        byte[] direccionb = tmpd.getBytes();

        Sleeper.sleep(1000);
        if (isLicencia==true) {
            nombreUsuarioImp = "A: "+nombreUsuario;
            licenciaPlacaImp = "1. IDENTIFICACION:"+identificacion;
        }else{
            nombreUsuarioImp = "";
            licenciaPlacaImp = "1. VEHICULO PLACA:"+ placa;
        }

        if (mmDevice != null) {
            hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x01);
            hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x01);
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
            hsBluetoothPrintDriver.BT_Write("   No. " + num_boleta);
            hsBluetoothPrintDriver.CR();


            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            if(nombreUsuarioImp.compareToIgnoreCase("")!=0) {
                hsBluetoothPrintDriver.BT_Write(nombreUsuarioImp);
                hsBluetoothPrintDriver.CR();
                hsBluetoothPrintDriver.CR();
            }

            hsBluetoothPrintDriver.BT_Write(licenciaPlacaImp);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("2. FECHA:" + fecha);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("3. LUGAR:");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(direccionb);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("(" + auxLat + "," + auxLon + ")");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("4. CONCEPTO:");
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(descripcionb);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("5. VALOR DE MULTA: $" + valor);
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("LOS RECARGOS POR MORA  EN  EL PAGO DE LA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("MULTA SE LOS CALCULARA EN EL MOMENTO DEL");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("PAGO DE LA PRESTACION, EN  LOS  TERMINOS");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("PREVISTOS  EN  EL ARTICULO 179 DE LA LEY");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("ORGANICA DE TRASPORTE TERRESTRE, TRANSITO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("Y SEGURIDAD VIAL.");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("AGENTE CIVIL DE TRANSITO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("NOMBRE:" + nombre_agente);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("CODIGO: UNFORMADO NO." + cod_agente);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("CANTON:" + canton);
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("--------------------------------------");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("FIRMA DEL AGENTE");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.SetAlignMode((byte) 0);

            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("AV  CARLOS  JULIO  AROSEMENA, GUAYAQUIL,");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("GUAYAS.");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("LOCAL 85 TEL: (593)(4)3713889 GUAYAQUIL,");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("ECUADOR.");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.BT_Write("EL   PAGO  DE  LA  MULTA  POR   CONCEPTO");
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(descripcionbp);
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.BT_Write("POR EL VALOR DE $"+valor+" DEBERA EFECTUARLO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("DENTRO DE LOS 10 DIAS HABILES POSTERIORES");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("A LA FECHA DE ESTA NOTIFICACION. VENCIDO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("ESE PLAZO  DEBERA  CANCELAR  UNA  MULTA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("ADICIONAL DEL DOS POR CIENTO (2%) SOBRE EL");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("VALOR PRINCIPAL POR CADA MES O FRACCION DE");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("MES DE MORA HASTA UN 100% DEL VALOR DE LA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("MULTA, Y SI FUERE NECESARIO SE RECAUDARA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("LA DEUDA PENDIENTE MEDIANTE PROCEDIMIENTO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("COACTIVO EL INFRACTOR DE SER EL CASO PODRA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("DENTRO  DEL  PLAZO  DE  3  DIAS  HABILES");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("IMPUGNAR ESTA  CITACION  DE TRANSITO ANTE");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("LA AUTORIDAD COMPETENTE.");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x01);
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
            hsBluetoothPrintDriver.BT_Write("CONSULTA DE MULTA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.BT_Write("WWW.ATM.GOB.EC");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            enableTestButton(true);
            imprime_hubros=false;

            //Sleeper.sleep(2000);
            //hsBluetoothPrintDriver.stop();
        }
    }

    private void textHubroxNoti() {
        //String tmcde = descripcionH;
        //byte[] descripcionb = tmcde.getBytes();

        //String tmpd = direccion;
        //byte[] direccionb = tmpd.getBytes();

        //Sleeper.sleep(1800);

        if (mmDevice != null) {
            hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x01);
            hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x01);
            hsBluetoothPrintDriver.setCharsetName("ISO-8859-1");
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
            hsBluetoothPrintDriver.BT_Write("     NOTIFICACION");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();


            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("1.NOMBRE: ");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.BT_Write(" "+nombreUsuario.toUpperCase());
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write(" IDENTIFICACION " +identificacion);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("2.VEHICULO PLACA: " + placa);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("3.FECHA Y HORA DE INFRACCION: ");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.BT_Write(" "+fecha);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("4.LUGAR DE INFRACCION: ");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write(" "+direccion);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write(" (" + auxLat + "," + auxLon + ")");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("5.CONCEPTO: ");
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(descripcionH);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("6.MENSAJE INSTITUCIONAL");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("AGENTE CIVIL DE TRANSITO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("NOMBRE: " + nombre_agente);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("CODIGO: UNFORMADO NO." + cod_agente);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("CANTON:" + canton);
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("--------------------------------------");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("FIRMA DEL AGENTE");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.SetAlignMode((byte) 0);

            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("AV  CARLOS  JULIO  AROSEMENA, GUAYAQUIL,");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("GUAYAS.");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("LOCAL 85 TEL: (593)(4)3713889 GUAYAQUIL,");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("ECUADOR.");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.BT_Write("WWW.ATM.GOB.EC");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            enableTestButton(true);
            imprime_hubros=false;

            //Sleeper.sleep(2000);
            //hsBluetoothPrintDriver.stop();
        }
    }

    private void textHubroxArt387() {
        String tmcde = descripcionH;
        byte[] descripcionb = tmcde.getBytes();
        Sleeper.sleep(500);
        String tmpd = direccion;
        byte[] direccionb = tmpd.getBytes();
        if (isLicencia==true) {
            nombreUsuarioImp = "A: "+nombreUsuario;
            licenciaPlacaImp = "1. IDENTIFICACION:"+identificacion;
        }else{
            nombreUsuarioImp = "";
            licenciaPlacaImp = "1. VEHICULO PLACA:"+ placa;
        }

        if (mmDevice != null) {
            hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x01);
            hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x01);
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
            hsBluetoothPrintDriver.BT_Write("   No. " + num_boleta);
            hsBluetoothPrintDriver.CR();


            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            if(nombreUsuarioImp.compareToIgnoreCase("")!=0) {
                hsBluetoothPrintDriver.BT_Write(nombreUsuarioImp);
                hsBluetoothPrintDriver.CR();
                hsBluetoothPrintDriver.CR();
            }

            hsBluetoothPrintDriver.BT_Write(licenciaPlacaImp);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("2. FECHA:" + fecha);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("3. LUGAR:");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(direccionb);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("(" + auxLat + "," + auxLon + ")");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("4. CONCEPTO:");
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(descripcionb);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("RESPONDER PARTE DE TRANSITO. ACERCARSE A ");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("LA UNIDAD JUDICIAL COMPETENTE. VIGENCIA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("DE DOCUMENTO 10 DIAS.");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("AGENTE CIVIL DE TRANSITO");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("NOMBRE:" + nombre_agente);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("CODIGO: UNFORMADO NO." + cod_agente);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("CANTON:" + canton);
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("--------------------------------------");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("FIRMA DEL AGENTE");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.SetAlignMode((byte) 0);

            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write("AV  CARLOS  JULIO  AROSEMENA, GUAYAQUIL,");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("GUAYAS.");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("LOCAL 85 TEL: (593)(4)3713889 GUAYAQUIL,");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.BT_Write("ECUADOR.");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x01);
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
            hsBluetoothPrintDriver.BT_Write("CONSULTA DE MULTA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.BT_Write("WWW.ATM.GOB.EC");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            enableTestButton(true);
            imprime_hubros=false;

            //Sleeper.sleep(2000);
            //hsBluetoothPrintDriver.stop();
            //hsBluetoothPrintDriver.stop();
        }
    }

    private void textHubroxFedatario() {
        String tmcde = descripcionH;
        byte[] descripcionb = tmcde.getBytes();
        Sleeper.sleep(500);

        String tmcdep = descripcion_p2;
        byte[] descripcionbp = tmcdep.getBytes();
        Sleeper.sleep(500);

        String tmpd = direccion;
        byte[] direccionb = tmpd.getBytes();

        byte[] leyenda_ticket=observacion_impresion_ticket.getBytes();
        Sleeper.sleep(500);


        if (isLicencia==true) {
            nombreUsuarioImp = "A: "+nombreUsuario;
            licenciaPlacaImp = "a) IDENTIFICACION:"+identificacion;
        }else{
            nombreUsuarioImp = "";
            licenciaPlacaImp = "a) VEHICULO PLACA:"+ placa;
        }

        if (mmDevice != null) {
            hsBluetoothPrintDriver.SetChineseCharacterMode((byte) 0x01);
            hsBluetoothPrintDriver.SetCharacterPrintMode((byte) 0x01);
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);

            //hsBluetoothPrintDriver.BT_Write("PARQUEO POSITIVO");

            hsBluetoothPrintDriver.BT_Write("NOTIFICACION PERIODO SOCIALIZACION");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.BT_Write("NO ES MULTA");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("No. " + num_boleta);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            if(nombreUsuarioImp.compareToIgnoreCase("")!=0) {
                hsBluetoothPrintDriver.BT_Write(nombreUsuarioImp);
                hsBluetoothPrintDriver.LF();
                hsBluetoothPrintDriver.LF();
            }

            hsBluetoothPrintDriver.BT_Write(licenciaPlacaImp);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();


            hsBluetoothPrintDriver.BT_Write("b) FECHA:" + fecha);
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("c) LUGAR:");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(direccionb);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("(" + auxLat + "," + auxLon + ")");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("d) PARQUIMETRO NUMERO:" + observacion);
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write("e) CONCEPTO:");
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(descripcionbp);
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.BT_Write("f) BASE LEGAL:");
            hsBluetoothPrintDriver.LF();

            hsBluetoothPrintDriver.BT_Write(descripcionb);
            hsBluetoothPrintDriver.CR();

            //hsBluetoothPrintDriver.BT_Write("g) VALOR DE MULTA: $" + valor);
            //hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.SetAlignMode((byte) 0);

            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);

            hsBluetoothPrintDriver.BT_Write(leyenda_ticket);
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("--------------------------------------");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.SetAlignMode((byte) 1);
            hsBluetoothPrintDriver.BT_Write("FIRMA CAPTURADOR DE EVENTO");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();

            hsBluetoothPrintDriver.SetAlignMode((byte) 0);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x01);
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x11);
            hsBluetoothPrintDriver.BT_Write("CONSULTA DE MULTA");
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.SetFontEnlarge((byte) 0x00);
            hsBluetoothPrintDriver.SetBlackReversePrint((byte) 0x00);
            hsBluetoothPrintDriver.SetBold((byte) 0x01);
            hsBluetoothPrintDriver.BT_Write("WWW.ATM.GOB.EC");
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.LF();
            hsBluetoothPrintDriver.CR();
            hsBluetoothPrintDriver.CR();
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
                TscDll.setup(80, 29, 4, 4, 0, 0, 0);
                TscDll.clearbuffer();
                TscDll.sendpicture(80, 23, "/sdcard/ATM/atm_logo_gris.jpg");
                TscDll.printlabel(1, 1);
                TscDll.clearbuffer();
                if(session.getFedatario().compareToIgnoreCase("true")==0){
                    if ((articulo.compareToIgnoreCase("387") == 0) && (numeral.compareToIgnoreCase("1") == 0)) {
                        //TscDll.sendcommand(getConfigLabelTSCArt387());
                        TscDll.sendcommand(getConfigLabelTSCFedatario());
                    } else {
                        TscDll.sendcommand(getConfigLabelTSCFedatario());
                    }
                }else {
                    if ((articulo.compareToIgnoreCase("387") == 0) && (numeral.compareToIgnoreCase("1") == 0)) {
                        TscDll.sendcommand(getConfigLabelTSCArt387());
                        //TscDll.sendcommand(getConfigLabelTSCFedatario());
                    } else {
                        if(notificacion) {
                            TscDll.sendcommand(getConfigLabelTSCNoti());
                        }else{
                            TscDll.sendcommand(getConfigLabelTSC());
                        }

                    }
                }
                TscDll.closeport();
                enableTestButton(true);

            }catch(Exception e){
                TscDll.closeport();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(res.getString(R.string.str_alerta));
                builder.setMessage(res.getString(R.string.str_impresora_no_lista));
                builder.setCancelable(false);
                builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
            }

        }else{
            TscDll.closeport();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(res.getString(R.string.str_alerta));
            builder.setMessage(res.getString(R.string.str_encender_impresora));
            builder.setCancelable(false);
            builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    enableTestButton(true);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
        }
    }
    //CPCL Impresora TCS
    private byte[] getConfigLabelTSC() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p2;
        espcp=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>42){
            v_descripcion_p=cad_descripcion_p(v_descripcion_p);
        }
        int pos=0;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(1450+espd+espc)+" 1\r\n"+
                    "PW 400\r\n"+
                    "TONE 0\r\n"+
                    "SPEED 3\r\n"+
                    "ON-FEED IGNORE\r\n"+
                    "NO-PACE\r\n"+
                    "BAR-SENSE\r\n"+

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg+espp)+" "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 16 "+(acu=acu+espc)+" 5.VALOR DE LA MULTA: $"+valor+ "\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"LOS RECARGOS POR MORA EN EL PAGO DE LA MULTA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SE LOS CALCULARÁ EN EL  MOMENTO  DEL PAGO DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA PRESTACIÓN, EN LOS TÉRMINOS  PREVISTOS EN\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  ARTÍCULO  179  DE  LA  LEY  ORGÁNICA  DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"TRASPORTE  TERRESTRE, TRÁNSITO  Y  SEGURIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"VIAL.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"EL   PAGO   DE   LA   MULTA   POR   CONCEPTO\r\n"+
                    "ML 25\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" "+v_descripcion_p.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 0 2 20 "+(acu=acu+espcp)+"POR  EL  VALOR DE $"+valor+", DEBERÁ EFECTUARLO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DENTRO DE LOS 10 DÍAS  HÁBILES POSTERIORES A\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA FECHA DE  ESTA NOTIFICACIÓN.  VENCIDO ESE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PLAZO  DEBERÁ  CANCELAR UNA  MULTA ADICIONAL\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL  DOS  POR  CIENTO  (2%)  SOBRE  EL VALOR\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PRINCIPAL  POR CADA MES O FRACCIÓN DE MES DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"MORA  HASTA UN 100% DEL VALOR DE LA MULTA, Y\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SI FUERE  NECESARIO  SE  RECAUDARÁ  LA DEUDA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PENDIENTE  MEDIANTE  PROCEDIMIENTO COACTIVO.\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  INFRACTOR  DE  SER  EL CASO PODRÁ DENTRO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL PLAZO  DE 3 DÍAS HÁBILES  IMPUGNAR  ESTA \r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"CITACIÓN  DE  TRÁNSITO   ANTE  LA  AUTORIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"COMPETENTE.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else{
            cpclConfigLabel = "! 0 200 200 "+(1490+espd+espc)+" 1\r\n"+
                    "PW 400\r\n"+
                    "TONE 0\r\n"+
                    "SPEED 3\r\n"+
                    "ON-FEED IGNORE\r\n"+
                    "NO-PACE\r\n"+
                    "BAR-SENSE\r\n"+

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 5 0 16 "+(acu=acu+espg+espp)+" "+nombreUsuarioImp.toUpperCase()+"\r\n"+

                    "T 7 0 16 "+(acu=acu+espg)+" "+ licenciaPlacaImp + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 16 "+(acu=acu+espc)+" 5.VALOR DE LA MULTA: $"+valor+ "\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"LOS RECARGOS POR MORA EN EL PAGO DE LA MULTA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SE LOS CALCULARÁ EN EL  MOMENTO  DEL PAGO DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA PRESTACIÓN, EN LOS TÉRMINOS  PREVISTOS EN\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  ARTÍCULO  179  DE  LA  LEY  ORGÁNICA  DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"TRASPORTE  TERRESTRE, TRÁNSITO  Y  SEGURIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"VIAL.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+
                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"EL   PAGO   DE   LA   MULTA   POR   CONCEPTO\r\n"+
                    "ML 25\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+" "+v_descripcion_p.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 0 2 20 "+(acu=acu+espcp)+"POR  EL  VALOR DE $"+valor+", DEBERÁ EFECTUARLO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DENTRO DE LOS 10 DÍAS  HÁBILES POSTERIORES A\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LA FECHA DE  ESTA NOTIFICACIÓN.  VENCIDO ESE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PLAZO  DEBERÁ  CANCELAR UNA  MULTA ADICIONAL\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL  DOS  POR  CIENTO  (2%)  SOBRE  EL VALOR\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PRINCIPAL  POR CADA MES O FRACCIÓN DE MES DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"MORA  HASTA UN 100% DEL VALOR DE LA MULTA, Y\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"SI FUERE  NECESARIO  SE  RECAUDARÁ  LA DEUDA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"PENDIENTE  MEDIANTE  PROCEDIMIENTO COACTIVO.\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"EL  INFRACTOR  DE  SER  EL CASO PODRÁ DENTRO\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DEL PLAZO  DE 3 DÍAS HÁBILES  IMPUGNAR  ESTA \r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"CITACIÓN  DE  TRÁNSITO   ANTE  LA  AUTORIDAD\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"COMPETENTE.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return cpcl;
    }

    //CPCL Impresora TCS
    private byte[] getConfigLabelTSCNoti() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }


        int pos=0;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu;

        byte[] cpcl = null;

        String cpclConfigLabel = "! 0 200 200 "+(910+espd+espc)+" 1\r\n"+
                "PW 400\r\n"+
                "TONE 0\r\n"+
                "SPEED 3\r\n"+
                "ON-FEED IGNORE\r\n"+
                "NO-PACE\r\n"+
                "BAR-SENSE\r\n"+

                "T 5 2 70 "+(acu=pos)+" Notificación"+"\r\n"+
                "T 7 0 16 "+(acu=acu+espg+espp+espp)+" 1.Nombre: "+"\r\n"+
                "T 7 0 38 "+(acu=acu+espp)+" "+nombreUsuario.toUpperCase()+", \r\n"+
                "T 7 0 38 "+(acu=acu+espp)+" identificación "+identificacion+" \r\n"+
                "T 7 0 16 "+(acu=acu+espp)+" 2.Vehiculo placa: "+placa+" \r\n"+
                "T 7 0 16 "+(acu=acu+espp)+" 3.Fecha y hora de infraccion: \r\n"+
                "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                "T 7 0 16 "+(acu=acu+espp)+" 4.Lugar de infraaccion:\r\n"+
                "ML 25\r\n"+
                "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                "ENDML\r\n"+
                "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                "T 7 0 16 "+(acu=acu+espp)+" 5.Concepto:\r\n"+
                "ML 25\r\n"+
                "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                "ENDML\r\n"+
                //"T 7 0 16 "+(acu=acu+espc)+" 5.VALOR DE LA MULTA: $"+valor+ "\r\n"+
                "T 7 0 16 "+(acu=acu+espc)+" 6.Mensaje institucional\r\n"+
                //"T 0 2 20 "+(acu=acu+espg)+"LOS RECARGOS POR MORA EN EL PAGO DE LA MULTA\r\n"+
                //"T 0 2 20 "+(acu=acu+esppp)+"SE LOS CALCULARÁ EN EL  MOMENTO  DEL PAGO DE\r\n"+
                //"T 0 2 20 "+(acu=acu+esppp)+"LA PRESTACIÓN, EN LOS TÉRMINOS  PREVISTOS EN\r\n"+
                //"T 0 2 20 "+(acu=acu+esppp)+"EL  ARTÍCULO  179  DE  LA  LEY  ORGÁNICA  DE\r\n"+
                //"T 0 2 20 "+(acu=acu+esppp)+"TRASPORTE  TERRESTRE, TRÁNSITO  Y  SEGURIDAD\r\n"+
                //"T 0 2 20 "+(acu=acu+esppp)+"VIAL.\r\n"+

                "T 7 0 16 "+(acu=acu+espg)+" 7.Agente civil de transito: \r\n"+
                "T 7 0 38 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                "T 7 0 38 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                "T 7 0 38 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+
                "T 7 0 14 "+(acu=acu+espp+espp)+" www.atm.gob.ec"+"\r\n"+
                //"IL 20 "+(acu)+" 200 "+(acu)+" 22\r\n"+

                "PRINT\r\n";

        try {
            cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return cpcl;
    }

    //CPCL Impresora TCS
    private byte[] getConfigLabelTSCArt387() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p2;
        espcp=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>42){
            v_descripcion_p=cad_descripcion_p(v_descripcion_p);
        }
        int pos=0;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(960+espd+espc)+" 1\r\n"+
                    "PW 400\r\n"+
                    "TONE 0\r\n"+
                    "SPEED 3\r\n"+
                    "ON-FEED IGNORE\r\n"+
                    "NO-PACE\r\n"+
                    "BAR-SENSE\r\n"+

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espg+espp)+" "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+

                    "T 0 2 20 "+(acu=acu+espc+espg)+"RESPONDER PARTE DE TRÁNSITO. ACERCARSE A LA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"UNIDAD JUDICIAL COMPETENTE. VIGENCIA DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DOCUMENTO 10 DÍAS.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA  MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else{
            cpclConfigLabel = "! 0 200 200 "+(1000+espd+espc)+" 1\r\n"+
                    "PW 400\r\n"+
                    "TONE 0\r\n"+
                    "SPEED 3\r\n"+
                    "ON-FEED IGNORE\r\n"+
                    "NO-PACE\r\n"+
                    "BAR-SENSE\r\n"+

                    "T 5 2 70 "+(acu=pos)+" No."+num_boleta+"\r\n"+
                    "T 5 0 16 "+(acu=acu+espg+espp)+" "+nombreUsuarioImp.toUpperCase()+"\r\n"+

                    "T 7 0 16 "+(acu=acu+espg)+" "+ licenciaPlacaImp + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 2.FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 3.LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" 4.CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+

                    "T 0 2 20 "+(acu=acu+espc+espg)+"RESPONDER PARTE DE TRÁNSITO. ACERCARSE A LA\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"UNIDAD JUDICIAL COMPETENTE. VIGENCIA DE\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"DOCUMENTO 10 DÍAS.\r\n"+

                    "T 7 0 20 "+(acu=acu+espg)+"AGENTE CIVIL DE TRÁNSITO \r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+" "+nombre_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CÓDIGO:UNIFORMADO NO."+cod_agente.toUpperCase()+"\r\n"+
                    "T 7 0 20 "+(acu=acu+esppp)+"CANTÓN: "+canton.toUpperCase()+"\r\n"+
                    "IL 26 "+(acu=acu+espg+espg+espg+espg)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 100 "+(acu=acu+esppp)+" FIRMA DEL AGENTE\r\n"+

                    "T 0 2 20 "+(acu=acu+espg)+"AV CARLOS JULIO AROSEMENA, GUAYAQUIL, GUAYAS\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"LOCAL 85 TEL:(593)(4)3713889 GUAYAQUIL,\r\n"+
                    "T 0 2 20 "+(acu=acu+esppp)+"ECUADOR.\r\n"+


                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return cpcl;
    }

    //CPCL Impresora TCS
    private byte[] getConfigLabelTSCFedatario() {
        licencia_placa_imp();

        String v_direccion= direccion;
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_descripcion=descripcion;
        espc=25;
        int tdesc=v_descripcion.length();
        if(tdesc>28){
            v_descripcion=cad_descripcion(v_descripcion);
        }

        String v_descripcion_p=descripcion_p2;
        espcf=25;
        int tdescp=v_descripcion_p.length();
        if(tdescp>28){
            v_descripcion_p=cad_descripcion_p_fedatario(v_descripcion_p);
        }

        //Leyenda Impresion
        String leyenda_impresion=observacion_impresion_ticket;
        //Log.i("LEYENDA", leyenda_impresion);
        espcfLeyImp=25;
        int tleyimp=v_descripcion_p.length();
        ////Log.i("tleyimp", tleyimp+" __");
        if(tleyimp>31){
            leyenda_impresion=cad_descripcion_p_fedatarioLeyImp(leyenda_impresion);
            //Log.i("LEYENDA", leyenda_impresion);
        }

        int pos=0;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        //Placa
        if(nombreUsuarioImp.equals("")) {
            cpclConfigLabel = "! 0 200 200 "+(960+espd+espc+espcf+espcfLeyImp)+" 1\r\n"+
                    "PW 400\r\n"+
                    "TONE 0\r\n"+
                    "SPEED 3\r\n"+
                    "ON-FEED IGNORE\r\n"+
                    "NO-PACE\r\n"+
                    "BAR-SENSE\r\n"+
                    "T 5 1 66 "+(acu=pos)+" AVISO DE EVENTO  "+"\r\n"+

                    "T 5 1 74 "+(acu=acu+espp+espp)+ "DE INFRACCIÓN\r\n"+

                    //"T 5 1 16 "+(acu=acu+espp+espp+espp)+ "NO ES MULTA\r\n"+

                    "T 5 1 90 "+(acu=acu+espp+espp+espp)+" No."+num_boleta+"\r\n"+

                    "T 7 0 16 "+(acu=acu+espp+espp+espp)+" a) "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" b) NOMBRE: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+nombreUsuario.toUpperCase() + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" c) FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" d) LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" e) PARQUÍMETRO NÚMERO: "+observacion+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" f) CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion_p+"\r\n"+
                    "ENDML\r\n"+


                    "T 7 0 16 "+(acu=acu+espcf)+" g) BASE LEGAL:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+

                    "T 7 0 16 "+(acu=acu+espc)+" h) VALOR DE LA MULTA: $"+valor+ "\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 20 "+(acu=acu+espp+espg)+" "+leyenda_impresion+"\r\n"+
                    "ENDML\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg+espg+espcfLeyImp)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 40 "+(acu=acu+esppp)+" FIRMA CAPTURADOR DE EVENTO\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Licencia
        } else{
            cpclConfigLabel = "! 0 200 200 "+(930+espd+espc+espcf+espcfLeyImp)+" 1\r\n"+
                    "PW 400\r\n"+
                    "TONE 0\r\n"+
                    "SPEED 3\r\n"+
                    "ON-FEED IGNORE\r\n"+
                    "NO-PACE\r\n"+
                    "BAR-SENSE\r\n"+

                    "T 5 1 66 "+(acu=pos)+" AVISO DE EVENTO  "+"\r\n"+

                    "T 5 1 74 "+(acu=acu+espp+espp)+ "DE INFRACCIÓN\r\n"+

                    //"T 5 1 16 "+(acu=acu+espp+espp+espp)+ "NO ES MULTA\r\n"+

                    "T 5 1 90 "+(acu=acu+espp+espp+espp)+" No."+num_boleta+"\r\n"+

                    "T 5 0 16 "+(acu=acu+espg+esppp)+" "+nombreUsuarioImp.toUpperCase()+"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp+espp)+" a) "+licenciaPlacaImp +"\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" b) FECHA Y HORA: \r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+fecha + "\r\n"+
                    "T 7 0 16 "+(acu=acu+espp)+" c) LUGAR:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion.toUpperCase()+"\r\n"+
                    "ENDML\r\n"+
                    "T 7 0 38 "+(acu=acu+espd)+" ("+ auxLat+", "+auxLon+") "+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" d) PARQUÍMETRO NÚMERO: "+observacion+ "\r\n"+

                    "T 7 0 16 "+(acu=acu+espp)+" e) CONCEPTO:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion_p+"\r\n"+
                    "ENDML\r\n"+


                    "T 7 0 16 "+(acu=acu+espcf)+" f) BASE LEGAL:\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 38 "+(acu=acu+espp)+" "+v_descripcion+"\r\n"+
                    "ENDML\r\n"+

                    //"T 7 0 16 "+(acu=acu+espc)+" g) VALOR DE LA MULTA: $"+valor+ "\r\n"+
                    "ML 25\r\n"+
                    "T 7 0 20 "+(acu=acu+espg+espc)+" "+leyenda_impresion+"\r\n"+
                    "ENDML\r\n"+

                    "IL 26 "+(acu=acu+espg+espg+espg+espg+espg+espcfLeyImp)+" 373 "+(acu)+" 8\r\n"+
                    "T 7 0 40 "+(acu=acu+esppp)+" FIRMA CAPTURADOR DE EVENTO\r\n"+

                    "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE MULTA"+"\r\n"+
                    "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                    "T 7 0 14 "+(acu=acu+espp)+"  WWW.ATM.GOB.EC"+"\r\n"+

                    "PRINT\r\n";

            try {
                cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return cpcl;
    }

    public static void cerrarSesionUser(){

        cerrarSesionUserWebsocket();
        pk_loadingCerrarSesion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_cerrando_sesion), false, false);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(pk_loadingCerrarSesion!=null) {
                            pk_loadingCerrarSesion.dismiss();
                        }
                        session.logoutUser(context);
                    }}, 2500);
    }

    public static void onlyCloseSessionUser(){
        try {
            getMyInstance().unregisterReceiver(mReceiver);
            //getMyInstance().unregisterReceiver(mBatInfoReceiver);
        }catch (Exception e){

        }

        pk_loadingCerrarSesion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_cerrando_sesion), false, false);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(pk_loadingCerrarSesion!=null) {
                            pk_loadingCerrarSesion.dismiss();
                        }
                        Intent i = new Intent(context.getApplicationContext(), SplashActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(i);
                    }}, 2500);
    }
    static ProgressDialog pk_loadingCerrarSesion;
    public static void reinicioSesionUpdate(){
        cerrarSesionUserWebsocket();

        pk_loadingCerrarSesion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_cerrando_sesion), false, false);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        if(pk_loadingCerrarSesion!=null) {
                            pk_loadingCerrarSesion.dismiss();
                        }

                        session.CloseSessionManager();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(res.getString(R.string.str_sistema));
                        builder.setMessage(res.getString(R.string.str_status_70));
                        builder.setCancelable(false);
                        builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(context.getApplicationContext(), SplashActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                context.startActivity(i);

                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_button));
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
                    }
                }, 2500);
    }

    public static void cerrarSesionUserWebsocket(){
        String uuid=session.getUuid();
        String token=session.getToken();
        JSONObject logoutSend = new JSONObject();

        try {
            logoutSend.accumulate("tipo", "logout").accumulate("uuid", uuid)
                    .accumulate("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(webSocketConnection!=null) {
            if (webSocketConnection.isConnected()) {
                webSocketConnection.sendMessage(logoutSend.toString());
            }
        }

        try {
            context.unregisterReceiver(mReceiver);
            //getMyInstance().unregisterReceiver(mBatInfoReceiver);
        }catch (Exception e){

        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_imprimir:
                                limpiarMainActivity();
                                hsBluetoothPrintDriver.stop();
                                validaStartHubrox=0;
                                openDialogImprimir();
                                break;
                            case R.id.nav_salir:
                                cerrarSesionUser();
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    private Dialog dialogImprimir;
    private int pos=0;
    public void openDialogImprimir() {
        dialogImprimir = new Dialog(MainActivity.this);
        dialogImprimir.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogImprimir.setContentView(R.layout.dialog_imprimir);
        //dialogExportarCitaciones.setTitle("Inicia Sesion");
        dialogImprimir.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogImprimir.show();

        Button btnExportarCitaciones = (Button)dialogImprimir.findViewById(R.id.btnExportarCitaciones);
        final TextView txt_descripcion_boleta= (TextView) dialogImprimir.findViewById(R.id.txt_descripcion_boleta);
        final Spinner spinnerBoleta = (Spinner) dialogImprimir.findViewById(R.id.spinner);

        //String[] datos = new String[] {"C#", "Java", "Python", "R", "Go"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Utils.consultarNumboleFirts(context));

        spinnerBoleta.setAdapter(adapter);


        btnExportarCitaciones.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(pos==0){
                    if(consultarDatosNumBoleta(context, txt_descripcion_boleta,spinnerBoleta.getItemAtPosition(pos).toString())){
                        cargarImpresora();
                    }else{
                        Toast.makeText(context, "No existe citaciones", Toast.LENGTH_LONG).show();
                    }
                }else{
                    cargarImpresora();
                }
            }
        });

        spinnerBoleta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                consultarDatosNumBoleta(context, txt_descripcion_boleta,spinnerBoleta.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialogImprimir.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                limpiarMainActivity();
            }
        });

    }




    private static TextView txtViewCount;
    private Button myButton;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_item, menu);
        super.onCreateOptionsMenu(menu);

        final View notificaitons = menu.findItem(R.id.actionNotifications).getActionView();
        txtViewCount = (TextView)notificaitons.findViewById(R.id.textOne);
        myButton=(Button)notificaitons.findViewById(R.id.myButton);

        if(totalCitacionesNoenviadas()==0){
            txtViewCount.setVisibility(View.GONE);
            txtViewCount.setVisibility(View.INVISIBLE);
        }else{
            txtViewCount.setText(totalCitacionesNoenviadas()+"");
            txtViewCount.setVisibility(View.GONE);
            txtViewCount.setVisibility(View.VISIBLE);
        }
        WebSocketComunication.valida_login=2;
        consultarCitacionesNoenviadas();
        if (responseOK == true) {
            if(webSocketConnection!=null) {
                if ((webSocketConnection.isConnected())) {
                    pk_loading_rc = ProgressDialog.show(MainActivity.this, res.getString(R.string.str_sistema), res.getString(R.string.str_reenviando_citacion), false, false);
                    pk_loading_rc.setCancelable(true);
                    reenviarCitacionesNoenviadas();
                }
            }
        }

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarCitacionesNoenviadas();
                if (responseOK == true) {
                    if ((webSocketConnection.isConnected())) {
                        pk_loading_rc = ProgressDialog.show(MainActivity.this, res.getString(R.string.str_sistema), res.getString(R.string.str_reenviando_citacion), false, false);
                        pk_loading_rc.setCancelable(true);
                        reenviarCitacionesNoenviadas();
                    }else{
                        alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_datos));
                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                consultarCitacionesRealizadas();
                return true;
            case R.id.action_settings:
                hsBluetoothPrintDriver.stop();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_legal:
                viewPDF();
                break;
            /*case R.id.errores:
                enviarErrorAppCorreo();
                break;*/
            /*case R.id.exit_app:
                cerraConexion();
                finish();
                System.exit(0);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy () {
        try {
            if(mReceiver!=null) {
                this.unregisterReceiver(mReceiver);
            }
            //getMyInstance().unregisterReceiver(mBatInfoReceiver);
        }catch (Exception e){
        }
        super.onDestroy();

        if(session.isLoggedIn()) {
            Intent imain = new Intent(context, MainActivity.class);
            startActivity(imain);
        }else{
            if(WebSocketComunication.serviceWebsocket!=null) {
                WebSocketComunication.serviceWebsocket.stopForeground(true);
                WebSocketComunication.serviceWebsocket.stopSelf();
                System.exit(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void viewPDF() {
        File pdfFile = new File("/sdcard/ATM/coip_transito.pdf");
        if (pdfFile.exists()){

            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            Uri path = Uri.fromFile(pdfFile);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(path, "application/pdf");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try{
                    startActivity(i);
            }catch(ActivityNotFoundException e){
                alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_sin_app_pdf));
            }
        }else{
            alertDialog(res.getString(R.string.str_error),res.getString(R.string.str_sin_pdf));
        }
    }

    public static String cadena_sin_tildes_caracter(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }


    public static void recibirActualizacion(byte[] bytes) {

        File file = new File("/sdcard/ATM/temp");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file.length() < 4194304) {

            if(file.exists()) {
               file.delete();
            }

            File fileExcel = new File(new File("/sdcard/ATM/"), "MultasElectronica.xls");
            if (fileExcel.exists()) {
                fileExcel.delete();
            }

            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileExcel));
                bos.write(bytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String nuevaVersionCOIP=cargarExcelCoip();
            ////Log.i("VERSION COIP", "VER NUEVA:  uuid "+ session.getUuid());

            JSONObject jsonSendUpdate = null;
            try {
                jsonSendUpdate = new JSONObject().
                        accumulate("tipo", "updateListoCoip").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid()).
                        accumulate("versionCOIP", nuevaVersionCOIP);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (webSocketConnection.isConnected() && banderaCerrarSesion==false){
                webSocketConnection.sendMessage(jsonSendUpdate.toString());
            }
            vibration();

            if(defaultSoundUri!=null) {
                try {
                    final MediaPlayer mp = MediaPlayer.create(context, defaultSoundUri);
                    if (mp != null) {
                        mp.start();
                    }
                }catch (Exception e){

                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(res.getString(R.string.str_alerta));
            builder.setMessage(res.getString(R.string.str_actualizacion_COIP)+nuevaVersionCOIP+" COIP");
            builder.setCancelable(false);
            builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_button));
            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));

        } else {
            if(file.exists()) {
                file.delete();
            }

            File fileApk = new File(new File("/sdcard/ATM/"), Version.getNombreNuevaAPK());

            JSONObject jsonSendUpdate = null;
            try {
                jsonSendUpdate = new JSONObject().
                        accumulate("tipo", "updateListo").
                        accumulate("token", session.getToken()).
                        accumulate("uuid", session.getUuid());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (fileApk.exists()) {
                //fileApk.delete();
                if (webSocketConnection.isConnected() && banderaCerrarSesion==false){
                    webSocketConnection.sendMessage(jsonSendUpdate.toString());
                }
            }else {
                try {
                    BufferedOutputStream bosa = new BufferedOutputStream(new FileOutputStream(fileApk));
                    bosa.write(bytes);
                    bosa.flush();
                    bosa.close();
                    if (webSocketConnection.isConnected() && banderaCerrarSesion==false){
                        webSocketConnection.sendMessage(jsonSendUpdate.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            vibration();

            if(defaultSoundUri!=null) {
                try {
                    final MediaPlayer mp = MediaPlayer.create(context, defaultSoundUri);
                    if (mp != null) {
                        mp.start();
                    }
                }catch (Exception e){

                }
            }

            pk_loadingCerrarSesion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_cerrando_sesion), false, false);
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        cerrarSesionUserWebsocket();
            }}, 1500);

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            session.CloseSessionManager();
                            if(pk_loadingCerrarSesion!=null) {
                                pk_loadingCerrarSesion.dismiss();
                            }
                            ////Log.i("REINICIO","ACTUALIZACION");
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(res.getString(R.string.str_alerta));
                            builder.setMessage(res.getString(R.string.str_sesion_cerrada_apk));
                            builder.setCancelable(false);
                            builder.setPositiveButton(res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(context.getApplicationContext(), SplashActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    context.startActivity(i);
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_button));
                            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));

                        }}, 3600);
        }
    }

    public void consultarCitacionesRealizadas() {
        getCurrentTimeStamp();
        DBParqueoLite admin = new DBParqueoLite(MainActivity.this, "administracion", null, 2);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select count(*) from citacion_no_enviadas where solofecha='"+solo_fecha+"'", null);
        int tam = 0;
        if (fila.moveToFirst()) {
            tam = fila.getInt(0);
        }else{
            tam=0;
        }
        bd.close();
        mnuContadorCitacion.setTitle("Citaciones diarias: "+tam);
    }

    public static void cargarListaInfracion(){
        try{
            autoCompleteTextView.setText("");
            for (int i = (ltsInfraccion.size() - 1); i >= 0; i--) {
                ltsInfraccion.remove(i);
            }
            if(ckb_coip.isChecked()==true) {
                for (String str : new TreeSet<String>(Utils.excelCoipOrdenanza.keySet())) {
                    int t = str.length();
                    if (str.substring(t - 4, t).compareToIgnoreCase("coip") == 0) {
                        String value = Utils.excelCoipOrdenanza.get(str);
                        ltsInfraccion.add(str.substring(0, t - 4) + " " + value);
                    }
                }
            }

            if(ckb_ordenanza.isChecked()==true) {
                for (String str : new TreeSet<String>(Utils.excelCoipOrdenanza.keySet())) {
                    int t = str.length();

                    if(t>=12){
                        if (str.substring(t - 9, t).compareToIgnoreCase("ordenanza") == 0) {
                            String value = Utils.excelCoipOrdenanza.get(str);
                            ltsInfraccion.add(str.substring(0, t - 9) + " " + value);
                        }

                    }
                }
            }
            if(ckb_resolucion.isChecked()==true) {
                for (String str : new TreeSet<String>(Utils.excelCoipOrdenanza.keySet())) {
                    int t = str.length();

                    if(t>=12){
                        if (str.substring(t - 10, t).compareToIgnoreCase("resolucion") == 0) {
                            String value = Utils.excelCoipOrdenanza.get(str);
                            ltsInfraccion.add(str.substring(0, t - 10) + " " + value);
                        }

                    }
                }
            }
            //Collections.sort(strings);
            adapter = new ArrayAdapter(context, 17367050, ltsInfraccion);
            autoCompleteTextView.setThreshold(0);
            autoCompleteTextView.setAdapter(adapter);
        }catch (Exception e){
            //alertDialog(res.getString(R.string.str_error), res.getString(R.string.str_error_excel));
        }
    }

    public void takeFoto() {
        intentC = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentC.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                //mFilePhotoTaken = File.createTempFile("IMG_", ".jpg", storageDir);

                mFilePhotoTaken=setUpPhotoFile();

                // Create the File where the photo should go
                // Continue only if the File was successfully created

                if (mFilePhotoTaken != null) {

                    //Log.i("VERSION ANDROID","API "+Build.VERSION.SDK_INT);

                    if (Build.VERSION.SDK_INT >= 21) {
                        //Log.i("VERSION ANDROID","API "+Build.VERSION_CODES);
                        ///Log.i("Path",mFilePhotoTaken.getPath());
                        // Implement this feature without material design
                        packUri= getApplicationContext().getPackageName() + ".my.package.name.provider";
                        mUriPhotoTaken = FileProvider.getUriForFile(this,
                                packUri,
                                mFilePhotoTaken);
                        intentC.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                        //inicializarPermision();

                    } else {
                        // Call some material design APIs here
                        mUriPhotoTaken = Uri.fromFile(mFilePhotoTaken);
                        intentC.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                    }

                    // Finally start camera activity
                    startActivityForResult(intentC, REQUEST_IMAGE_CAPTURE);

                }
            } catch (IOException e) {
                //setInfo(e.getMessage());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE:
                        if(mCurrentPhotoPath != null){
                            BitmapFactory.Options bounds = new BitmapFactory.Options();
                            // Decode the image file into a Bitmap sized to fill the View
                            bounds.inJustDecodeBounds = false;
                            bounds.inSampleSize = 4;
                            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, bounds);
                            Log.i("TAMANIO1", bm.getByteCount()+"___");
                            String value = Utils.getManufacturer();
                            Log.i("Manufacture",value+"___");
                            if(value.compareToIgnoreCase("Urovo")==0 || value.compareToIgnoreCase("samsung")==0 || value.compareToIgnoreCase("Motorola Solutions")==0 || value.compareToIgnoreCase("Google")==0) {
                                // Read EXIF Data
                                ExifInterface exif = null;
                                try {
                                    exif = new ExifInterface(mCurrentPhotoPath);
                                    Log.i("exif",exif.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(exif!=null) {
                                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                                    int rotationAngle = 0;
                                    //int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                                    // Rotate Bitmap
                                    Matrix matrix = new Matrix();
                                    matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                                    bm = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);


                                }
                            }

                            File file = new File(mCurrentPhotoPath);

                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
                                Log.i("TAMANIO2", bm.getByteCount()+"___");
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            File file2 = new File(mCurrentPhotoPath);
                            setImagenBitmap(file2.getPath());
                            galleryAddPic(Uri.fromFile(file2));
                        }
                        break;
                    case SELECT_PICTURE:
                        final Uri u=data.getData();
                        String path = getRealPathFromURI(context,u);
                        BitmapFactory.Options bounds = new BitmapFactory.Options();
                        // Decode the image file into a Bitmap sized to fill the View
                        bounds.inJustDecodeBounds = false;
                        bounds.inSampleSize = 4;
                        Bitmap bm = BitmapFactory.decodeFile(path, bounds);

                        File file = new File(path);

                        if(file.length()>1000) {
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
                                Log.i("TAMANIO2", bm.getByteCount() + "___");
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        File file2 = new File(path);
                        setImagenBitmap(file2.getPath());

                        break;
                }
            }
        }
    }

    public void setImagenBitmap(String path){

        Bitmap bm = BitmapFactory.decodeFile(path);
        Log.i("TAMANIO", bm.getByteCount()+"___");
        //Bitmap bm = Bitmap.createScaledBitmap(imageBitmap,500,662,true);
        if (contFoto == 0) {
            img_foto_infraccion.setImageBitmap(bm);
            imagen1 = getStringImage(bm);
            contFoto = 1;
        } else {
            img_foto_infraccion2.setImageBitmap(bm);
            imagen2 = getStringImage(bm);
            contFoto = 0;
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Log.i("PATH", contentUri.getPath()+"  ");
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            Log.d("PAHT SOTRE", storageDir.getPath()+"GRRRRRR");
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private void galleryAddPic(Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
