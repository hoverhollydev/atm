package d4d.com.atm;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import d4d.com.atm.utils.SessionManager;
import d4d.com.atm.utils.Utils;
import d4d.com.atm.utils.Version;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class SplashActivity extends AppCompatActivity {
    //Usuario
    private SessionManager session;
    private Version version;

    private int Splash_time = 3000;
    public static AppCompatActivity splash;
    private String versionActualAPK="";
    private String versionNuevaAPK="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        inicializarPermisos();
    }

    private static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    //Manifest.permission.CAMERA,

    private void inicializarPermisos(){
        if(!permisosAndroid6plus(this, PERMISSIONS)){
            //Permisos SO Android 6.x.x o superior
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else{
            validarInicio();
        }
    }

    private void validarInicio(){
        session = new SessionManager(getApplicationContext());
        version = new Version(getApplicationContext());
        versionActualAPK = Utils.versionActualAPK(getResources());
        Log.i("VERSION APK ACTUAL",versionActualAPK);
        if(version.isSaveIn()){
            versionNuevaAPK = Version.getVersionNuevaAPK();
        }else{
            versionNuevaAPK = versionActualAPK;
            Log.i("versionNuevaAPK",versionNuevaAPK);
        }

        if(versionActualAPK.compareToIgnoreCase(versionNuevaAPK)==0){
            if (session.isLoggedIn() == false) {
                ProgressBar mBar = (ProgressBar) findViewById(R.id.progressBarCircular);
                mBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                iniciarLoginActivity();
            } else {
                Splash_time = 0;
                iniciarMainActivity();
            }
        }else {
            Splash_time = 1500;
            if (session.isLoggedIn() == true) {
                session.CloseSessionManager();
            }
            verificarAPK();
        }
    }

    private void iniciarMainActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(session.getFedatario().compareToIgnoreCase("false")==0){
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }else{
                    intent = new Intent(SplashActivity.this, MainActivity2.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, Splash_time);
        splash = this;
    }

    private void iniciarLoginActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, Splash_time);
        splash = this;
    }

    String app_pkg_name = "d4d.com.atm";
    String path="";
    int UNINSTALL_REQUEST_CODE = 1;
    private void verificarAPK(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                try {


                //if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    path = Environment.getExternalStorageDirectory() + "/ATM/" + Version.getNombreNuevaAPK();
                //} else{
                    //path= "/sdcard/ATM/"+ Version.getNombreNuevaAPK();
                //}

                    File file = new File(path);
                    if (file.exists()) {
                        if(versionActualAPK.compareToIgnoreCase("2.0.8")==0 || versionActualAPK.compareToIgnoreCase("2.0.9")==0 || versionActualAPK.compareToIgnoreCase("2.0.10")==0 || versionActualAPK.compareToIgnoreCase("2.0.11")==0 || versionActualAPK.compareToIgnoreCase("2.0.12")==0){
                            app_pkg_name = "app.atm.comunication";
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.putExtra("Intent.ACTION_PACKAGE_REPLACED", "d4d.com.atm");
                        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        Intent intent1 = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent1.setData(Uri.parse("package:" + app_pkg_name));
                        intent1.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        startActivityForResult(intent1, UNINSTALL_REQUEST_CODE);
                    }else{
                        iniciarLoginActivity();
                    }
                }catch (Exception e){
                    Log.e("Error","Procesos");
                }
            }
        }, Splash_time);
        splash = this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Log.d("TAG", "onActivityResult: user accepted the (un)install");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.putExtra("Intent.ACTION_PACKAGE_REPLACED", "d4d.com.atm");
                intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else if (resultCode == RESULT_CANCELED) {
                //Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                //Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    public static boolean permisosAndroid6plus(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if ((grantResults.length > 0)
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[3] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[4] == PackageManager.PERMISSION_GRANTED)) {
                    validarInicio();
                }else{
                    if(!permisosAndroid6plus(this, PERMISSIONS)) {
                        //Permisos SO Android 6.x.x o superior
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    }
                }
                break;
            default:
                break;
        }
    }
}
