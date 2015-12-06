package fr.ecp.sio.gameout.model;

/**
 * Created by erwan on 14/11/2015.
 */
public class Ball extends GameObject {
    public short x;
    public short y;
    public short vx;
    public short vy;

    public Ball() {
        x = 0;
        y = 0;
        vx = 1;
        vy = 1;
    }
}