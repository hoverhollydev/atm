package d4d.com.atm.utils;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

import d4d.com.atm.R;
import d4d.com.atm.data_base.DBParqueoLite;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class Utils {
    public static HashMap<String, String> excelCoipOrdenanza;

    public static String getNumberCitation(int number){
        String numberString = " ";
        if (number < 10){
            numberString = "00000" + String.valueOf(number);
        }else if (number >= 10 && number < 100){
            numberString = "0000" + String.valueOf(number);
        }else if (number >= 100 && number < 1000){
            numberString = "000" + String.valueOf(number);
        }else if (number >= 1000 && number < 10000){
            numberString = "00" + String.valueOf(number);
        }else if (number >= 10000 && number < 100000){
            numberString = "0" + String.valueOf(number);
        }else{
            numberString = String.valueOf(number);
        }
        return numberString;
    }

    public static String getImei(Context context) throws SecurityException {
        String str="";
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                str=readFileAsString(context);
            }else{
                str = ((TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            }

            Log.i("IMEIconfig", "IMEI "+ str);
            Log.i("IMEIconfig", "IMEI ACT"+ Build.VERSION.SDK_INT);
            Log.i("IMEIconfig", "IMEI  Q"+ Build.VERSION_CODES.Q);

        } catch (SecurityException localException) {
            //throw new SecurityException(localException);
        }
        return str;
    }

    public static void readExcelFile(String filename, String fedatarioSession) throws Exception {
        excelCoipOrdenanza = new HashMap<String, String>();
        // Creating Input Stream
        File file = new File(filename);
        FileInputStream myInput = new FileInputStream(file);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setEncoding("ISO-8859-1");
        Workbook wb = Workbook.getWorkbook(myInput,wbSettings);
        Sheet s=wb.getSheet(0);
        int row =s.getRows();
        int col=s.getColumns();
        String ley ="";
        String referencia ="";
        String articulo = "";
        String numeral = "";
        String descripcion ="";
        String valor ="";
        String fedatario =""; // fedatario Valor= s o n
        String observacion =""; //Leyenda de impresión
        for(int i=1;i<row;i++) {
            for (int c=0;c<col;c++) {
                Cell z = s.getCell(c,i);
                if(c==0){
                    ley=z.getContents();
                }
                if(c==1){
                    referencia=z.getContents();
                }
                if(c==2){
                    articulo=z.getContents();
                }
                if(c==3){
                    numeral=z.getContents();
                }
                if(c==4){
                    descripcion=z.getContents();
                }
                if(c==5){
                    valor=z.getContents();
                }
                if(c==7){
                    fedatario=z.getContents();
                }
                if(c==8){
                    observacion=z.getContents();
                }
            }

            if(fedatarioSession.compareToIgnoreCase("true")==0){
                if(fedatario.compareToIgnoreCase("n")==0) {
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "ley", ley);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "ref", referencia);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "art", articulo);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "num", numeral);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "des", descripcion);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "val", valor);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "obs", observacion);
                    //Log.i("RESOLUCION", articulo + "-" + numeral + " resolucion " +descripcion);

                    if (ley.compareToIgnoreCase("COIP") == 0) {
                        excelCoipOrdenanza.put(articulo + "-" + numeral + "coip", descripcion);
                    }
                    if (ley.compareToIgnoreCase("ORDENANZA") == 0) {
                        excelCoipOrdenanza.put(articulo + "-" + numeral + "ordenanza", descripcion);
                    }
                    if (ley.compareToIgnoreCase("RESOLUCION") == 0) {
                        excelCoipOrdenanza.put(articulo + "-" + numeral + "resolucion", descripcion);

                    }
                }
            }else {
                if(fedatario.compareToIgnoreCase("s")==0) {
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "ley", ley);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "ref", referencia);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "art", articulo);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "num", numeral);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "des", descripcion);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "val", valor);
                    excelCoipOrdenanza.put(articulo + "-" + numeral + "obs", observacion);

                    if (ley.compareToIgnoreCase("COIP") == 0) {
                        excelCoipOrdenanza.put(articulo + "-" + numeral + "coip", descripcion);
                    }
                    if (ley.compareToIgnoreCase("ORDENANZA") == 0) {
                        excelCoipOrdenanza.put(articulo + "-" + numeral + "ordenanza", descripcion);
                    }
                    if (ley.compareToIgnoreCase("RESOLUCION") == 0) {
                        excelCoipOrdenanza.put(articulo + "-" + numeral + "resolucion", descripcion);
                    }
                }
            }

        }
    }

    public static String getVersionCoip(String filename) throws Exception {
        // Creating Input Stream
        File file = new File(filename);
        FileInputStream myInput = new FileInputStream(file);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setEncoding("ISO-8859-1");
        Workbook wb = Workbook.getWorkbook(myInput,wbSettings);
        Sheet s=wb.getSheet(0);
        String version ="";
        try {
            Cell z = s.getCell(6, 1);
            version = z.getContents();
        }catch (Exception e){
        }finally {
        }
        return version;
    }

    public static void alertDialog(final String title, final String message, Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
        });

        AlertDialog alert = builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(ContextCompat.getColor(c, R.color.blue_button));
        pbutton.setTextColor(ContextCompat.getColor(c,R.color.window_background));
    }


    public static String versionActualAPK(Resources res){
        String version=res.getString(R.string.str_version);
        return version;
    }

    public static String getCurrentTimeStamp() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        //TimeStamp
        Long tsl = calendar.getTimeInMillis();
        String ts = tsl.toString();
        return ts;
    }

    public static void eliminarCitacionesOffline(Context context) {
        int num1=consultarNumeroCitaciones(context);
        String numBoleta=consultarNumboletFIFO(context);
        if(num1>10) {
            //Log.i("numBoleta",numBoleta+" >>>>>>>>");
            if(numBoleta.compareToIgnoreCase("00")!=0) {
                //bd.delete("citacion_no_enviadas", "estado='enviada' and solofecha !='" + solo_fecha + "'", null);
                bd_citacionE.delete("citacion_no_enviadas", "estado='enviada' and numBoleta ='" + numBoleta + "'", null);
                bd_citacionE.close();
                eliminarCitacionesOffline(context);
            }
        }else{
            bd_citacionE.close();
        }

    }

    //SQLlite
    public static DBParqueoLite adminE;
    public static SQLiteDatabase bd_citacionE;
    public static int consultarNumeroCitaciones(Context context) {
        adminE = new DBParqueoLite(context, "administracion", null, 2);
        bd_citacionE = adminE.getWritableDatabase();
        Cursor fila = bd_citacionE.rawQuery("select count(*) from citacion_no_enviadas", null);
        int tam=fila.getCount();
        int c=0;
        if (fila!=null) {
            if (fila.moveToFirst()) {
                for (int i = 0; i < tam; i++) {
                    c = fila.getInt(0);
                    fila.moveToNext();
                }
            }
        }
        return c;
    }

    public static String consultarNumboletFIFO(Context context) {
        Cursor fila = bd_citacionE.rawQuery("select *  from citacion_no_enviadas where estado='enviada' ORDER BY numBoleta ASC LIMIT 1" , null);
        int tam=fila.getCount();
        String f18="00";
        if (fila!=null) {
            if (fila.moveToFirst()) {
                for (int i = 0; i < tam-1; i++) {
                    f18 = fila.getString(18);
                    fila.moveToNext();
                }
            }
        }
        return f18;
    }

    public static String[] consultarNumboleFirts(Context context) {
        int num1=consultarNumeroCitaciones(context);
        String[]  numBoleta= null;
        if(num1>0) {
            Cursor fila = bd_citacionE.rawQuery("select *  from citacion_no_enviadas ORDER BY numBoleta DESC LIMIT 10", null);
            int tam = fila.getCount();
            if (fila != null) {
                numBoleta=new String[tam];
                if (fila.moveToFirst()) {
                    for (int i = 0; i < tam; i++) {
                        numBoleta[i] = fila.getString(18);
                        fila.moveToNext();
                    }
                } else {
                }
            }
        }else{
            numBoleta =new String[1];
            numBoleta[0]="No existe citación";
        }
        bd_citacionE.close();
        return numBoleta;
    }

    public static String getManufacturer(){
        String value = Build.MANUFACTURER;
        return value;
    }

    public static String obtenerModelo(){
        String valor = Build.MODEL;
        return valor;
    }

    public static String getSerialChip(Context context) throws SecurityException {
        String str="";
        try {
            str=((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();//Serial Chip
        } catch (SecurityException localException) {
        }
        return str;
    }

    public static String getOperadorCelular(Context context) throws SecurityException {
        String str="";
        try {
            str=((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();//Operador
        } catch (SecurityException localException) {
        }
        return str;
    }


    /*private static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("/sdcard/ATM/imeiconfig.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("IMEItxt", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("IMEItxt", "Can not read file: " + e.toString());
        }

        return ret;
    }*/

    public static String readFileAsString(Context context) {
        //Context context = App.instance.getApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        //sdcard/ATM/imeiconfig.txt"

        Log.i("IMEIDIR",context.getFilesDir().getParent());
        try {
            in = new BufferedReader(new FileReader(new File("/sdcard/ATM/", "imeiconfig.txt")));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            //Logger.logError(TAG, e);
        } catch (IOException e) {
            //Logger.logError(TAG, e);
        }

        return stringBuilder.toString();
    }


}
