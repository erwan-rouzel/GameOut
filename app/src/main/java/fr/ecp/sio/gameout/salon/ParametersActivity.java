package fr.ecp.sio.gameout.salon;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import fr.ecp.sio.gameout.R;

public class ParametersActivity extends PreferenceActivity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new ParametersFragment())
                .commit();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class ParametersFragment extends PreferenceFragment{
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            findPreference("pref_ads_level").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String adslevel = SP.getString("pref_ads_level", "NA");
                    Toast.makeText(getApplicationContext(), "Le niveau est " + adslevel, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}
