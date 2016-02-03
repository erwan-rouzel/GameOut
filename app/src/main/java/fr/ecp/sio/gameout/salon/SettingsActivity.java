package fr.ecp.sio.gameout.salon;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import fr.ecp.sio.gameout.R;

public class SettingsActivity extends ActionBarActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    // Intent codes used in startActivityForResult
    private final static int RC_ACHIEVEMENTS = 10002;
    private final static int REQUEST_LEADERBOARD = 5001;

    // Google Api Client
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final AdView adView = (AdView) findViewById(R.id.banner);
        adView.loadAd(new AdRequest.Builder().build());

        if (GoogleApiClientSingleton.getInstance().isSignedIn())
            mGoogleApiClient = GoogleApiClientSingleton.getInstance().getApiClient();

        findViewById(R.id.leaderboard_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GoogleApiClientSingleton.getInstance().isSignedIn()) {
                    Intent intent = Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient);
                    startActivityForResult(intent, REQUEST_LEADERBOARD);
                }

            }
        });

        findViewById(R.id.achievements_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GoogleApiClientSingleton.getInstance().isSignedIn()) {
                    Intent intent = Games.Achievements.getAchievementsIntent(mGoogleApiClient);
                    startActivityForResult(intent, RC_ACHIEVEMENTS);
                }
            }
        });

        findViewById(R.id.credits_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);

            }
        });

        findViewById(R.id.parameters_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ParametersActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
