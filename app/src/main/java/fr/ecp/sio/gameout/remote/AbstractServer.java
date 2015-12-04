package fr.ecp.sio.gameout.remote;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by erwan on 21/11/2015.
 */
public abstract class AbstractServer {
    public static final String TAG = AbstractServer.class.getName();
    public static final Logger LOG = Logger.getLogger(TAG);
    protected int port;

    public AbstractServer(int port) {
        this.port = port;
    }

    protected void log(String message) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Log.d("[" + sdf.format(now) + "]-[" + this.getClass().getSimpleName() + "] ",  message);
    }

    protected void log(Exception exception) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Log.d("[" + sdf.format(now) + "]-[" + this.getClass().getSimpleName() + "] ", exception.toString());
        exception.printStackTrace();
    }
}