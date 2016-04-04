package tfg.accelbikeapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rodry on 30/03/16.
 */
public class DispAdapter extends ArrayAdapter<BluetoothDevice> {

    public final Context context;
    public final ArrayList<BluetoothDevice> values;

    public DispAdapter(Context context, ArrayList<BluetoothDevice> values){

        super(context, R.layout.row_layout, values);
        this.context = context;
        this.values = values;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        rowView = inflater.inflate(R.layout.row_layout, parent, false);

        String dispositivo = values.get(position).getName() + ", " +
                values.get(position).getAddress();

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(dispositivo);

        return rowView;

    }
}
