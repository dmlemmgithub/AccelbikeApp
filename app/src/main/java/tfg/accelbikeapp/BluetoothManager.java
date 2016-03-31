package tfg.accelbikeapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodry on 30/03/16.
 */
public class BluetoothManager {

    private Context context;
    private BluetoothAdapter bAdapter;
    private BLEScanner mScanner;

    public BluetoothManager(Context context, ArrayList<BluetoothDevice> disp){

        this.context = context;

        mScanner = new BLEScanner(disp);
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        bAdapter = bluetoothManager.getAdapter();

    }

    public boolean bluetoothDisponible(){

        return bAdapter != null;

    }

    public boolean bluetoothActivado(){

        return bAdapter.isEnabled();

    }

    public void listarDispositivos(){

        mScanner.listarDispositivos(bAdapter);

    }

    public void pararEscaneo(){

        mScanner.pararEscaneo();

    }

    public void conectarDispositivo(BluetoothDevice bd){

        BLEGatt.getInstancia().disconnect();
        BLEGatt.getInstancia().connectToDevice(context, bd);
        //BLEGatt.getInstancia().leer(); // TODO Quitar esto de aqui

    }

}
