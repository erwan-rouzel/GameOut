package fr.ecp.sio.gameout;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
