package fr.ecp.sio.gameout.ads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import fr.ecp.sio.gameout.R;

/**
 * Created by cquenum on 27/01/16.
 */
public class InterstitialAdSingleton {
    private static final String TAG = InterstitialAdSingleton.class.getSimpleName();

    private static InterstitialAdSingleton ourInstance = null;
    private InterstitialAd mInterstitialAd = null;
    private Context mContext = null;

    public static InterstitialAdSingleton getInstance(Context context) {

        if (ourInstance == null)
            ourInstance = new InterstitialAdSingleton(context);
        return ourInstance;
    }

    private InterstitialAdSingleton(Context context) {
        mContext = context;
        mInterstitialAd = new InterstitialAd(mContext.getApplicationContext());
        String adUnitId = mContext.getResources().getString(R.string.fs_ads_unit_id);
        mInterstitialAd.setAdUnitId(adUnitId);
    }

    public InterstitialAd getInterstitialAd(){
        requestNewInterstitial();
        return mInterstitialAd;
    }

    private void requestNewInterstitial() {
        Log.d(TAG, "requestNewInterstitial");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
}
