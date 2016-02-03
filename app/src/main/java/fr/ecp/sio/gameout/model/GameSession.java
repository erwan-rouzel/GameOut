package fr.ecp.sio.gameout.model;

//import com.google.gson.JsonElement;

import org.json.JSONArray;

public class GameSession extends GameObject {
    public int id;
    public int timestamp;
    public int scores;
    public int gameType;
    // TODO erwan : rajouter ces infos ici + dans le protocole + côté serveur
    // Numéro d'équipe
    // Identifiant du joueur google play
    // Taille du terrain après calibration en H
    // Taille du terrain après calibration en V
    // Coordonnée GPS du joueur (pour faire des stats)
    // Heure locale du joueur (pour faire des stats)

    // Infos connues uniquement de l'hôte (on peut les mettre à -1 pour les autres joueurs)
    public int numberOfPlayersInTeam1;
    public int numberOfPlayersInTeam2;
    public int numberOfPlayersInTeam3;


    public GameSession() {
        this.id = -1;
    }

    public GameSession(JSONArray json) {

    }
}