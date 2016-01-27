package fr.ecp.sio.gameout;

import fr.ecp.sio.gameout.model.HVPoint;

/**
 * Created by erwanrouzel on 09/12/15.
 */
public class LocationManager {
    private static LocationManager mInstance;
    private HVPoint position;

    private LocationManager() {

    }

    public static LocationManager getInstance() {
        if(mInstance == null) {
            mInstance = new LocationManager();
        }
        return mInstance;
    }

    public HVPoint getCurrentPosition() {
        return position;
    }

    public void setPosition(HVPoint position) {
        this.position = position;
    }
}