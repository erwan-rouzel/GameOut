package fr.ecp.sio.gameout.remote;

import fr.ecp.sio.gameout.model.GameState;
import fr.ecp.sio.gameout.model.Player;
import fr.ecp.sio.gameout.model.Team;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by erwan on 21/11/2015.
 */
public class RemoteStreamServer extends AbstractServer implements Runnable {
    public static final int PLAYER_PORT=9500;
    public RemoteStreamServer(int port) {
        super(port);
    }

    public void run() {
        DatagramPacket sendPacket;
        DatagramSocket udpSocket;
        byte[] udpData = new byte[1024];
        byte[] sendData;
        byte[] gameStateMessage;

        try {
            udpSocket = new DatagramSocket(port);
        } catch(SocketException e) {
            log(e);
            System.exit(0);
            return;
        }

        log(this.getClass().getSimpleName() + " now listening on port " + port + "...");

        while(true) {
            /* Process UDP Packets */
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log(e);
            }

            try {
                //TODO
            } catch (Exception e) {
                log(e);
            }
        }
    }

    public static byte[] getGameStateMessage(GameState gameState) {
        /*
        8 octets : timestamp
        1 octet : incrément
        3 octets : scores (entre 0 et 9)
        2 octets : X balle
        2 octets : Y balle
        2 octets : VX balle
        2 octets : VY balle
        1 octet : nombre de personnes équipe 1
        1 octet : nombre de personnes équipe 2
        1 octet : nombre de personnes équipe 3

        2 octets : X (E1, J1)
        2 octets : Y (E1, J1)
        2 octets : VX (E1, J1)
        2 octets : VY (E1, J1)
        1 octet : état J1
        ...
        */

        ArrayList<Byte> message = new ArrayList<Byte>();

        long timestamp = new Date().getTime();
        byte increment = 0;
        byte score1 = gameState.teams[0].score;
        byte score2 = gameState.teams[1].score;
        byte score3 = gameState.teams[2].score;
        short ballX = gameState.ball.x;
        short ballY = gameState.ball.y;
        short ballVX = gameState.ball.vx;
        short ballVY = gameState.ball.vy;
        byte numPlayersTeam1 = (byte) gameState.teams[0].players.length;
        byte numPlayersTeam2 = (byte) gameState.teams[1].players.length;
        byte numPlayersTeam3 = (byte) gameState.teams[2].players.length;

        byte[] buffer;

        buffer = GameoutUtils.longToBytes(timestamp);
        message.add(buffer[0]);
        message.add(buffer[1]);
        message.add(buffer[2]);
        message.add(buffer[3]);
        message.add(buffer[4]);
        message.add(buffer[5]);
        message.add(buffer[6]);
        message.add(buffer[7]);

        message.add(increment);
        message.add(score1);
        message.add(score2);
        message.add(score3);

        buffer = GameoutUtils.shortToBytes(ballX);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballY);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballVX);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballVY);
        message.add(buffer[0]);
        message.add(buffer[1]);

        message.add(numPlayersTeam1);
        message.add(numPlayersTeam2);
        message.add(numPlayersTeam3);

        for(Team t: gameState.teams) {
            for(Player p: t.players) {
                buffer = GameoutUtils.shortToBytes(p.x);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.y);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.vx);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.vy);
                message.add(buffer[0]);
                message.add(buffer[1]);

                message.add((byte) p.state.ordinal());
            }
        }

        byte[] result = new byte[message.size()];
        for(int i = 0; i < message.size(); i++) {
            result[i] = message.get(i);
        }
        return result;
    }

    public static void updateGameState(GameState state) {
        state.ball.x += 1;
        state.ball.y += 1;
    }
}
