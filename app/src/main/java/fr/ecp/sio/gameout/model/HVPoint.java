package fr.ecp.sio.gameout.model;

/**
 * Created by od on 11/6/2015.
 * Point défini par ces coordonnées horizontale et verticale dans un espace fictif representant
 * le terrain de jeu dans la largeur est défini dans la constante WIDTH_REF.
 */
public class HVPoint
{
    public final static int WIDTH_REF = 12000; // Largeur arbitraire du terrain
    public short H;
    public short V;

    public HVPoint(short H, short V) {
        this.H = H;
        this.V = V;
    }

    public HVPoint() {
        this.H = 0;
        this.V = 0;
    }
}
