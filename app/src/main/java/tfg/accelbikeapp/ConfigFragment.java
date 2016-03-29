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
import android.bluetooth.le.ScanResult;
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
    Spinner lista;
    String[] datosLista = {"Dispositivos"};

    Boolean statusGPS;
    private BluetoothAdapter bAdapter;
    private ScanSettings settings;
    private List<ScanFilter> filtros;
    //mGatt--------------------------------------------------------------
    private BluetoothGatt mGatt;
    BluetoothGattCharacteristic characteristic;
    private Handler mHandler;
    boolean enabled;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private static final UUID MCP_SERVICE_UUID = UUID.fromString("0000a000-0000-1000-8000-00805f9b34fb");
    private static final UUID MCP_CHARACTERISTIC_UUID = UUID.fromString("0000a001-0000-1000-8000-00805f9b34fb");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.config_layout, null);
        initUI(v);
        return v;
    }

    public void initUI(View v){
        gps = (Switch) v.findViewById(R.id.switch1);
        ble = (Button) v.findViewById(R.id.botonble);
        lista = (Spinner) v.findViewById(R.id.listble);

        //-------------Bluetooth---------------
        ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth();
            }
        });
        // Con el boolean statusGPS aqui tenemos que controlar que
        // todo ha ido bien para que cuando cargemos la lista no pete!!
        lista.setEnabled(false);
        ArrayAdapter<String> adapatdor = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, datosLista);
        lista.setAdapter(adapatdor);

        lista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void bluetooth(){
        String mensaje;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bAdapter = bluetoothManager.getAdapter();

        //mHandler = new Handler();

        if (bAdapter == null){
            ble.setEnabled(false); // El dispositivo no tiene Bluetooth, desactivamos boton
        }else{
            if (bAdapter.isEnabled()){
                mensaje = "Bluetooth ya esta activado";
                mostrarToast(mensaje);
            }else{

                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,REQUEST_ENABLE_BT);
            }
            listarDispositivos();
        }
    }

    public void listarDispositivos(){

        BluetoothLeScanner scaner;
        String mensaje;

        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filtros = new ArrayList<>();

        scaner = bAdapter.getBluetoothLeScanner();

        scaner.startScan(filtros,settings,mScanCallback);

        mensaje = "Escaneando";
        mostrarToast(mensaje);

    }

    //----------------------------------------------------------------------------------------------

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            //Log.i("callbackType", String.valueOf(callbackType));
            //Log.i("result", result.toString());

            String mensaje;
            //BluetoothDevice btDevice = result.getDevice();
            mensaje = result.toString();
            mostrarToast(mensaje);

            /*
            // Mostrar el dispositivo en la lista
            if(!dispositivos.contains(btDevice)) {

                dispositivos.add(btDevice);
                adapter.notifyDataSetChanged();
            }
            */
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

            String mensaje;

            for (ScanResult sr : results) {

                //Log.i("ScanResult - Results", sr.toString());

                BluetoothDevice btDevice = sr.getDevice();
                mensaje = btDevice.toString();

                //mensaje = sr.toString();
                mostrarToast(mensaje);

                /*BluetoothDevice btDevice = sr.getDevice();

                // Mostrar el dispositivo en la lista
                if(!dispositivos.contains(btDevice)) {

                    dispositivos.add(btDevice);
                    adapter.notifyDataSetChanged();
                }*/
            }
        }

        @Override
        public void onScanFailed(int errorCode) {

            String mensaje = "Fallo en el escaneo";
            mostrarToast(mensaje);
            //Log.e("Scan Failed", "Error code: " + errorCode);
        }
    };

    //----------------------------------------------------------------------------------------------

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            // en esta funcion se puede hacer que cambie de accion con un intent y eso
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState){

                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices(); // invoca a onServicesDiscovered
                    // mostrarDatos();
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i("gattCallback", "STATE_DISCONNECTED");
                    mGatt = null;
                    break;

                default:
                    Log.i("gattCallback", "STATE_OTHER");
                    break;

            } // switch
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());


            //dd
            for (BluetoothGattService s : services){

                Log.i("onServicesDiscovered", "Servicio: " + s.getUuid());

            }

            //gatt.readCharacteristic(gatt.getService(IMMEDIATE_ALERT_UUID).getCharacteristic(ALERT_LEVEL_UUID));
           /*int s = gatt.getService(IMMEDIATE_ALERT_UUID).getCharacteristic(ALERT_LEVEL_UUID)
                    .getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);*/

            // Esto lee la primera caracteristica del primer servicio, mirar como pillarlas bien.
            //gatt.readCharacteristic(services.get(2).getCharacteristics().get(0));
            /*int s = gatt.getService(IMMEDIATE_ALERT_UUID).getCharacteristic(ALERT_LEVEL_UUID)
                    .getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            Log.i("ALERT_SERVICE", "" + s);*/

        }

        //Aqui se recibe la info que nos manda el dispositivo
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {

            byte[] datos = characteristic.getValue();

            byte data1 = datos[0];
            byte data2 = datos[1];

            int ch0 = data1 << 8;
            ch0 |= (data2 & 0x00FF);
            ch0 &= 0x03FF;
            Log.i("Data1", Integer.toString(data1));
            Log.i("Data2", Integer.toString(data2));
            Log.i("CH0", Integer.toString(ch0));

            //channel0 = ch0;
        }
    };


    public void connectToDevice(BluetoothDevice device) {

        if (mGatt == null){
            mGatt = device.connectGatt(this.getContext(), false, gattCallback);
            //scanLeDevice(false);
        }
    }

    public void disconnect(/*View view*/){

        if (mGatt == null) return;
        mGatt.close();
        mGatt = null;

    }

    public void mostrarToast(String mensaje){
        Toast.makeText(this.getContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    public void onDestroy(){
        super.onDestroy();

        if (mGatt == null) return;
        mGatt.close();
        mGatt = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == Activity.RESULT_CANCELED){
                //Bluetooth not enabled
                //finish();
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public void alertLevel(View view){

        BluetoothGattService alertService = mGatt.getService(MCP_SERVICE_UUID);

        if(alertService == null) {
            Log.d("writealertlevel", "MCP service not found!");
            return;
        }

        for (BluetoothGattCharacteristic c : alertService.getCharacteristics()) {
            Log.i("Caracteristicas", c.getUuid().toString());
        }

        BluetoothGattCharacteristic alertLevel = alertService.getCharacteristic(MCP_CHARACTERISTIC_UUID);
        if(alertLevel == null) {
            Log.d("writealertlevel", "MCP charateristic not found!");
            return;
        }
        //alertLevel.setValue(level, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
       /* mGatt.writeCharacteristic(alertLevel);*/

        boolean ok = mGatt.readCharacteristic(alertLevel/*mGatt.getService(IMMEDIATE_ALERT_UUID)
                            .getCharacteristic(ALERT_LEVEL_UUID)*/);

        Log.i("WriteAlertLevel", "readCharacteristic: " + ok);

        //mostrarToast(Integer.toString(channel0));
    }
}