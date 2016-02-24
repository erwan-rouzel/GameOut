package fr.ecp.sio.gameout.model;

import fr.ecp.sio.gameout.utils.GameoutUtils;

/**
 * Created by erwan on 14/11/2015.
 */
public class Ball extends GameObject {
    private static short SPEED_COEFF = 10;
    public short x;
    public short y;
    public short vx;
    public short vy;
    public short rx;
    public short ry;

    public Ball() {
        initSpeed();
        initPosition();
        initRadius();
    }

    public void updateSpeed(int speedPlayerX, int speedPlayerY) {
        vx += vx * speedPlayerX * (SPEED_COEFF + GameoutUtils.randomWithRange(0, 20))/100;
        vy += vy * speedPlayerY * (SPEED_COEFF + GameoutUtils.randomWithRange(0, 20))/100;
    }

    public void initSpeed() {
        vx = (short) (5 + GameoutUtils.randomWithRange(0, 3));
        vy = (short) (7 + GameoutUtils.randomWithRange(0, 3));
    }

    public void initPosition() {
        x = HVPoint.WIDTH_REF / 2;
        y = HVPoint.HEIGHT_REF / 2;
    }

    public void initRadius() {
        rx = HVPoint.WIDTH_REF / 80;
        ry = HVPoint.WIDTH_REF / 80;
    }
}