package fr.ecp.sio.gameout.salon;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import fr.ecp.sio.gameout.R;

/**
 * Created by ABDELHAFIZ on 02/02/2016.
 */
public class ParamActivity extends ActionBarActivity {
    public static String Nom_serveur,Niveau_pub,lissage_param;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);

        // Create an Adapter that holds a list
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.ads_level, R.layout.dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.server_name, R.layout.dropdown_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.anti_rebond, R.layout.dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                Niveau_pub = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "The pub level is " + Niveau_pub, Toast.LENGTH_LONG).show();

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                Nom_serveur = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "The server name is " + Nom_serveur, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                lissage_param = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "The lissage param is " + lissage_param, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
