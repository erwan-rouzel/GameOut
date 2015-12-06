package fr.ecp.sio.gameout.remote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by erwan on 21/11/2015.
 */
public class RemoteStreamServer extends AbstractServer implements Runnable {
    public RemoteStreamServer(int port) {
        super(port);
    }

    public void run() {
        DatagramSocket udpSocket;
        byte[] udpData = new byte[128];
        byte[] sendData = new byte[32];
        DatagramPacket receivePacket = new DatagramPacket(udpData, udpData.length);
        DatagramPacket sendPacket;

        log(this.getClass().getSimpleName() + " now listening on port " + port + "...");

        while(true) {
            /* Process UDP Packets */
            try {
                try {
                    udpSocket = new DatagramSocket(this.port);
                } catch(SocketException e) {
                    log(e);
                    System.exit(0);
                    return;
                }

                log("Before receive packet");
                udpSocket.receive(receivePacket);
                log("After receive packet");
                GameoutClientHelper.updateGameState(receivePacket.getData());
                log("After update game state");
            } catch (Exception e) {
                log(e);
            }
        }

        //udpSocket.close();
    }
}
