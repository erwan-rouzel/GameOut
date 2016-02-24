package fr.ecp.sio.gameout.model;

import fr.ecp.sio.gameout.utils.GameoutUtils;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/* TODO erwan : rajouter ces infos ici + dans le protocole + côté serveur
  Taille du terrain après calibration en H
  Taille du terrain après calibration en V
  Infos connues uniquement de l'hôte (on peut les mettre à -1 pour les autres joueurs)
*/

public class GameSession extends GameObject {
    @SerializedName("id")
    public int id;
    @SerializedName("roomId")
    public String roomId;
    @SerializedName("timestamp")
    public int timestamp;
    @SerializedName("gameType")
    public int gameType;
    @SerializedName("serverHostName")
    public String serverHostName;
    @SerializedName("numberOfPlayersInTeam1")
    public int numberOfPlayersInTeam1;
    @SerializedName("numberOfPlayersInTeam2")
    public int numberOfPlayersInTeam2;
    @SerializedName("numberOfPlayersInTeam3")
    public int numberOfPlayersInTeam3;

    public GameSession() {
        this.id = -1;
        this.timestamp = (int) GameoutUtils.getCurrentTimestamp();
        double d = 10000*(new Random().nextDouble());
        this.roomId = ((Long)((long) d)).toString();
        this.serverHostName = "noserver";
        this.gameType = GameType.PONG_MONO;
        this.numberOfPlayersInTeam1 = 1;
        this.numberOfPlayersInTeam2 = 0;
        this.numberOfPlayersInTeam3 = 0;
    }

    public GameSession(int id, String roomId, String serverHostName, int gameType, int numberOfPlayersInTeam1, int numberOfPlayersInTeam2, int numberOfPlayersInTeam3) {
        this.id = id;
        this.timestamp = (int) GameoutUtils.getCurrentTimestamp();
        this.roomId = roomId;
        this.serverHostName = serverHostName;
        this.gameType = gameType;
        this.numberOfPlayersInTeam1 = numberOfPlayersInTeam1;
        this.numberOfPlayersInTeam2 = numberOfPlayersInTeam2;
        this.numberOfPlayersInTeam3 = numberOfPlayersInTeam3;
    }

    public void updateFromGameInit(GameInit gameInit) {
        this.id = gameInit.sessionId;
        this.roomId = gameInit.roomId;
        this.serverHostName = gameInit.serverHostName;
    }
}