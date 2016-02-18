package fr.ecp.sio.gameout;

/**
 * Created by od on 11/19/2015.
 * Calcul de la fréquence de rafraichissement d'un type d'évènement.
 * 10 types d'évènements sont possible
 */
public class TimeKeeper
{
    private static long cpt[] = new long[10];
    private static long firstDateMillis[] = new long[10];
    private static long lastDateMillis [] = new long[10];

    static private int boxed(int i)
    {
        return (i<0) ? 0 : (i>9) ? 9 : i;
    }

    static public void resetStat(int ind)
    {
        cpt[boxed(ind)] = 0;
    }

    static public void resetAllStat()
    {
        for (int i=0; i<10; i++)
            cpt[i]=0;
    }

    static public void addEvent(int ind)
    {
        int i = boxed(ind);
        cpt[i]++;
        lastDateMillis[i] = System.currentTimeMillis();
        if (cpt[i] <= 1)
            firstDateMillis[i] = lastDateMillis[i];
    }

    static public long nbEvents(int ind)
    {
        return (cpt[boxed(ind)]);
    }

    static public int meanPeriod(int ind)
    {
        int i = boxed(ind);
        int res;
        if (cpt[i] < 2)
            res = 1200; // 1,2 seconde comme valeur par défaut
        else
        {
            res =(int) ((lastDateMillis[i] - firstDateMillis[i])/(cpt[i]-1));
            // Normal values are between 800 to 1600 ms, we will excludes values far from these ones
            if (res < 100)
                res = 100;

            if (res > 2300)
                    res = 2300;
        }
        return (res);
    }
}
