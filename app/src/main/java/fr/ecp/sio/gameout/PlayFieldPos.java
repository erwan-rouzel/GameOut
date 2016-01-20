package fr.ecp.sio.gameout;

/**
 * Created by od on 10/31/2015.
 * Classe dont les instances décrive l'état instanté d'un jeu :
 * Score, position et vitesse des raquettes, position et vitesse de la balle.
 */
public class PlayFieldPos
{
    public long timeLNU; // date in ms of Last Network Update
    public long timeExt; // date in ms for witch the values had been extrapolated

    public char[]  big_digit = new char[3]; // Afficheur (score en particulier)

    public int      nb_team;   // 1 à 3 équipes, 1 à 3 joueurs par équipe
    public int []   nb_player; // Nb joueurs par équipe
    public int [][] xPosPadExt, yPosPadExt; // Position des raquettes Extrapolée à une date recente
    public int [][] xPosPad,    yPosPad; // Position des raquettes [Equipe][Joueur]
    public int [][] xRadPad,    yRadPad; // Taille des raquettes [Equipe][Joueur]
    public int [][] xSpePad,    ySpePad; // Vitesse des raquettes [Equipe][Joueur]
    public char[][] statePad; // Bits donnant l'état de la raquette (active ou pas, hors zone...)

    public int xPosBal, yPosBal;
    public int xRadBal, yRadBal;
    public int xSpeBal, ySpeBal; // distance per hour

    public static char ThreadTraffic='R'; // Gestion à l'ancienne de l'activité du thread
    private int bestScoreBid; // Meilleur score (temporaire)
    //TODO Supprimer l'utilisation de la variable bestScoreBid (Bid pour Bidon).



    PlayFieldPos() // initialisation des variables de classe représentant l'état du terrain
    {
        int e,j;
        xPosBal = HVPoint.WIDTH_REF /2;   yPosBal = HVPoint.WIDTH_REF / 2;
        xRadBal = HVPoint.WIDTH_REF / 80; yRadBal = HVPoint.WIDTH_REF / 80;
        xSpeBal = 0; ySpeBal = 0;
        timeLNU = -1;
        timeExt = -1;

        bestScoreBid = 0;

        for (e=0; e<3; e++)
        {
            big_digit[e] = 0;
        }

        nb_team = 1;
        nb_player = new int[nb_team];

        for (e=0;e<nb_team;e++)
        {
            nb_player[e] = 1;
        }

        xPosPadExt = new int [nb_team][nb_player[0]]; //TODO revoir allocation memoire des paddles
        yPosPadExt = new int [nb_team][nb_player[0]];
        xPosPad    = new int [nb_team][nb_player[0]];
        yPosPad    = new int [nb_team][nb_player[0]];
        xRadPad    = new int [nb_team][nb_player[0]]; //TODO Adapt aux equipes de taille différentes
        yRadPad    = new int [nb_team][nb_player[0]];
        xSpePad    = new int [nb_team][nb_player[0]];
        ySpePad    = new int [nb_team][nb_player[0]];
        statePad   = new char[nb_team][nb_player[0]];

        for (e=0;e<nb_team;e++)
            for (j=0; j<nb_player[e]; j++)
            {
                xRadPad    [e][j] = HVPoint.WIDTH_REF/16;
                yRadPad    [e][j] = HVPoint.WIDTH_REF/80;

                xPosPadExt [e][j] = xPosPad [e][j];
                yPosPadExt [e][j] = yPosPad [e][j];

                xSpePad    [e][j] = 0;
                ySpePad    [e][j] = 0;

                statePad   [e][j] = (char) 1; // Active et pas hors zone
            }
    }

    public void extrapolate ()
    {
        int e,j;
        long deltaTime, targetTime;

        targetTime = System.currentTimeMillis()+1; // We estimate that it will be used in 1 ms
        deltaTime = targetTime - timeLNU;

        for (e=0;e<nb_team;e++)
            for (j=0; j<nb_player[e]; j++)
            {
                xPosPadExt [e][j] = xPosPad[e][j]
                                   + (int) Math.round(xSpePad[e][j]/3600000.*deltaTime);
                yPosPadExt [e][j] = yPosPad[e][j]
                                   + (int) Math.round(ySpePad[e][j]/3600000.*deltaTime);
            }
    }

    // Fait sauter la raquette en un point précis. La raquette est gelée à l'arrêt.
    public void setPosPad0(int e, int j, HVPoint p)
    {
        long tCurrent = System.currentTimeMillis();

        xPosPad[e][j] = p.H;
        yPosPad[e][j] = p.V;

        xSpePad[e][j] = 0;
        ySpePad[e][j] = 0;

        xPosPadExt[e][j] = xSpePad[e][j];
        yPosPadExt[e][j] = ySpePad[e][j];

        timeLNU = tCurrent;
    }

