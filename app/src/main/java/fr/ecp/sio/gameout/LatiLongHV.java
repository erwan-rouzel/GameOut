package fr.ecp.sio.gameout;

/**
 * Created by Olivier Desté on 11/5/2015.
 * Convertion de la latitude et la longitude dans une grille très locale entre 0 et WIDTH_REF-1
 */

import android.location.Location;

public class LatiLongHV
{
    private static Location mBackLeftCorner;
    private static Location mBackRightCorner;
    private static double mBLLongi, mBRLongi;
    private static double mBLLatit, mBRLatit;
    private static double invCosLat; // 1/cos de la latitude.

    public static void setBackLeftCorner(Location pLoc)
    {
        mBackLeftCorner = pLoc;
        mBLLatit = mBackLeftCorner.getLatitude();
        mBLLongi = mBackLeftCorner.getLongitude();
        double c = Math.cos(Math.toRadians(mBLLatit));
        if (c<= 0.02) c = 0.02; // Ne fonctionne pas prèt des pôles
        invCosLat = 1./c;
    }

    public static void setBackRightCorner(Location pLoc)
    {
        mBackRightCorner = pLoc;
        mBRLatit = mBackRightCorner.getLatitude ();
        mBRLongi = mBackRightCorner.getLongitude();
        double c = Math.cos(Math.toRadians(mBRLatit));
        if (c<= 0.02) c = 0.02; // Ne fonctionne pas prèt des pôles
        invCosLat = 1./c;
    }

    public static double distBackLeftCorner (Location pLoc) // Distance en mètre
    {
        double latit = pLoc.getLatitude();
        double longi = pLoc.getLongitude();
        double res;
        res =  1./invCosLat;
        res *= mBLLongi - longi;
        res *= res;
        res += (mBLLatit - latit)*(mBLLatit - latit);
        return 111111.*Math.sqrt(res);
    }

    public static double distBackRightCorner (Location pLoc) // Distance en mètre
    {
        double latit= pLoc.getLatitude();
        double longi = pLoc.getLongitude();
        double res;
        res =  1./invCosLat;
        res *= mBRLongi - longi;
        res *= res;
        res += (mBRLatit - latit)*(mBRLatit - latit);
        return 111111.*Math.sqrt(res);
    }

    public static int distBackCorners () // Distance en mètres
    {
        double res;
        res =  1./invCosLat;
        res *= mBLLongi - mBRLongi;
        res *= res;
        res += (mBLLatit - mBRLatit)*(mBLLatit - mBRLatit);
        return (int) Math.round(111111.*Math.sqrt(res));
    }

    static public HVPoint convertLocToHV (Location pLoc)
    {
        double x,x1, y1, x2, y2, xp, yp, xr, yr, l2, xc, yc;
        HVPoint res = new HVPoint();

        //TODO mettre le code qui suit en mémoire à la calibration
        y1 = mBLLatit;
        y2 = mBRLatit;

        x1 = mBLLongi * invCosLat;
        x2 = mBRLongi * invCosLat;

        xr = x2-x1;
        yr = y2-y1;
        l2 =xr*xr + yr*yr;
        // Fin du code à mettre en mémoire lors de la calibration

        yp = pLoc.getLatitude();
        xp = pLoc.getLongitude() * invCosLat;

        xc = xp - x1;
        yc = yp - y1;

        x = (xc*xr + yc*yr)/l2; /* Produit scalaire avec normalisation => projection */
        x = (x < 0) ? 0 : (x > 1) ? 1 : x;
        if (x<0)
            res.H = -1;
        else if (x>1)
            res.H = HVPoint.WIDTH_REF;
        else
            res.H = (int) Math.round(x*HVPoint.WIDTH_REF-1);

        res.V = (HVPoint.WIDTH_REF/10)*8;

        return res;
    }
}
