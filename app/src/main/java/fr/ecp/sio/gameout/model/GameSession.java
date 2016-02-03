package fr.ecp.sio.gameout.model;

//import com.google.gson.JsonElement;

import org.json.JSONArray;

public class GameSession extends GameObject {
    public int id;
    public int timestamp;
    public int scores;
    public int gameType;
    public int numberOfPlayersInTeam1;
    public int numberOfPlayersInTeam2;
    public int numberOfPlayersInTeam3;

    public GameSession() {
        this.id = -1;
    }

    public GameSession(JSONArray json) {

    }
}