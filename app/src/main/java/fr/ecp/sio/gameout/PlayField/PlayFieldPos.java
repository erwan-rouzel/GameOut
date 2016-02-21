package fr.ecp.sio.gameout.PlayField;

import fr.ecp.sio.gameout.LocationManager;
import fr.ecp.sio.gameout.TimeKeeper;
import fr.ecp.sio.gameout.model.GameStatus;
import fr.ecp.sio.gameout.model.HVPoint;
import fr.ecp.sio.gameout.model.Player;
import fr.ecp.sio.gameout.model.Team;
import fr.ecp.sio.gameout.remote.RemoteGameState;

/**
 * Created by od on 10/31/2015.
 * Classe dont les instances décrive l'état instanté d'un jeu :
 * Score, position et vitesse des raquettes, position et vitesse de la balle.
 */
public class PlayFieldPos
{
    public long timeLGP; // date in ms of Last Network Update
    public long timeExt; // date in ms for witch the values had been extrapolated

    public byte[]  big_digit = new byte[3]; // Afficheur (score en particulier)

    public int      nb_team;   // 1 à 3 équipes, 1 à 3 joueurs par équipe
    public int []   nb_player; // Nb joueurs par équipe

    public int xPosPadLocalExt, yPosPadLocalExt; // Position extrapolée de la raquette du client
    public int xPosPadLocal, yPosPadLocal; // Position connue de la raquette du client
    public int xSpePadLocal, ySpePadLocal; // Vitesse de la raquette du client

    public int [][] xPosPadSrv, yPosPadSrv; // Position des raquettes recues du serveur
    public int [][] xRadPadSrv, yRadPadSrv; // Taille des raquettes [Equipe][Joueur]
    public int [][] xSpePadSrv, ySpePadSrv; // Vitesse des raquettes [Equipe][Joueur]
    public byte[][] statePadSrv; // Bits donnant l'état de la raquette (active ou pas, hors zone...)

    public int xPosBalSrv, yPosBalSrv;
    public int xRadBalSrv, yRadBalSrv;
    public int xSpeBalSrv, ySpeBalSrv; // distance per hour

    public static char ThreadTraffic='R'; // Gestion à l'ancienne de l'activité du thread
    private int bestScoreBid; // Meilleur score (temporaire)
    public boolean isGameStarted;
    public byte gameStatus;
    //TODO Supprimer l'utilisation de la variable bestScoreBid (Bid pour Bidon).
    private final Integer mutex = 0; // Use to deal with concurrent write on paddles positions or speeds


    PlayFieldPos() // initialisation des variables de classe représentant l'état du terrain
    {
        int e,j;
        xPosBalSrv = HVPoint.WIDTH_REF /2;   yPosBalSrv = HVPoint.WIDTH_REF / 2;
        xRadBalSrv = HVPoint.WIDTH_REF / 80; yRadBalSrv = HVPoint.WIDTH_REF / 80;
        xSpeBalSrv = 0; ySpeBalSrv = 0;
        timeLGP = -1;
        timeExt = -1;
        isGameStarted = false;
        gameStatus = GameStatus.INITIALIZING;

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

        xPosPadSrv = new int [nb_team][nb_player[0]]; //TODO revoir allocation memoire des paddles
        yPosPadSrv = new int [nb_team][nb_player[0]];
        xRadPadSrv = new int [nb_team][nb_player[0]]; //TODO Adapt aux equipes de taille différentes
        yRadPadSrv = new int [nb_team][nb_player[0]];
        xSpePadSrv = new int [nb_team][nb_player[0]];
        ySpePadSrv = new int [nb_team][nb_player[0]];
        statePadSrv= new byte[nb_team][nb_player[0]];

        for (e=0;e<nb_team;e++)
            for (j=0; j<nb_player[e]; j++)
            {
                xRadPadSrv  [e][j] = HVPoint.WIDTH_REF/16;
                yRadPadSrv  [e][j] = HVPoint.WIDTH_REF/80;

                xSpePadSrv  [e][j] = 0;
                ySpePadSrv  [e][j] = 0;

                statePadSrv [e][j] = (char) 1; // Active et pas hors zone
            }

        //TODO revoir les initialisations des positions,vitesses locales
        xPosPadLocalExt = 12;
        yPosPadLocalExt = 24; // Position extrapolée de la raquette du client
        xPosPadLocal = 13;
        yPosPadLocal = 25; // Position connue de la raquette du client
        xSpePadLocal = 0;
        xSpePadLocal = 0;
    }

    public void extrapolateLocal ()
    {
        long deltaTime, targetTime;

        synchronized (mutex)
        {
            targetTime = System.currentTimeMillis()+1; // We estimate that it will be used in 1 ms
            deltaTime = targetTime - timeLGP;
            xPosPadLocalExt = xPosPadLocal + (int) Math.round(xSpePadLocal/3600000.*deltaTime);
            yPosPadLocalExt = yPosPadLocal + (int) Math.round(ySpePadLocal/3600000.*deltaTime);
        }
    }

