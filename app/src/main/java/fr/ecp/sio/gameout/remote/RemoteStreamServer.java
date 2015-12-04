package fr.ecp.sio.gameout.remote;

import com.google.android.gms.games.Game;

import fr.ecp.sio.gameout.model.GameState;
import fr.ecp.sio.gameout.model.Player;
import fr.ecp.sio.gameout.model.PlayerState;
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
        DatagramSocket udpSocket;
        byte[] udpData = new byte[1024];
        byte[] sendData;
        DatagramPacket receivePacket = new DatagramPacket(udpData, udpData.length);
        DatagramPacket sendPacket;

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
                log("Before receive packet");
                udpSocket.receive(receivePacket);
                log("After receive packet");
                updateGameState(receivePacket.getData());
                log("After update game state");
            } catch (Exception e) {
                log(e);
            }
        }
    }

    public static void updateGameState(byte[] m) {
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

        RemoteGameState remoteGameState = RemoteGameState.getInstance();
        if(remoteGameState == null) return;

        remoteGameState.timestamp =  GameoutUtils.bytesToLong(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7]);
        remoteGameState.increment = m[8];
        remoteGameState.teams[0].score = m[9];
        remoteGameState.teams[1].score = m[10];
        remoteGameState.teams[2].score = m[11];
        remoteGameState.ball.x = GameoutUtils.bytesToShort(m[12], m[13]);
        remoteGameState.ball.y = GameoutUtils.bytesToShort(m[14], m[15]);
        remoteGameState.ball.vx = GameoutUtils.bytesToShort(m[16], m[17]);
        remoteGameState.ball.vy = GameoutUtils.bytesToShort(m[18], m[19]);

        byte sizeTeam1 = m[20];
        byte sizeTeam2 = m[21];
        byte sizeTeam3 = m[22];

        int n = 23;
        for(int i = 0; i < sizeTeam1; i++) {
            remoteGameState.teams[0].players[i].x =     GameoutUtils.bytesToShort(m[n + 0 + i*9],   m[n + 1 + i*9]);
            remoteGameState.teams[0].players[i].y =     GameoutUtils.bytesToShort(m[n + 2 + i*9],   m[n + 3 + i*9]);
            remoteGameState.teams[0].players[i].vx =    GameoutUtils.bytesToShort(m[n + 4 + i*9],   m[n + 5 + i*9]);
            remoteGameState.teams[0].players[i].vy =    GameoutUtils.bytesToShort(m[n + 6 + i*9],   m[n + 7 + i*9]);
            remoteGameState.teams[0].players[i].state = (m[n + 8 + i*9] == 0)?PlayerState.Inactive:PlayerState.Active;
        }

        n = 23 + sizeTeam1*9;
        for(int i = 0; i < sizeTeam2; i++) {
            remoteGameState.teams[1].players[i].x =     GameoutUtils.bytesToShort(m[n + 0 + i*9],   m[n + 1 + i*9]);
            remoteGameState.teams[1].players[i].y =     GameoutUtils.bytesToShort(m[n + 2 + i*9],   m[n + 3 + i*9]);
            remoteGameState.teams[1].players[i].vx =    GameoutUtils.bytesToShort(m[n + 4 + i*9],   m[n + 5 + i*9]);
            remoteGameState.teams[1].players[i].vy =    GameoutUtils.bytesToShort(m[n + 6 + i*9],   m[n + 7 + i*9]);
            remoteGameState.teams[1].players[i].state = (m[n + 8 + i*9] == 0)?PlayerState.Inactive:PlayerState.Active;
        }

        n = 23 + sizeTeam2*9;
        for(int i = 0; i < sizeTeam3; i++) {
            remoteGameState.teams[2].players[i].x =     GameoutUtils.bytesToShort(m[n + 0 + i*9],   m[n + 1 + i*9]);
            remoteGameState.teams[2].players[i].y =     GameoutUtils.bytesToShort(m[n + 2 + i*9],   m[n + 3 + i*9]);
            remoteGameState.teams[2].players[i].vx =    GameoutUtils.bytesToShort(m[n + 4 + i*9],   m[n + 5 + i*9]);
            remoteGameState.teams[2].players[i].vy =    GameoutUtils.bytesToShort(m[n + 6 + i*9],   m[n + 7 + i*9]);
            remoteGameState.teams[2].players[i].state = (m[n + 8 + i*9] == 0)?PlayerState.Inactive:PlayerState.Active;
        }
    }
}
