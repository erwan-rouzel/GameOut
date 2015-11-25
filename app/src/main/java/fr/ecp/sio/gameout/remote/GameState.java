package fr.ecp.sio.gameout.remote;

/**
 * Created by erwan on 14/11/2015.
 */
public class GameState {
    public int id;
    public Ball ball;
    public Team[] teams;

    public GameState(GameSession session) {
        this.id = session.id;
        this.ball = new Ball();
        this.teams = new  Team[3];
        this.teams[0] = new Team(this, session.numberOfPlayersInTeam1);
        this.teams[1] = new Team(this, session.numberOfPlayersInTeam2);
        this.teams[2] = new Team(this, session.numberOfPlayersInTeam3);
    }
}