package tfg.accelbikeapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.List;

/**
 * Created by David on 22/02/2016.
 */
public class PrincipalFragment extends Fragment implements GattObserver{

    Button inicio, parar;
    Chronometer crono;
    TextView acel;

    Thread th;

    long Time = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.principal_layout, container, false);
        View v = inflater.inflate(R.layout.principal_layout, null);
        BLEGatt.getInstancia().registerObserver(this);
        initUI(v);
        return v;
    }

    public void initUI(View v){

        crono = (Chronometer) v.findViewById(R.id.cronometro);
        inicio = (Button) v.findViewById(R.id.inicio);
        parar = (Button) v.findViewById(R.id.parar);
        acel = (TextView) v.findViewById(R.id.acel);

        inicio.setEnabled(true);
        parar.setEnabled(false);
        acel.setText("info acel");

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO comprobar que BLEGatt esta conectado
                crono.start();
                inicio.setEnabled(false);
                parar.setEnabled(true);
                crono.setBase(SystemClock.elapsedRealtime());
                startThread();

            }
        });

        parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inicio.setEnabled(true);
                parar.setEnabled(false);
                crono.stop();
                stopThread();

            }
        });
    }

    public void startThread(){

        th = new Thread(new Runnable() {

            public void run() {

                while (!Thread.interrupted()) {

                    BLEGatt.getInstancia().leer();

                    try {

                        Thread.sleep(1000);

                    }
                    catch (InterruptedException e) {

                        return;

                    }
                }
            }
        });

        th.start();

    }

    public void stopThread(){

        th.interrupt();
        th = null;

    }

    public void onDestroy(){

        Log.i("PrincipalFragment", "Destruido");

        super.onDestroy();
        BLEGatt.getInstancia().removeObserver(this);

    }

    public void onDataRead(final List<Short> valores){

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                acel.setText(Short.toString(valores.get(0)) + ", " +
                             Short.toString(valores.get(1)) + ", " +
                             Short.toString(valores.get(2)));

            }
        });

        Log.i("PrincipalFragment", "He recibido un dato!");

    }
}