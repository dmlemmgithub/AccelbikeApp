package tfg.accelbikeapp;

import java.util.List;

/**
 * Created by rodry on 31/03/16.
 */
public interface GattObserver {

    void onDataRead(List<Short> valores);

}
