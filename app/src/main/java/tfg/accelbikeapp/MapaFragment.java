package tfg.accelbikeapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

/**
 * Created by David on 22/02/2016.
 */
public class MapaFragment extends Fragment {
    GoogleMap googleMap;
    MapView mapView;

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mapa_layout,container,false);

        mapView = (MapView)v.findViewById(R.id.mi_mapa);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        return v;
    }


}
