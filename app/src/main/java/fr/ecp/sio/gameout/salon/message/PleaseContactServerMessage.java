package fr.ecp.sio.gameout.salon.message;

import com.google.android.gms.games.Game;

import fr.ecp.sio.gameout.model.GameInit;

/**
 * Created by od on 2/10/2016.
 */
public class PleaseContactServerMessage extends  Message {
    public static final String TAG = PleaseContactServerMessage.class.getSimpleName();
    private GameInit gameInit;

    public PleaseContactServerMessage(){}

    public PleaseContactServerMessage(GameInit gameInit){
        super(TAG);
        this.gameInit = gameInit;
    }

    public GameInit getGameInit() {
        return gameInit;
    }

    public void setGameInit(GameInit gameInit) {
        this.gameInit = gameInit;
    }
}
