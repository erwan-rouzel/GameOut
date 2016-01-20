package fr.ecp.sio.gameout.salon;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.InputStream;

import fr.ecp.sio.gameout.MainActivity;
import fr.ecp.sio.gameout.R;
import fr.ecp.sio.gameout.utils.GameoutUtils;

public class CreditsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        TextView creditsTxtView = (TextView) findViewById(R.id.credits_content);
        InputStream inputStream = getResources().openRawResource(R.raw.credits);
        creditsTxtView.setText(Html.fromHtml(GameoutUtils.readTxt(inputStream)));
        creditsTxtView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CreditsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
