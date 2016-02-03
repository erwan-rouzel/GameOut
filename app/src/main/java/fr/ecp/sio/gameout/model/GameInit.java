package fr.ecp.sio.gameout.model;

/**
 * Created by erwanrouzel on 31/01/16.
 */
public class GameInit {
    public byte teamId;
    public byte playerId;

    public GameInit(int teamId, int playerId) {
        this.teamId = (byte) teamId;
        this.playerId = (byte) playerId;
    }
}