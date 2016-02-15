package fr.ecp.sio.gameout.remote;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import fr.ecp.sio.gameout.CurPfp;
import fr.ecp.sio.gameout.LocationManager;
import fr.ecp.sio.gameout.model.GameInit;
import fr.ecp.sio.gameout.model.GameSession;
import fr.ecp.sio.gameout.model.GameState;
import fr.ecp.sio.gameout.model.HVPoint;
import fr.ecp.sio.gameout.model.Player;
import fr.ecp.sio.gameout.model.Team;
import fr.ecp.sio.gameout.remote.helper.GameoutClientHelper;

/**
 * Created by od on 11/20/2015.
 */
public class RemoteGameState extends GameState
{
    private static RemoteGameState instance;

    private RemoteGameState(GameSession session) throws IOException, ExecutionException, InterruptedException {
        super(session);
        StartSessionTask startSessionTask = new StartSessionTask();
        GameInit gameInit = startSessionTask.execute(session).get();
        LocationManager.getInstance().setPlayer(gameInit.teamId, gameInit.playerId);
        session.updateFromGameInit(gameInit);

        GameoutClient.getInstance().setStreamIpAdress(session.serverHostName);
        this.id = gameInit.sessionId;

    }

    public static synchronized RemoteGameState startGame(GameSession session) throws IOException, ExecutionException, InterruptedException {
        if(instance == null) {
            instance = new RemoteGameState(session);
        }

        return instance;
    }

    public static synchronized RemoteGameState startGame() {
        return instance;
    }

    public void sendPosition(final HVPoint position) {
        //TODO peut-on retirer les lignes en commentaires dans sendPosition
        //TODO sendPosition fait pense que c'est appelé souvent (10Hz) pourtant il y a un
        //SendPositionTask serverTask = new SendPositionTask();
        //serverTask.execute(position);

        String responseFromServer;
        GameoutClient client = null;
        try {
            client = GameoutClient.getInstance();
            LocationManager locationManager = LocationManager.getInstance();
            GameState gameState = new GameState(client.getGameSession());
            byte teamId = locationManager.getTeamId();
            byte playerId = locationManager.getPlayerId();

            Team team = new Team(teamId, gameState, gameState.teams[teamId].players.length);

            //HVPoint position = params[0];
            Player player = new Player(playerId, team);

            player.x = (short)position.H;
            player.y = (short)position.V;
            player.vx = (short)1;
            player.vx = (short)1;

            byte[] responseBytes = client.sendPosition(player);
            GameoutClientHelper.updateGameState(responseBytes);
            responseFromServer = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class StartSessionTask extends AsyncTask<GameSession, Void, GameInit> {
        @Override
        protected GameInit doInBackground(GameSession... params) {
            try {
                return GameoutClient.getInstance().startGameSession(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(GameInit gameInit) {
            super.onPostExecute(gameInit);
            CurPfp.pfp.isGameStarted = true;
        }
    }

    /*
    TODO : faire fonctionner l'envoie des données avec cette AsyncTask

    private static class SendPositionTask extends AsyncTask<HVPoint, Void, String> {

        @Override
        protected String doInBackground(HVPoint... params) {
            String responseFromServer = "";

            GameoutClient client = null;
            try {
                client = GameoutClient.startGame();

                GameState gameState = new GameState(client.getGameSession());
                Team team = new Team((byte)0, gameState, 2);

                HVPoint position = params[0];
                Player player = new Player((byte)0, team);
                player.x = (short)position.H;
                player.y = (short)position.V;
                player.vx = (short)1;
                player.vx = (short)1;

                byte[] responseBytes = client.sendPosition(player);
                GameoutClientHelper.updateGameState(responseBytes);
                responseFromServer = "";
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseFromServer;
        }
    }
    */
}
