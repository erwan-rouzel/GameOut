package fr.ecp.sio.gameout;

/**
 * Created by od on 11/20/2015.
 */
public class RemoteGameState
{
    public int nbTeam;
    //poisition de la balle, sa vitesse, timeStamp

    RemoteGameState (int iDTeam, int iDPlayer) // constructeur
    {

    }

    public void sendPosition(HVPoint p)
    {

    }

    public void getNewState() // Fonction bloquante en attente des nouvelles position.
    {
      // une reception UDP
        // Mise à jour des attributs de l'objet
    }
}
