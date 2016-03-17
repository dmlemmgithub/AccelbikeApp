package tfg.accelbikeapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * Created by David on 10/03/2016.
 */
public class AccelerometerFragment extends Fragment implements SensorEventListener {

    TextView ejeX, ejeY, ejeZ,speed;
    float last_x, last_y, last_z;
    long lastUpdate;
    int SHAKE_MOVIL = 400;
    /*TODO Hacer como opción de configuración
    * SENSIBILIDAD : ALTA SHAKE_MOVIL = 400;
    * SENSIBILIDAD : MEDIA SHAKE_MOVIL = 800;
    * SENSIBILIDAD : BAJA SHAKE_MOVIL = 1600;
    * */
    private Sensor mAccelerometer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.accelerometer_layout,container,false);
        ejeX = (TextView) v.findViewById(R.id.x_id);
        ejeY = (TextView) v.findViewById(R.id.y_id);
        ejeZ = (TextView) v.findViewById(R.id.z_id);
        speed = (TextView) v.findViewById(R.id.speed);

        return v;
    }

    public void onResume(){
        super.onResume();
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() > 0){ //Compruebo si tiene acelerometro mi dispositivo
            //registrar en mi sensor manager,tiempo de retardo
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    public void onPause(){
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this,mAccelerometer);
        super.onPause();
    }

    public void onStop(){
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this,mAccelerometer);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        // only allow one update every 1000ms (1 seg) 5000ms (5 seg).
        if ((curTime - lastUpdate) > 5000) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float accel_diff = (x + y + z - last_x - last_y - last_z);

            //Velocidad = aceleración / tiempo
            float speed = Math.abs(accel_diff / diffTime * 500000);

            if (speed > SHAKE_MOVIL) {
                this.speed.setText(String.format("Vel Aceleración: %s w/ speed", String.valueOf(speed)));

                //mCallback.onTrigger(Enums.Types.ACCELERATION, Enums.Action.AMBIGUOUS);
            }
            last_x = x;
            last_y = y;
            last_z = z;
            this.ejeX.setText(String.format("X: %s", String.valueOf(x)));
            this.ejeY.setText(String.format("Y: %s", String.valueOf(y)));
            this.ejeZ.setText(String.format("Z: %s", String.valueOf(z)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
