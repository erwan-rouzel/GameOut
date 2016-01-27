/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License atlastMovxplusDy
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ODE : look at https://mikeyhogarth.wordpress.com/2010/10/09/how-to-develop-pong-for-android/
 * TODO que ce passe-t-il si l'on presse calib button juste avant le premier fix ?
 * TODO finir d implementer la methode onSaveInstanceState
 * TODO vérifier que les Object Lock sont bien utilisé partout où cela est nécessaire
 * TODO Faut il ajouter un "implements SurfaceHolder.Callback" ?
 * TODO Faut il ajouter des tests de ré-entrance pour les cas où ca va trop vite ?
 */
package fr.ecp.sio.gameout;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import fr.ecp.sio.gameout.model.GameSession;
import fr.ecp.sio.gameout.model.HVPoint;
import fr.ecp.sio.gameout.remote.RemoteGameState;
import fr.ecp.sio.gameout.remote.SyncStateService;

/**
 * Out door game mixing the fun of mobile games, retro gaming and fitness activity.
 * Initial team :
 * - Erwan Rouzel : Server and cloud computing
 * - Codjo Quenum : Google Play, advertisement funding
 * - Olivier Desté : intial idea and Android dev
 *
 */
public class GameActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    InterstitialAd mInterstitialAd;
    protected char balTraffic = 'N';
    //private   BallThread balThread = null;

    private long tUpdBal = -1; //Date of the last update of the ball in milliseconds
    private long tInitBal = -1;  // Date of first update of the ball for debug
    private long nbMajBal = 0;   // Number of updates since tInitBal for debug

    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 600;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // TODO Revoir l'utilisation du Bundle et les données qui y sont enregistrées
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi (GPS+).
     *
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location on earth.
     */
    protected Location mCurrentLocation = null;

    // UI Widgets.
    protected ToggleButton mOnOffSwitch;
    protected Button mCalibButton;
    protected Button mParamTestButton;

    protected String mInfoString; // Pour le debug
    protected TextView mInfoTextView; // Affichage pour le debug
    protected TextView mScoreTextView; // Affichage temporaire du score
    protected TextView mBestScoreTextView; // Affichage temporaire du meilleur score

    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    EditText mLogServerEditText;


    //Le texte indiquant à l'utilisateur le prochain point de calibration à acquérir
    String calibText="vide";
    int calibStage=0; // Numéro de l'étape de calibration
    String paramTestText;
    int paramTest;

    /*
    protected class BallThread extends Thread
    {
        public void run()
        {
            int xMov, yMov;
            long tCurrent;
            int  tDelta;

            do
            {
                try
                {
                    Thread.sleep(400);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            while ((balTraffic == 'R') || (balTraffic == 'N'));

            balTraffic = 'J';
            while(balTraffic == 'J')
            {
                try
                {
                    Thread.sleep(40);
                    tCurrent = System.currentTimeMillis();
                    tDelta   = (int) (tCurrent - tUpdBal);
                    if (tDelta==0)
                    {
                        Log.v("Ball","null delta time");
                        tDelta = 1;
                    }
                    nbMajBal++;
                    if (tInitBal<0)
                        tInitBal = tCurrent;

                    // Mouvement sur X
                    xMov = (int) Math.round(CurPfp.pfp.xSpeBal/3600000.*tDelta);
                    CurPfp.pfp.xPosBal += xMov;

                    // Rebond en X sur les murs
                    if (CurPfp.pfp.xPosBal < CurPfp.pfp.xRadBal)
                        CurPfp.pfp.xSpeBal = (short) Math.abs(CurPfp.pfp.xSpeBal);
                    else
                    if (CurPfp.pfp.xPosBal>(HVPoint.WIDTH_REF-CurPfp.pfp.xRadBal))
                        CurPfp.pfp.xSpeBal = (short) - Math.abs(CurPfp.pfp.xSpeBal);

                    // Mouvement sur Y
                    yMov = (int) Math.round(CurPfp.pfp.ySpeBal/3600000.*tDelta);
                    CurPfp.pfp.yPosBal += yMov;

                    // Rebond en Y sur les murs
                    if (CurPfp.pfp.yPosBal < CurPfp.pfp.yRadBal)
                        CurPfp.pfp.ySpeBal = (short) Math.abs(CurPfp.pfp.ySpeBal);
                    else
                    if (CurPfp.pfp.yPosBal>(HVPoint.WIDTH_REF-CurPfp.pfp.yRadBal))
                    // Cas special en plus nous remettons le score à 0 la balle a été loupée
                    {
                        CurPfp.pfp.ySpeBal = (short) - Math.abs(CurPfp.pfp.ySpeBal);
                        CurPfp.pfp.resetScoreBidon();
                        CurPfp.pfp.balleRegleVitesse();
                    }

                    // Rebond sur la raquette
                    if ( ! (
                            ( CurPfp.pfp.ySpeBal < 0 )
                                    || ((CurPfp.pfp.yPosBal + CurPfp.pfp.yRadBal) < (CurPfp.pfp.yPosPadExt[0][0] - CurPfp.pfp.yRadPad[0][0]))
                                    || ((CurPfp.pfp.yPosBal - CurPfp.pfp.yRadBal) > (CurPfp.pfp.yPosPadExt[0][0] + CurPfp.pfp.yRadPad[0][0]))
                                    || ((CurPfp.pfp.xPosBal + CurPfp.pfp.xRadBal) < (CurPfp.pfp.xPosPadExt[0][0] - CurPfp.pfp.xRadPad[0][0]))
                                    || ((CurPfp.pfp.xPosBal - CurPfp.pfp.xRadBal) > (CurPfp.pfp.xPosPadExt[0][0] + CurPfp.pfp.xRadPad[0][0]))
                        )   )
                    {
                        CurPfp.pfp.ySpeBal = (short) -Math.abs(CurPfp.pfp.ySpeBal);
                        // incrementons le score, la balle a été envoyée
                        CurPfp.pfp.incScoreBidon();
                        // agmentons la vitesse de la balle lorsque le score augmente
                        CurPfp.pfp.balleRegleVitesse();
                    }
                    tUpdBal = tCurrent;
                }
                catch (InterruptedException ex)
                {
                    Log.v("Bal","Pb during ball thread mvt calculations");
                }
            }

            if (balTraffic != 'O')
                Log.v("Bal","Should be Orange before stop");
            balTraffic = 'R';
        }
    }
    */

    /*
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        PlayFieldSurfaceView lPfsv;

        super.onCreate(savedInstanceState);
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-2963674502359443/7565389212");
        requestNewInterstitial();
        if (mInterstitialAd != null && mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        setContentView(R.layout.game_activity);

        // Locate the UI widgets.
        mOnOffSwitch = (ToggleButton) findViewById(R.id.toggle_gps_button);
        mCalibButton = (Button)  findViewById(R.id.calib_button);
        mParamTestButton = (Button) findViewById(R.id.param_test_button);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);

        mScoreTextView = (TextView) findViewById(R.id.text_view_score);
        mBestScoreTextView = (TextView) findViewById(R.id.text_view_best_score);
        mInfoTextView  = (TextView) findViewById(R.id.text_delta_view);

        mRequestingLocationUpdates = false;
        calibStage= 0;
        paramTest = 3;
        // Update values using data stored in the Bundle.
        // TODO understand why next line meaning
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        updateMyButtons();
        GPSTiming.resetStatEvents();

        Intent mServiceIntent = new Intent(this, SyncStateService.class);
        this.startService(mServiceIntent);
    }

    /**
     * Updates fields based on data stored in the   bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                updateMyButtons();
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /*
     * Handles the start/stop switch
     */

    public void toggleButtonHandler(View view)
    {
        Boolean pIsChecked = ((ToggleButton) view).isChecked();
        if (pIsChecked)
        {
            if (!mRequestingLocationUpdates)
            {
                calibStage=0;
                CurPfp.pfp.balleAuCentre();
                tUpdBal = System.currentTimeMillis();
                mRequestingLocationUpdates = true;
                mCalibButton.setEnabled(true);
                paramTest = 3;
                startLocationUpdates();
            }
        }
        else
        {
            if (mRequestingLocationUpdates)
            {
                calibStage=0;
                CurPfp.pfp.balleAuCentre();
                tUpdBal = System.currentTimeMillis();
                mRequestingLocationUpdates = false;
                mCalibButton.setEnabled(true);
                stopLocationUpdates();
                GPSTiming.resetStatEvents();
            }
        }
        updateMyButtons();
    }

    /**
     * Handles the Set Calib button.
     */
    public void calibButtonHandler(View view)
    {
        if (mRequestingLocationUpdates)
        {
            switch (calibStage)
            {
                case 0:
                    LatiLongHV.setBackLeftCorner(mCurrentLocation);
                    mCalibButton.setEnabled(true); // Inutile
                  break;

                case 1:
                    LatiLongHV.setBackRightCorner(mCurrentLocation);
                    mCalibButton.setEnabled(false);
                    
                    // C'est le moment de mettre la balle en jeu.
                    CurPfp.pfp.balleMiseEnJeuRep();
                    tUpdBal = System.currentTimeMillis();

                    tInitBal   = -1;
                    nbMajBal   = 0;

                    //TODO : envoyer la taille du terrain au serveur
                    // pour le calcul de la vitesse de la balle

                    /*
                    if (balThread == null)
                    {
                        balTraffic = 'R';
                        balThread = new BallThread();
                        balThread.start();
                    }

                    if (balTraffic == 'R')
                        balTraffic = 'V';
                    else
                        Log.v("Bal", "Bad ball restart condition");
                    */

                    break;

                default:                    // Normalement ce code n'est pas utilisé
                    mCalibButton.setEnabled(false);
                    Log.v("Calib","cas non prévu");
            }
        }
        calibStage++;
        updateMyButtons();
    }

    /**
     * Handles the Set Calib button.
     */
    public void paramTestButtonHandler(View view) throws IOException, ExecutionException, InterruptedException {
        paramTest = (paramTest+1)%8;
        paramTestText = String.format("%2d",paramTest);
        mParamTestButton.setText(paramTestText);
        //TODO ERWAN ajouter test serveur
        mLogServerEditText = (EditText) this.findViewById(R.id.edit_text_log_server);
        mLogServerEditText.setTextSize(8.0f);
        mLogServerEditText.setMinWidth(400);

        Random random = new Random();

        GameSession gameSession = new GameSession();
        gameSession.id = random.nextInt(10000);
        gameSession.numberOfPlayersInTeam1 = 1;
        gameSession.numberOfPlayersInTeam2 = 0;
        gameSession.numberOfPlayersInTeam3 = 0;

        HVPoint pt = new HVPoint((short)6000, (short)10000);
        LocationManager.getInstance().setPosition(pt);

        long startTime = System.nanoTime();
        RemoteGameState.getInstance(gameSession).sendPosition(pt);
        long endTime = System.nanoTime();
        double difference = Math.round((endTime - startTime)/1e6);

        RemoteGameState remoteGameState = RemoteGameState.getInstance(gameSession);

        logServer("ball=(" + remoteGameState.ball.x + ", " + remoteGameState.ball.y + ")");

        /*
        new Thread()
        {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    CurPfp.pfp.syncGameState();
                }
            }
        }.start();
        */
    }

    private void logServer(String message) {
        mLogServerEditText.setText(mLogServerEditText.getText() + "[" + RemoteGameState.getInstance().timestamp + "] " + message + "\n");
        mLogServerEditText.setSelection(mLogServerEditText.getText().length());
    }

    /**
     * The Start Updates button is enabled if the user is not requesting location updates.
     * The Stop Updates button is enabled if the user is requesting location updates.
     * The Calib button is enabled if the user is requesting location updates.
     * TODO Enable calib button only when an recent currentLocation is available
     */
    private void updateMyButtons()
    {
        if (mRequestingLocationUpdates)
        {
            switch (calibStage)
            {
                case 0:
                    calibText = "BL";
                    break;
                case 1:
                    calibText = "BR";
                    break;
                case 2:
                    calibText = "Done";
                    break;
                default:
                    calibText = "Bug ?";
            }
        }
        else
        {
            calibText = "...";
        }
        mCalibButton.setText(calibText);
        paramTestText = String.format("%2d",paramTest);
        mParamTestButton.setText(paramTestText);
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI()
    {
        if (mCurrentLocation == null)
        {
            mInfoString = "No current coord";
            mInfoTextView.setText(mInfoString);
        }
        else
        {
            mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));

            switch (calibStage)
            {
                case 0:
                    mInfoString = "V151120c No calib "  + " G=" + GPSTiming.nbEvents();
                    break;

                case 1:
                    double dist = LatiLongHV.distBackLeftCorner(mCurrentLocation);

                    // Ball begin to move from center
                    tInitBal   = -1;
                    nbMajBal=  0;

                    mInfoString = "dist="+ String.format("%.2f",dist) + "m.";
                    mInfoString += (dist>45)?" OK":" Allez plus loin";
                    break;

                case 2:
                    HVPoint p = LatiLongHV.convertLocToHV(mCurrentLocation);
                    switch (paramTest) // 2 - 6 pour essayer le param entre 0 et 1 par saut de 1/4
                    {
                        case 0 : CurPfp.pfp.setPosPad0 (0, 0, p);
                        case 1 : CurPfp.pfp.setPosPad1 (0, 0, p);
                        default: CurPfp.pfp.setPosPad2 (0, 0, p, (paramTest - 2f) / 4f);
                    }

                    mInfoString = "bal=" + Math.round((tUpdBal - tInitBal)/(1.0*nbMajBal))
                                + "ms " + "G=" + GPSTiming.nbEvents() + " "
                                + GPSTiming.meanPeriod()
                                + "ms" ;

                    break;

                default:
                    mInfoString = "bug ?"  + " G=" + GPSTiming.nbEvents();
                    break;
            }
            mInfoTextView.setText(mInfoString);

            String scoreString = String.format("%3d",
                    CurPfp.pfp.scoreBidon());
            mScoreTextView.setText(scoreString);

            String bestScoreString = String.format("%3d", CurPfp.pfp.bestScoreBidon());
            mBestScoreTextView.setText(bestScoreString);

            // TODO supprimer texte ci-dessous.
            paramTestText = String.format("%2d",paramTest);
            mParamTestButton.setText(paramTestText);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateUI(); // TODO vérifier que updateUI travaille dans ce cas du if
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        updateMyButtons();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        mCurrentLocation = location;
        GPSTiming.addEvent();
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void moveLeftButtonHandler(View view) {
        RemoteGameState gameState = RemoteGameState.getInstance();

        HVPoint newPosition = LocationManager.getInstance().getCurrentPosition();
        newPosition.H = (short) (newPosition.H - 400);

        //LocationManager.getInstance().setPosition(newPosition);
        CurPfp.pfp.setPosPad0(0, 0, newPosition);
        CurPfp.pfp.syncGameState();

        logServer("x=" + gameState.teams[0].players[0].x);
    }

    public void moveRightButtonHandler(View view) {
        RemoteGameState gameState = RemoteGameState.getInstance();

        HVPoint newPosition = LocationManager.getInstance().getCurrentPosition();
        newPosition.H = (short) (newPosition.H + 400);

        //LocationManager.getInstance().setPosition(newPosition);
        CurPfp.pfp.setPosPad0(0, 0, newPosition);
        CurPfp.pfp.syncGameState();

        logServer("x=" + gameState.teams[0].players[0].x);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);

    }
}
