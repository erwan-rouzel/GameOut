package fr.ecp.sio.gameout.remote;

import java.net.InetAddress;

/**
 * Created by erwan on 14/11/2015.
 */

public class Player {
    public Team parentTeam;
    public InetAddress ip;
    public PlayerType type;
    public PlayerState state;
    public short x;
    public short y;
    public short vx;
    public short vy;


    public Player(Team parentTeam) {
        this.parentTeam = parentTeam;
        ip = null;
        type = PlayerType.Guest;
        state = PlayerState.Active;
        x = 0;
        y = 0;
        vx = 1;
        vy = 1;
    }
}