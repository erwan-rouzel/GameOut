package fr.ecp.sio.gameout.model;

/**
 * Created by erwan on 14/11/2015.
 */
public class Team {
    public byte id;
    public GameState parentGameState;
    public Player[] players;
    public byte score;

    public Team(GameState parentGameState, int numberOfPlayers) {
        this.parentGameState = parentGameState;
        this.players = new Player[numberOfPlayers];

        for(int i = 0; i < numberOfPlayers; i++) {
            this.players[i] = new Player(this);
        }
    }
}