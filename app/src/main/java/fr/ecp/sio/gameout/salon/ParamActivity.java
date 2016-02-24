package fr.ecp.sio.gameout.salon;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

import fr.ecp.sio.gameout.R;
import fr.ecp.sio.gameout.utils.SharedPreferencesUtils;

/**
 * Created by ABDELHAFIZ on 02/02/2016.
 */
public class ParamActivity extends ActionBarActivity {
    public static String nomServeur, niveauPub, lissageParam;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);

        // Create an Adapter that holds a list
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.ads_level, R.layout.dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.server_name, R.layout.dropdown_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.anti_rebond, R.layout.dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);

            spinner1.setSelection(Integer.parseInt(SharedPreferencesUtils.readSharedSetting(getApplicationContext(), "AD_LEVEL_POS", "0")));
            spinner2.setSelection(Integer.parseInt(SharedPreferencesUtils.readSharedSetting(getApplicationContext(), "SERVEUR_NAME_POS", "0")));
            spinner3.setSelection(Integer.parseInt(SharedPreferencesUtils.readSharedSetting(getApplicationContext(), "LISSAGE_PARAM_POS", "0")));


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                niveauPub = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "The pub level is " + niveauPub, Toast.LENGTH_LONG).show();
                SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "AD_LEVEL", niveauPub);
                SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "AD_LEVEL_POS", String.valueOf(pos));

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                nomServeur = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "The server name is " + nomServeur, Toast.LENGTH_LONG).show();
                SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "SERVEUR_NAME", nomServeur);
                SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "SERVEUR_NAME_POS", String.valueOf(pos));

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                lissageParam = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "The lissage param is " + lissageParam, Toast.LENGTH_LONG).show();
                SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "LISSAGE_PARAM", lissageParam);
                SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "LISSAGE_PARAM_POS", String.valueOf(pos));

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "AD_LEVEL", niveauPub);
        SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "SERVEUR_NAME", nomServeur);
        SharedPreferencesUtils.saveSharedSetting(getApplicationContext(), "LISSAGE_PARAM", lissageParam);
    }
}