    // Fait sauter la raquette en un point précis. La raquette a un vecteur vitesse calculé en
    // fonction de l'avant dernière poisition.
    public void setPosPad1(int e, int j, HVPoint p)
    {
        long tCurrent = System.currentTimeMillis();

        int  deltaH = p.H - xPosPad[e][j];
        int  deltaV = p.V - yPosPad[e][j];
        int  deltaT = (int) (tCurrent - CurPfp.pfp.timeLNU);
        if (deltaT<1)  // tODO logguer une erreur en cas de deltaT très court ou nul
            deltaT=1; // Pour éviter les divisions par zero

        xPosPad[e][j] = p.H;
        yPosPad[e][j] = p.V;

        xSpePad[e][j] = (int) Math.round(deltaH*3600000./deltaT);
        ySpePad[e][j] = (int) Math.round(deltaV*3600000./deltaT);

        xPosPadExt[e][j] = xSpePad[e][j];
        yPosPadExt[e][j] = ySpePad[e][j];

        timeLNU = tCurrent;
    }

    // Ne fait pas sauter la raquette, mais modifie le vecteur vitesse avec comme cible
    // le point auquel est prédit la position de la raquette dans un futur proche, c.a.d lors de
    // la reception de la prochaine position. Param entre 0 et 1. Essayez 0.6
    public void setPosPad2(int e, int j, HVPoint p, float coef)
    {
        long tPrev = timeLNU; // La date de l'étape n-1 est celle de la Last Network Update
        long tCurrent = System.currentTimeMillis(); // la date de l'étape n
        long tNext  = tCurrent + GPSTiming.meanPeriod(); // La date de l'étape n+1

        int  deltaH = p.H - xPosPadExt[e][j]; // Delta entre position extrapolée et la position sou
        int  deltaV = p.V - yPosPadExt[e][j];// haitée à l'instant n.

        int  HNext = Math.round(p.H + coef * deltaH * (tNext-tCurrent) / (float) (tCurrent-tPrev));
        int  VNext = Math.round(p.V + coef * deltaV * (tNext-tCurrent) / (float) (tCurrent-tPrev));

        // Big risk of overshoot with big move, fall back to a more jumpy and
        // less smooth move.
        if (Math.abs(deltaH)+Math.abs(deltaV) > (HVPoint.WIDTH_REF/4))
            setPosPad1(e,j,p);
        else
        {
            xPosPad[e][j] = xPosPadExt[e][j];
            yPosPad[e][j] = yPosPadExt[e][j];

            xSpePad[e][j] = (int) Math.round((HNext-xPosPad[e][j])*3600000./(tNext-tCurrent));
            ySpePad[e][j] = (int) Math.round((VNext-yPosPad[e][j])*3600000./(tNext-tCurrent));
        }
        timeLNU = tCurrent;
    }

    public void resetScoreBidon()
    {
        int e;
        for (e=0; e<3; e++)
        {
            big_digit[e] = 0;
        }
    }

    public int scoreBidon()
    {
        return(big_digit[0]*100 + big_digit[1]*10 + big_digit[2]);
    }

    public void incScoreBidon()
    {
        big_digit [2] ++;
        if (big_digit[2] > 9)
        {
            big_digit[2] = 0;
            big_digit[1]++;
        }
        if (big_digit[1] > 9)
        {
            big_digit[1] = 0;
            big_digit[0]++;
        }

        int score = scoreBidon();
        if (score>bestScoreBid)
            bestScoreBid = score;
    }

    public int bestScoreBidon()
    {
        return (bestScoreBid);
    }

    public void balleMiseEnJeuRep() // Mise en jeu répétable de la balle (toujours pareil)
    {
        // Mise en jeu de la balle au centre. Vitesse adaptée au terrain.
        //TODO appeler la fonction balleRegleVitesse pour fixer la vitesse
        ySpeBal = -HVPoint.WIDTH_REF*23456 / LatiLongHV.distBackCorners();
        xSpeBal = -ySpeBal / 4; // distance per hour
        xPosBal =  HVPoint.WIDTH_REF/2;
        yPosBal =  HVPoint.WIDTH_REF/2;
    }

    public void balleAuCentre()
    {
        // Mise en jeu de la balle au centre. Vitesse nulle.
        xSpeBal =  0;
        ySpeBal =  0;
        xPosBal =  HVPoint.WIDTH_REF/2;
        yPosBal =  HVPoint.WIDTH_REF/2;
    }

    public void balleRegleVitesse()
    {
        int   score = scoreBidon();
        float coefScore = score/80.f+1.f;
        float signX = (xSpeBal>0) ? 1.f : -1f;
        float signY = (ySpeBal>0) ? 1.f : -1f;
        float baseY = HVPoint.WIDTH_REF*23456.f / LatiLongHV.distBackCorners();
        float baseX = baseY / 4.f;
        xSpeBal = Math.round(signX*coefScore*baseX); // distance per hour
        ySpeBal = Math.round(signY*coefScore*baseY); // distance per hour
    }
}
