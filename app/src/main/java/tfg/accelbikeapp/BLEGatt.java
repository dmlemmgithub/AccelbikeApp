package tfg.accelbikeapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by rodry on 30/03/16.
 */
public class BLEGatt {

    private static BLEGatt instancia;

    private BLEGatt(){

        super();

    }

    public static BLEGatt getInstancia(){

        if (instancia == null)
            instancia = new BLEGatt();

        return instancia;

    }

    private BluetoothGatt mGatt;

    private static final UUID SERVICE_UUID = UUID.fromString("0000a000-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("0000a001-0000-1000-8000-00805f9b34fb");

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

            leer();

        }

        //Aqui se recibe la info que nos manda el dispositivoghjg
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {

            byte[] datos = characteristic.getValue();

            short x = datos[0];
            x = (short) (((x << 8) & 0xFF00) | (datos[1] & 0x00FF));

            short y = datos[2];
            y = (short) (((y << 8) & 0xFF00) | (datos[3] & 0x00FF));

            short z = datos[4];
            z = (short) (((z << 8) & 0xFF00) | (datos[5] & 0x00FF));

            Log.i("Eje X", Integer.toString(x));
            Log.i("Eje Y", Integer.toString(y));
            Log.i("Eje Z", Integer.toString(z));

        }
    };

    public void connectToDevice(Context context, BluetoothDevice device) {

        if (mGatt == null){

            mGatt = device.connectGatt(context, false, gattCallback);
            //scanLeDevice(false);
        }
    }

    public void disconnect(){

        if (mGatt == null) return;
        mGatt.close();
        mGatt = null;

    }

    public void leer(){

        BluetoothGattService alertService = mGatt.getService(SERVICE_UUID);

        if(alertService == null) {
            Log.d("writealertlevel", "MCP service not found!");
            return;
        }

        for (BluetoothGattCharacteristic c : alertService.getCharacteristics()) {
            Log.i("Caracteristicas", c.getUuid().toString());
        }

        BluetoothGattCharacteristic alertLevel = alertService.getCharacteristic(CHARACTERISTIC_UUID);
        if(alertLevel == null) {
            Log.d("writealertlevel", "MCP charateristic not found!");
            return;
        }

        Log.i("PROPERTY_READ", Integer.toString(alertLevel.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ));

        boolean ok = mGatt.readCharacteristic(alertLevel);

        Log.i("WriteAlertLevel", "readCharacteristic: " + ok);

    }

}
