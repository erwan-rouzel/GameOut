package fr.ecp.sio.gameout.PlayField;

import android.util.Log;

import fr.ecp.sio.gameout.PlayField.PlayFieldPos;
import fr.ecp.sio.gameout.PlayField.PlayFieldSurfaceView;
import fr.ecp.sio.gameout.TimeKeeper;
import fr.ecp.sio.gameout.model.GameStatus;

/**
 * Created by od on 10/31/2015.
 * En charge de demander plusieurs fois par seconde le re-dessin de l'aire de jeu
 */
public class PlayFieldThread extends Thread
{
    private PlayFieldSurfaceView mPfsv=null;
    protected PlayFieldPos mPfp=null;

    public PlayFieldThread (PlayFieldSurfaceView lPlayFieldSurfaceView)
    {
        mPfsv = lPlayFieldSurfaceView;
    }

    public void setPlayFieldPos (PlayFieldPos pPlayFieldPos)
    {
        mPfp = pPlayFieldPos;
    }

    @Override
    public void run()
    {;
        while(CurPfp.pfp.gameStatus != GameStatus.FINISHED)
        {
            try
            {
                //TODO Revoir le sleep pour régler entre 50 et 125 selon l'heure courante
                Thread.sleep(125);
                TimeKeeper.duratStartEvent(0);
                mPfsv.maj_visu(true);
                TimeKeeper.duratEndEvent(0);
            }
            catch (InterruptedException ex)
            {
                Log.v("Pfsv thread","Pb dans le slip vert");
            }
        }

        Log.v("STATUS", " Game Over!");
    }
}
