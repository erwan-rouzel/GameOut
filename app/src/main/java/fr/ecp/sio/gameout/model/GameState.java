package fr.ecp.sio.gameout.model;

/**
 * Created by erwan on 14/11/2015.
 */
public class GameState extends GameObject {
    public GameSession session;
    public long timestamp;
    public int increment;
    public int id;
    public Ball ball;
    public Team[] teams;
    public byte status;

    public GameState(GameSession session) {
        this.session = session;
        this.id = session.id;
        this.ball = new Ball();
        this.teams = new  Team[3];

        if(session.numberOfPlayersInTeam1 > 0) {
            this.teams[0] = new Team((byte)0, this, session.numberOfPlayersInTeam1);
        }

        if(session.numberOfPlayersInTeam2 > 0) {
            this.teams[1] = new Team((byte)1, this, session.numberOfPlayersInTeam2);
        }

        if(session.numberOfPlayersInTeam3 > 0) {
            this.teams[2] = new Team((byte)2, this, session.numberOfPlayersInTeam3);
        }

        this.status = GameStatus.INITIALIZING;
    }
}