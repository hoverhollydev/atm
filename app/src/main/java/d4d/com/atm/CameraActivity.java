package d4d.com.atm;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {
    int numTiempo=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (null == savedInstanceState) {

            String videoTiempo=getIntent().getStringExtra("videoTiempo");

            try {
                numTiempo= Integer.parseInt(videoTiempo);
            }catch (Exception e){
                numTiempo=5000;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //Put the value
                Camera2VideoFragment c2v = new Camera2VideoFragment ();
                Bundle args = new Bundle();
                args.putInt("videoTiempo", numTiempo);
                c2v.setArguments(args);
                //Inflate the fragment
                getFragmentManager().beginTransaction().add(R.id.container, c2v).commit();
            }else{
                Toast.makeText(getApplicationContext(), "La grabación de video no esta disponible para versiones inferiores a Android 6.0", Toast.LENGTH_LONG).show();

            }
        }
    }

}
