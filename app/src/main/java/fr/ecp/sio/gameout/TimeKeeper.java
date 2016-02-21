package fr.ecp.sio.gameout;

/**
 * Created by od on 11/19/2015.
 * Calcul de la fréquence de rafraichissement d'un type d'évènement.
 * 10 types d'évènements sont possible
 */
public class TimeKeeper
{
    private static long perioCpt[] = new long[10];
    private static long perioFirstDateMillis[] = new long[10];
    private static long perioLastDateMillis [] = new long[10];
    private static int  perioMin[] = new int[10];
    private static int  perioMax[] = new int[10];

    private static long duratStart[] = new long [10];
    private static long duratCpt[]   = new long [10];
    private static long duratCumul[] = new long [10];
    private static long duratMax[]   = new long [10];

    static private int boxed(int i)
    {
        return (i<0) ? 0 : (i>9) ? 9 : i;
    }

    static public void resetAllStat()
    {
        for (int i=0; i<10; i++)
        {
            resetPerio(i, 0, 54321);
            resetDurat(i);
        }
    }

    static public void resetDurat(int ind)
    {
        int i = boxed(ind);
        duratStart[i] = 0;
        duratCpt[i]   = 0;
        duratCumul[i] = 0;
        duratMax[i]   = -1;
    }

    static public void duratStartEvent(int ind)
    {
        int i = boxed(ind);
        duratStart[i] = System.currentTimeMillis();
    }

    static public void duratEndEvent(int ind)
    {
        int i = boxed(ind);
        long duration = System.currentTimeMillis() - duratStart[i];
        duratCumul[i] += duration;
        duratCpt[i]++;
        if (duration>duratMax[i])
            duratMax[i] = duration;
    }

    static public int meanDurat(int ind)
    {
        int i = boxed(ind);
        int res = -1;
        if (duratCpt[i]>0)
            res = (int) (1 + duratCumul [i] /duratCpt[i]);
        return(res);
    }

    static public int maxDurat(int ind)
    {
        int i = boxed(ind);
        int res = (int) duratMax[i];
        return(res);
    }

    static public void resetPerio(int ind)
    {
        int i = boxed(ind);
        perioCpt[i] = 0;
    }

    static public void resetPerio(int ind, int min, int max)
    {
        int i = boxed(ind);
        perioMin[i]=min;
        perioMax[i]=max;
        perioCpt[boxed(i)] = 0;
    }

    static public void addEvent(int ind)
    {
        int i = boxed(ind);
        perioCpt[i]++;
        perioLastDateMillis[i] = System.currentTimeMillis();
        if (perioCpt[i] <= 1)
            perioFirstDateMillis[i] = perioLastDateMillis[i];
    }

    static public long nbEvents(int ind)
    {
        return (perioCpt[boxed(ind)]);
    }

    static public int meanPeriod(int ind)
    {
        int i = boxed(ind);
        int res;
        if (perioCpt[i] < 2)
            res = 1+ perioMax[i]/2 + perioMin[i]/2;
        else
        {
            res = (int) ((perioLastDateMillis[i] - perioFirstDateMillis[i]) / (perioCpt[i]-1));
            if (res < perioMin[i])
                res = perioMin[i];

            if (res > perioMax[i])
                    res = perioMax[i];
        }
        return (res);
    }
}
