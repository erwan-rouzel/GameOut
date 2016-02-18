package fr.ecp.sio.gameout.remote.helper;

import fr.ecp.sio.gameout.model.PlayerState;
import fr.ecp.sio.gameout.remote.RemoteGameState;
import fr.ecp.sio.gameout.utils.GameoutUtils;

/**
 * Created by erwanrouzel on 06/12/15.
 */
public class GameoutClientHelper {
    public static void updateGameState(byte[] m) {
        /*
        8 octets : timestamp
        1 octet : status
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
        remoteGameState.status = m[8];
        remoteGameState.increment = m[9];

        if(remoteGameState.session.numberOfPlayersInTeam1 > 0) {
            remoteGameState.teams[0].score = m[10];
        }

        if(remoteGameState.session.numberOfPlayersInTeam2 > 0) {
            remoteGameState.teams[1].score = m[11];
        }

        if(remoteGameState.session.numberOfPlayersInTeam3 > 0) {
            remoteGameState.teams[2].score = m[12];
        }

        remoteGameState.ball.x = GameoutUtils.bytesToShort(m[13], m[14]);
        remoteGameState.ball.y = GameoutUtils.bytesToShort(m[15], m[16]);
        remoteGameState.ball.vx = GameoutUtils.bytesToShort(m[17], m[18]);
        remoteGameState.ball.vy = GameoutUtils.bytesToShort(m[19], m[20]);

        byte sizeTeam1 = m[21];
        byte sizeTeam2 = m[22];
        byte sizeTeam3 = m[23];

        int n = 24;
        for(int i = 0; i < sizeTeam1; i++) {
            remoteGameState.teams[0].players[i].x =     GameoutUtils.bytesToShort(m[n + 0 + i*9],   m[n + 1 + i*9]);
            remoteGameState.teams[0].players[i].y =     GameoutUtils.bytesToShort(m[n + 2 + i*9],   m[n + 3 + i*9]);
            remoteGameState.teams[0].players[i].vx =    GameoutUtils.bytesToShort(m[n + 4 + i*9],   m[n + 5 + i*9]);
            remoteGameState.teams[0].players[i].vy =    GameoutUtils.bytesToShort(m[n + 6 + i*9],   m[n + 7 + i*9]);
            remoteGameState.teams[0].players[i].state = (m[n + 8 + i*9] == 0)? PlayerState.INACTIVE:PlayerState.ACTIVE;
        }

        n = 24 + sizeTeam1*9;
        for(int i = 0; i < sizeTeam2; i++) {
            remoteGameState.teams[1].players[i].x =     GameoutUtils.bytesToShort(m[n + 0 + i*9],   m[n + 1 + i*9]);
            remoteGameState.teams[1].players[i].y =     GameoutUtils.bytesToShort(m[n + 2 + i*9],   m[n + 3 + i*9]);
            remoteGameState.teams[1].players[i].vx =    GameoutUtils.bytesToShort(m[n + 4 + i*9],   m[n + 5 + i*9]);
            remoteGameState.teams[1].players[i].vy =    GameoutUtils.bytesToShort(m[n + 6 + i*9],   m[n + 7 + i*9]);
            remoteGameState.teams[1].players[i].state = (m[n + 8 + i*9] == 0)?PlayerState.INACTIVE:PlayerState.ACTIVE;
        }

        n = 24 + sizeTeam2*9;
        for(int i = 0; i < sizeTeam3; i++) {
            remoteGameState.teams[2].players[i].x =     GameoutUtils.bytesToShort(m[n + 0 + i*9],   m[n + 1 + i*9]);
            remoteGameState.teams[2].players[i].y =     GameoutUtils.bytesToShort(m[n + 2 + i*9],   m[n + 3 + i*9]);
            remoteGameState.teams[2].players[i].vx =    GameoutUtils.bytesToShort(m[n + 4 + i*9],   m[n + 5 + i*9]);
            remoteGameState.teams[2].players[i].vy =    GameoutUtils.bytesToShort(m[n + 6 + i*9],   m[n + 7 + i*9]);
            remoteGameState.teams[2].players[i].state = (m[n + 8 + i*9] == 0)?PlayerState.INACTIVE:PlayerState.ACTIVE;
        }
    }
}
