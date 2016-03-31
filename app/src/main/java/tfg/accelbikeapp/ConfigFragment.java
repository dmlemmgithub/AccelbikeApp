package tfg.accelbikeapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by David on 23/02/2016.
 */

public class ConfigFragment extends Fragment {

    Switch gps;
    Button ble;
    ListView lista;
    String[] datosLista = {"Dispositivos"};

    Boolean statusGPS;

    BluetoothGattCharacteristic characteristic;
    private Handler mHandler;
    boolean enabled;

    private tfg.accelbikeapp.BluetoothManager manager;
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter bAdapter;

    private static final int REQUEST_ENABLE_BT = 1;

    private DispAdapter dispAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("ConfigFragment", "creado");
        View v = inflater.inflate(R.layout.config_layout, null);
        initUI(v);
        return v;
    }

    public void initUI(View v){

        gps = (Switch) v.findViewById(R.id.switch1);
        ble = (Button) v.findViewById(R.id.botonble);
        lista = (ListView) v.findViewById(R.id.listble);

        final ArrayList<BluetoothDevice> dispositivos = new ArrayList<>();
        dispAdapter = new DispAdapter(getContext(), dispositivos);

        lista.setAdapter(dispAdapter);

        manager = new tfg.accelbikeapp.BluetoothManager(this.getContext(), dispositivos);

        // Configurar el boton
        if (!manager.bluetoothDisponible())
            ble.setEnabled(false);
        else
            ble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                if (!manager.bluetoothActivado()){

                    Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnBTon, REQUEST_ENABLE_BT);

                }
                else listar();

                }
            });

        //-------------Bluetooth---------------

        // Con el boolean statusGPS aqui tenemos que controlar que
        // todo ha ido bien para que cuando cargemos la lista no pete!!
        lista.setEnabled(true);
        /*ArrayAdapter<String> adapatdor = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, datosLista);
        lista.setAdapter(adapatdor);*/

        final Context context = this.getContext();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                manager.conectarDispositivo(dispositivos.get(position));

            }
        });
    }

    public void listar(){

        Handler mHandler = new Handler();

        manager.listarDispositivos();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                manager.pararEscaneo();
                dispAdapter.notifyDataSetChanged();

            }
        }, SCAN_PERIOD); // mHandler.postDelayed
    }

    public void mostrarToast(String mensaje){
        Toast.makeText(this.getContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    public void onDestroy(){
        super.onDestroy();

        Log.i("ConfigFragment", "destruido");


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode != Activity.RESULT_CANCELED){

                listar();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}