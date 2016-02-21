package fr.ecp.sio.gameout.remote;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

import fr.ecp.sio.gameout.TimeKeeper;
import fr.ecp.sio.gameout.model.GameInit;
import fr.ecp.sio.gameout.model.GameSession;
import fr.ecp.sio.gameout.model.Player;
import fr.ecp.sio.gameout.utils.GameoutUtils;

/*

 Facade client :
 -
 */

public class GameoutClient {
    private static final String SESSION_SERVER_IP = "195.154.123.213";
    private static final int TCP_PORT = 9475;
    private static final int UDP_PORT = 9476;

    private InetAddress sessionIpAddress;
    private InetAddress streamIpAddress;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;

    public GameSession getGameSession() {
        return gameSession;
    }

    private GameSession gameSession;
    private byte messageCounter;
    private static GameoutClient instance;

    private GameoutClient() throws UnknownHostException, SocketException {
        messageCounter = 0;
        sessionIpAddress = InetAddress.getByName(SESSION_SERVER_IP);
        udpSocket = new DatagramSocket();
        gameSession = null;
    }

    public static synchronized GameoutClient getInstance() throws IOException {
        if(instance == null) {
            instance = new GameoutClient();
        }

        return instance;
    }

    public GameInit startGameSession(GameSession session) throws IOException {
        gameSession = session;

        String jsonSession = new Gson().toJson(session);
        Log.i("JSON_SESSION", jsonSession);
        String response = sendMessageTCP(jsonSession);

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(response));
        reader.setLenient(true);
        return gson.fromJson(reader, GameInit.class);
    }

    public boolean isGameStarted() {
        return (gameSession != null);
    }

    public byte[] sendPosition(Player player) throws IOException {
        /*
        4 octets : ID Partie
        8 octets : timestamp en secondes
        1 octet : ID team
        1 octet : ID Joueur dans team
        1 octet : action
        1 octet : incrément
        2 octets : coordonnée X
        2 octets : coordonnée Y
        2 octets : coordonnée VX
        2 octets : coordonnée VY
        */

        byte[] buffer;
        ArrayList<Byte> message = new ArrayList<Byte>();

        buffer = GameoutUtils.intToBytes(gameSession.id);
        message.add(buffer[0]);
        message.add(buffer[1]);
        message.add(buffer[2]);
        message.add(buffer[3]);

        buffer = GameoutUtils.longToBytes(new Date().getTime());
        message.add(buffer[0]);
        message.add(buffer[1]);
        message.add(buffer[2]);
        message.add(buffer[3]);
        message.add(buffer[4]);
        message.add(buffer[5]);
        message.add(buffer[6]);
        message.add(buffer[7]);

        message.add(player.parentTeam.id);

        message.add(player.id);

        message.add((byte)0);

        message.add(messageCounter);

        buffer = GameoutUtils.shortToBytes(player.x);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(player.y);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(player.vx);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(player.vy);
        message.add(buffer[0]);
        message.add(buffer[1]);

        byte[] result = new byte[message.size()];
        for(int i = 0; i < message.size(); i++) {
            result[i] = message.get(i);
        }

        return sendMessageUDP(result);
    }

    private String sendMessageTCP(String message) throws IOException {
        tcpSocket = new Socket(sessionIpAddress, TCP_PORT);

        OutputStream output = tcpSocket.getOutputStream();
        output.write(GameoutUtils.stringToBytes(message));

        BufferedReader input = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        String response = input.readLine();
        tcpSocket.close();

        return response;
    }

    private byte[] sendMessageUDP(byte[] sendData) throws IOException {

        TimeKeeper.duratStartEvent(7);
        udpSocket = new DatagramSocket();
        udpSocket.setSoTimeout(1200);

        byte[] receiveData = new byte[1024];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, streamIpAddress, UDP_PORT);
        udpSocket.send(sendPacket);
        TimeKeeper.duratEndEvent(7);

        TimeKeeper.duratStartEvent(8);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        TimeKeeper.duratEndEvent(8);

        TimeKeeper.duratStartEvent(9);
        udpSocket.receive(receivePacket);
        byte[] responseBytes = receivePacket.getData();
        udpSocket.setSoTimeout(200);
        udpSocket.close();
        TimeKeeper.duratEndEvent(9);

        return responseBytes;
    }

    public void setStreamIpAdress(String ip) throws UnknownHostException {
        this.streamIpAddress = InetAddress.getByName(ip);
    }
}