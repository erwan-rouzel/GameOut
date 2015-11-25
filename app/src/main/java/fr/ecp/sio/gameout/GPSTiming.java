package fr.ecp.sio.gameout;

/**
 * Created by od on 11/19/2015.
 * Calcul de la fréquence de rafraichissement des évènements GPS
 */
public class GPSTiming
{
    private static long cpt;
    private static long firstDateMillis;
    private static long lastDateMillis;

    static public void resetStatEvents()
    {
        cpt = 0;
    }

    static public void addEvent()
    {
        cpt++;
        lastDateMillis = System.currentTimeMillis();
        if (cpt <= 1)
            firstDateMillis = lastDateMillis;
    }

    static public long nbEvents()
    {
        return (cpt);
    }

    static public int meanPeriod()
    {
        int res;
        if (cpt < 2)
            res = 1200; // 1,2 seconde comme valeur par défaut
        else
        {
            res =(int) ((lastDateMillis - firstDateMillis)/(cpt-1));
            // Normal values are between 800 to 1600 ms, we will excludes values far from these ones
            if (res < 100)
                res = 100;

            if (res > 2300)
                    res = 2300;
        }
        return (res);
    }
}
