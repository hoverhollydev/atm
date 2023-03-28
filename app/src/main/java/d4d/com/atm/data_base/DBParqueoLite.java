package d4d.com.atm.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JuanPablo on 12/09/2016.
 */
public class DBParqueoLite extends SQLiteOpenHelper {

    public DBParqueoLite(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL("drop table if exists citacion_no_enviadas");

        db.execSQL("create table citacion_no_enviadas(id integer primary key AUTOINCREMENT, " +
                "usuario text, tipo_identificacion text, identificacion text," +
                " placa text, cod_agente text, articulo text, literal text, fecha text," +
                " direccion text, zona text, provincia text, localidad text, distrito text," +
                " circuito text, subCircuito text, institucion text, observacion text, " +
                "numBoleta text, tipoLicencia text, numCitacion text, descripcion text, " +
                "latitud text, longitud text, imagen text, imagen2 text, estado text, " +
                "timestamp text, solofecha text, valor text, transmision text, precision text, " +
                "nombre_licencia text, descripcion_p text, " +
                "descripcion_p2 text, descripcionH text, notifica text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        db.execSQL("drop table if exists citacion_no_enviadas");

        db.execSQL("create table citacion_no_enviadas(id integer primary key AUTOINCREMENT, " +
                "usuario text, tipo_identificacion text, identificacion text," +
                " placa text, cod_agente text, articulo text, literal text, fecha DATETIME," +
                " direccion text, zona text, provincia text, localidad text, distrito text," +
                " circuito text, subCircuito text, institucion text, observacion text, " +
                "numBoleta text, tipoLicencia text, numCitacion text, descripcion text, " +
                "latitud text, longitud text, imagen text, imagen2 text, estado text, " +
                "timestamp text, solofecha text, valor text, transmision text, precision text, " +
                "nombre_licencia text, descripcion_p text, " +
                "descripcion_p2 text, descripcionH text, notifica text)");
    }
}