    // Fait sauter la raquette en un point précis. La raquette est gelée à l'arrêt.
    public void setPosPad0(HVPoint p)
    {
        synchronized (mutex)
        {
            long tCurrent = System.currentTimeMillis();
            xPosPadLocal = p.H;
            yPosPadLocal = p.V;

            xSpePadLocal = 0;
            ySpePadLocal = 0;

            xPosPadLocalExt = xPosPadLocal;
            yPosPadLocalExt = yPosPadLocal;

            timeLGP = tCurrent;
        }

    }

    // Fait sauter la raquette en un point précis. La raquette a un vecteur vitesse calculé en
    // fonction de l'avant dernière poisition.
    public void setPosPad1(HVPoint p)
    {
        //TODO appeler la version setPosPad1 en tout début de partie
        synchronized (mutex)
        {
            long tCurrent = System.currentTimeMillis();

            int  deltaH = p.H - xPosPadLocal;
            int  deltaV = p.V - yPosPadLocal;
            int  deltaT = (int) (tCurrent - CurPfp.pfp.timeLGP);
            if (deltaT<1)  // TODO logguer une erreur en cas de deltaT très court ou nul
                deltaT=1; // Pour éviter les divisions par zero

            xPosPadLocal = p.H;
            yPosPadLocal = p.V;

            xSpePadLocal = (int) Math.round(deltaH*3600000./deltaT);
            //ySpePadLocal = (int) Math.round(deltaV*3600000./deltaT);
            //TODO DEBUG, retirer cette ligne
            ySpePadLocal = 11;

            xPosPadLocalExt = xPosPadLocal;
            yPosPadLocalExt = yPosPadLocal;

            timeLGP = tCurrent;
        }
    }

    // Ne fait pas sauter la raquette, mais modifie le vecteur vitesse avec comme cible
    // le point auquel est prédit la position de la raquette dans un futur proche, c.a.d lors de
    // la reception de la prochaine position. Param entre 0 et 1. Essayez 0.6
    public void setPosPad2(HVPoint p, float coef)
    {
        synchronized (mutex)
        {
            long tPrev = timeLGP; // La date de l'étape n-1 est celle de la Last Network Update
            long tCurrent = System.currentTimeMillis(); // la date de l'étape n
            long tNext  = tCurrent + TimeKeeper.meanPeriod(0); // La date de l'étape n+1

            int  deltaH = p.H - xPosPadLocalExt; // Delta entre position extrapolée et la position
            int  deltaV = p.V - yPosPadLocalExt; // souhaitée à l'instant n.

            int  HNext = Math.round(p.H + coef * deltaH * (tNext-tCurrent) / (float) (tCurrent-tPrev));
            int  VNext = Math.round(p.V + coef * deltaV * (tNext-tCurrent) / (float) (tCurrent-tPrev));

            // Big risk of overshoot with big move, fall back to a more jumpy and
            // less smooth move.
            if (Math.abs(deltaH)+Math.abs(deltaV) > (HVPoint.WIDTH_REF/4))
                setPosPad0(p);
            else
            {
                xPosPadLocal = xPosPadLocalExt;
                yPosPadLocal = yPosPadLocalExt;

                xSpePadLocal = (int) Math.round((HNext-xPosPadLocal)*3600000./(tNext-tCurrent));
                //ySpePadLocal = (int) Math.round((VNext-yPosPadLocal)*3600000./(tNext-tCurrent));
                //TODO DEBUG, retirer cette ligne
                ySpePadLocal = 22;
            }
            timeLGP = tCurrent;
        }
    }

    public int scoreBidon()
    {
        return(big_digit[0]*100 + big_digit[1]*10 + big_digit[2]);
    }

    public int bestScoreBidon()
    {
        return (bestScoreBid);
    }

    public void syncGameState() {
        //TODO ne plus appeler getInstance. ER ET OD
        RemoteGameState gameState = RemoteGameState.getInstance();
        LocationManager locationManager = LocationManager.getInstance();

        if(gameState == null) return;
        //if (gameState.status == GameStatus.INITIALIZING) return;

        //Envoyer position de la raquette du joueur
        gameState.sendPosition(locationManager.getCurrentPosition());

        // Mapping entre RemoteGameState et PlayFieldPos
        xPosBalSrv = gameState.ball.x;
        yPosBalSrv = gameState.ball.y;
        synchronized (mutex)
        {
            for(Team team: gameState.teams) {
                if(team != null) {
                    this.big_digit[team.id] = team.score;

                    for (Player player : team.players) {
                        if (player != null) {
                            xPosPadSrv  [team.id][player.id] = gameState.teams[team.id].players[player.id].x;
                            yPosPadSrv  [team.id][player.id] = gameState.teams[team.id].players[player.id].y;
                            statePadSrv [team.id][player.id] = gameState.teams[team.id].players[player.id].state;
                        }
                    }
                }
            }
        }

        this.gameStatus = gameState.status;
        big_digit[0] = gameState.teams[0].score;
        //TODO Demander au serveur de toujours renvoyer les 3 gros chiffres
//        big_digit[1] = gameState.teams[1].score;
//        big_digit[2] = gameState.teams[2].score;
    }
}
