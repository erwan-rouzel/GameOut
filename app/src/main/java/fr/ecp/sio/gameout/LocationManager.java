package fr.ecp.sio.gameout;

import fr.ecp.sio.gameout.model.HVPoint;

/**
 * Created by erwanrouzel on 09/12/15.
 */
public class LocationManager {
    private int playerId;
    private int teamId;
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

    public void setPlayer(byte teamId, byte playerId) {
        this.playerId = playerId;
        this.teamId = teamId;
    }

    public byte getPlayerId() {
        return (byte)playerId;
    }

    public byte getTeamId() {
        return (byte)teamId;
    }
}