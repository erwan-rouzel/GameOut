package fr.ecp.sio.gameout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import fr.ecp.sio.gameout.ads.InterstitialAdSingleton;
import fr.ecp.sio.gameout.utils.SharedPreferencesUtils;

public class SplashActivity extends ActionBarActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private InterstitialAd mInterstitialAd = null;
    private String pubLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pubLevel= SharedPreferencesUtils.readSharedSetting(getApplicationContext(),"AD_LEVEL","Ads free");

        if (pubLevel.equalsIgnoreCase("High")) {
            mInterstitialAd = InterstitialAdSingleton
                    .getInstance(getApplicationContext())
                    .getInterstitialAd();

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    mInterstitialAd.show();
                    finish();
                }
            });
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }


    }
}
