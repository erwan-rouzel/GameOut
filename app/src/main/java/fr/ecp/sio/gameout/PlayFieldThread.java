package fr.ecp.sio.gameout;

import android.graphics.Canvas;
import android.util.Log;

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
        PlayFieldPos.ThreadTraffic = 'R';
    }

    public void setPlayFieldPos (PlayFieldPos pPlayFieldPos)
    {
        mPfp = pPlayFieldPos;
    }

    @Override
    public void run()
    {
        Canvas lCanvas;

        while(PlayFieldPos.ThreadTraffic != 'V')
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Log.v("Pfsv thread", "Pb dans de slip rouge");
            }
        }

        PlayFieldPos.ThreadTraffic = 'J';
        while(PlayFieldPos.ThreadTraffic == 'J')
        {
            try
            {
                Thread.sleep(100);
                lCanvas = mPfsv.getHolder().lockCanvas();

                if(lCanvas != null)
                {
                    synchronized (mPfsv.getHolder())
                    {
                        mPfsv.maj_visu(lCanvas, true);
                    }
                    mPfsv.getHolder().unlockCanvasAndPost(lCanvas);
                }
            }
            catch (InterruptedException ex)
            {
                Log.v("Pfsv thread","Pb dans le slip vert");
            }
        }

        if (PlayFieldPos.ThreadTraffic != 'O')
            Log.v("pfsv thread", "Should be Orange before stop");
        PlayFieldPos.ThreadTraffic = 'R';
    }
}
