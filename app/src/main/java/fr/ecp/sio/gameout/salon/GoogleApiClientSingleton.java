package fr.ecp.sio.gameout.salon;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Created by cquenum on 15/01/16.
 */
public class GoogleApiClientSingleton {

    private static final String TAG = GoogleApiClientSingleton.class.getSimpleName();

    private  GoogleApiClient mGoogleApiClient = null;

    private static GoogleApiClientSingleton ourInstance = new GoogleApiClientSingleton();

    public static GoogleApiClientSingleton getInstance() {
        Log.i(TAG, ": Returning the Api instance");
        if(ourInstance == null) {
            ourInstance = new GoogleApiClientSingleton();
        }

        return ourInstance;
    }

    private GoogleApiClientSingleton() {
    }

    public  GoogleApiClient getApiClient(){
        Log.i(TAG,": Returning the Api client");
        return mGoogleApiClient;
    }

    public  void setApiClient(GoogleApiClient apiClient){
        Log.i(TAG,": Setting the Api client");
        mGoogleApiClient = apiClient;
    }

    public  boolean isSignedIn() {
        Log.i(TAG,": Is the Api connected?");
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public  void signOut(){
        Log.i(TAG,": Sign out");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }
}
