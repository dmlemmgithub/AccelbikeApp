package tfg.accelbikeapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodry on 30/03/16.
 */
public class BLEScanner { // Alexis joputa

    private BluetoothLeScanner scanner;
    private ArrayList<BluetoothDevice> dispositivos;

    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            //Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());

            String mensaje;
            BluetoothDevice btDevice = result.getDevice();
            mensaje = result.toString();


                // Mostrar el dispositivo en la lista
            if(!dispositivos.contains(btDevice)) {

                dispositivos.add(btDevice);

            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

            String mensaje;

            for (ScanResult sr : results) {

                Log.i("ScanResult - Results", sr.toString());

                BluetoothDevice btDevice = sr.getDevice();
                mensaje = btDevice.toString();

                //mensaje = sr.toString();
                // mostrarToast(mensaje);

                // Mostrar el dispositivo en la lista
                if(!dispositivos.contains(btDevice)) {

                    dispositivos.add(btDevice);

                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {

            String mensaje = "Fallo en el escaneo";
            // mostrarToast(mensaje);
            Log.e("Scan Failed", "Error code: " + errorCode);
        }
    };


    public BLEScanner(ArrayList<BluetoothDevice> disp){

        dispositivos = disp;

    }

    public void listarDispositivos(BluetoothAdapter adapter){

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        List<ScanFilter> filtros = new ArrayList<>();

        scanner = adapter.getBluetoothLeScanner();

        scanner.startScan(filtros, settings, mScanCallback);

    }

    public void pararEscaneo(){

        scanner.stopScan(mScanCallback);
        Log.i("listarDispositivos", "Escaneo parado");

    }

    public List<BluetoothDevice> getLista(){

        return dispositivos;

    }

    public void destroy(){

        scanner.stopScan(mScanCallback);
        scanner = null;
        dispositivos = null;

    }
}
