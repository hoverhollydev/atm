package d4d.com.atm.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by JuanPablo on 06/03/2016.
 */
public class Version {

    public static SharedPreferences prefVersion;
    private static SharedPreferences.Editor editorVersion;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_VERS = "PrefVersiones";
    private static final String IS_SAVE = "IsSaveIn";
    public static final String KEY_VERSION_APK = "versionAPK";
    public static final String KEY_NOMBRE_APK = "nombreAPK";

    // Constructor
    public Version(Context context) {
        this._context = context;
        prefVersion = _context.getSharedPreferences(PREF_VERS, PRIVATE_MODE);
        editorVersion = prefVersion.edit();
        editorVersion.commit();
    }

    public void datosVersion(String verionNuevaAPK, String nombreNuevaAPK){
        editorVersion.putBoolean(IS_SAVE, true);
        editorVersion.putString(KEY_VERSION_APK, verionNuevaAPK);
        editorVersion.putString(KEY_NOMBRE_APK, nombreNuevaAPK);
        editorVersion.commit();
    }

    public static boolean isSaveIn() {
        return prefVersion.getBoolean(IS_SAVE, false);
    }

    public static String getVersionNuevaAPK(){
        return prefVersion.getString(KEY_VERSION_APK, null);
    }

    public static void setVersionNuevaAPK(String versionAPK){
        editorVersion.putString(KEY_VERSION_APK, versionAPK);
        editorVersion.commit();
    }

    public static String getNombreNuevaAPK(){
        return prefVersion.getString(KEY_NOMBRE_APK, null);
    }
    public static void setNombreNuevaAPK(String nombreAPK){
        editorVersion.putString(KEY_NOMBRE_APK, nombreAPK);
        editorVersion.commit();
    }
}