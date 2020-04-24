package njubainville1.nait.ca.eggcatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class ResultScreen extends AppCompatActivity {

    private InterstitialAd interstitial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        //Create the interstitial
        interstitial = new InterstitialAd(this);

        //Set your unit id, the following is a test id for development purposes
        interstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        //Create request
        AdRequest adRequest = new AdRequest.Builder().build();

        //Start loading...
        interstitial.loadAd(adRequest);

        //Once request is loaded
        interstitial.setAdListener(new AdListener()
        {
            public void onAdLoaded()
            {
                displayInterstitial();
            }
        });

        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);

        int score = getIntent().getIntExtra("SCORE", 0);
        scoreLabel.setText(score + "");

        int highScore = getIntent().getIntExtra("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);

    }

    public void displayInterstitial()
    {
        if (interstitial.isLoaded())
        {
            interstitial.show();
        }
    }

    public void tryAgain(View view)
    {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //Disable returning to the previous screen
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch(event.getKeyCode())
            {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
