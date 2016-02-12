package fr.ecp.sio.gameout.model;

import java.net.InetAddress;

/**
 * Created by erwan on 14/11/2015.
 */

public class Player extends GameObject {
    public byte id;
    public Team parentTeam;
    public InetAddress ip;
    public byte type;
    public byte state;
    public short x;
    public short y;
    public short vx;
    public short vy;

    // TODO erwan : rajouter ces infos ici + dans le protocole + côté serveur
    // Numéro d'équipe
    // Identifiant du joueur google play
    // Coordonnée GPS du joueur (pour faire des stats)
    // Heure locale du joueur (pour faire des stats)


    public Player(byte id, Team parentTeam) {
        this.parentTeam = parentTeam;
        this.id = id;
        ip = null;
        type = PlayerType.GUEST;
        state = PlayerState.ACTIVE;
        vx = 1;
        vy = 1;

        x = 5000;

        if(parentTeam.isBottom) {
            y = (short) (10000 - 1500*((short)id));
        } else {
            y = 1000;
        }
    }
}