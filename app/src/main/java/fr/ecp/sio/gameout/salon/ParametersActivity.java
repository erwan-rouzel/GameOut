package fr.ecp.sio.gameout.salon;

import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import fr.ecp.sio.gameout.R;

public class ParametersActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new ParametersFragment())
                .commit();

    }

    public static class ParametersFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
