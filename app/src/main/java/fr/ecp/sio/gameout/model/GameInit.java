package fr.ecp.sio.gameout.model;

/**
 * Created by erwanrouzel on 31/01/16.
 */
public class GameInit {
    public String roomId;
    public String serverHostName;
    public int sessionId;
    public byte teamId;
    public byte playerId;

    public GameInit(String roomId, String serverHostName, int sessionId, int teamId, int playerId) {
        this.roomId = roomId;
        this.serverHostName = serverHostName;
        this.sessionId = sessionId;
        this.teamId = (byte) teamId;
        this.playerId = (byte) playerId;
    }
}